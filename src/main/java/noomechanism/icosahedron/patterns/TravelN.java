package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;
import noomechanism.icosahedron.DirectionalLightBar;

import java.util.ArrayList;
import java.util.List;

public class TravelN extends LXPattern {
  public static final int MAX_BLOBS = 100;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter maxValue = new CompoundParameter("maxv", 1.0, 0.0, 1.0);
  public CompoundParameter speed = new CompoundParameter("speed", 1.0, 0.0, 10.0);
  public CompoundParameter randSpeed = new CompoundParameter("randspd", 1.0, 0.0, 5.0);
  public DiscreteParameter numBlobs = new DiscreteParameter("blobs", 1, 1, MAX_BLOBS);
  public BooleanParameter sparkle = new BooleanParameter("sparkle", true);
  public DiscreteParameter nextBarKnob = new DiscreteParameter("nxtBar", -1, -1, 4);
  public CompoundParameter sparkleMin = new CompoundParameter("spklMin", 0.0f, 0.0f, 255.0f);
  public CompoundParameter sparkleDepth = new CompoundParameter("spklDepth", 255.0f, 0.0f, 255.0f);
  public BooleanParameter waveKnob = new BooleanParameter("triWave", true).setDescription("Render triangle wave");
  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");

  public static class Blob {
    public DirectionalLightBar dlb;
    public float pos = 0f;
    public float speed = 1f;
    List<DirectionalLightBar> prevBars;
    List<DirectionalLightBar> nextBars;

    public void updateCurrentBar(int barSelector) {
      // First, lets transfer the current lightbar into our
      // previous lightbars list.  The list will be trimmed in our draw loop.
      prevBars.add(0, dlb);
      // Next the current lightbar should come from the beginning of the nextBars
      // The nextBars list will be filled out in the draw loop if necessary.
      if (nextBars.size() > 0)
        dlb = nextBars.remove(0);
      else
        dlb = dlb.chooseNextBar(barSelector);

      // Set or position based on the directionality of the current lightbar.
      if (dlb.forward) {
        pos = 0.0f;
      } else {
        pos = 1.0f;
      }
    }
  }

  public Blob[] blobs = new Blob[MAX_BLOBS];

  public TravelN(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(maxValue);
    addParameter(speed);
    addParameter(numBlobs);
    addParameter(randSpeed);
    addParameter(sparkle);
    addParameter(nextBarKnob);
    addParameter(sparkleMin);
    addParameter(sparkleDepth);
    addParameter(waveKnob);
    addParameter(widthKnob);
    resetBars();
  }

  public void resetBars() {
    for (int i = 0; i < MAX_BLOBS; i++) {
      blobs[i] = new Blob();
      blobs[i].pos = (float)Math.random();
      blobs[i].dlb = new DirectionalLightBar(i % 30, true);
      float randSpeedOffset = randSpeed.getValuef() * (float)Math.random();
      blobs[i].speed = randSpeedOffset;
      blobs[i].nextBars = new ArrayList<DirectionalLightBar>();
      blobs[i].prevBars = new ArrayList<DirectionalLightBar>();
    }
  }

  /**
   * onActive is called when the pattern starts playing and becomes the active pattern.  Here we re-assigning
   * our speeds to generate some randomness in the speeds.
   */
  @Override
  public void onActive() {
    resetBars();
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }

    for (int i = 0; i < numBlobs.getValuei(); i++) {
      Blob blob = blobs[i];
      boolean needsCurrentBarUpdate = false;
      for (LightBar lb : IcosahedronModel.lightBars) {
        if (blob.dlb.lb.barNum == lb.barNum) {
          // -- Render on our target light bar --
          float minMax[] = renderWaveform(lb, blob.pos);

          // -- Fix up the set of lightbars that we are rendering over.
          int numPrevBars = -1 * (int)Math.floor(minMax[0]);
          int numNextBars = (int)Math.ceil(minMax[1] - 1.0f);
          if (!blob.dlb.forward) {
            int oldNumNextBars = numNextBars;
            numNextBars = numPrevBars;
            numPrevBars = oldNumNextBars;
          }
          // We need to handle the initial case, so we might need to add multiple next bars to our list.
          while (blob.nextBars.size() < numNextBars) {
            DirectionalLightBar dlb;
            if (blob.nextBars.size() == 0)
              dlb = blob.dlb.chooseNextBar(nextBarKnob.getValuei());
            else
              dlb = blob.nextBars.get(blob.nextBars.size() - 1).chooseNextBar(nextBarKnob.getValuei());
            blob.nextBars.add(dlb);
          }

          /*
          while (blob.prevBars.size() < numPrevBars) {
            DirectionalLightBar dlb;
            if (blob.prevBars.size() == 0)
              dlb = blob.dlb.choosePrevBar(nextBarKnob.getValuei());
            else
              dlb = blob.prevBars.get(blob.prevBars.size() - 1).choosePrevBar(nextBarKnob.getValuei());
            blob.prevBars.add(dlb);
          }
          */

          // Garbage collect any old bars.
          // TODO(tracy): We should trim both nextBars and prevBars each time so for example if our slope changes
          // dynamically, we might want to reduce our prevBars and nextBars list.  It is only an optimization since
          // we will just render black in ADD mode which should have no effect but is just inefficient.
          if (blob.prevBars.size() > numPrevBars) {
            blob.prevBars.remove(blob.prevBars.size() - 1);
          }

          // For the number of previous bars, render on each bar
          for (int j = 0; j < numPrevBars && j < blob.prevBars.size(); j++) {
            DirectionalLightBar prevBar = blob.prevBars.get(j);
            // We need to compute the next bar pos but we need to account for any intermediate bars.
            float prevBarPos = blob.dlb.computePrevBarPos(blob.pos, prevBar);
            // LightBar lengths are normalized to 1.0, so we need to shift our compute distance based on
            // whether there are any intermediate lightbars.
            if (prevBar.forward) prevBarPos += j;
            else prevBarPos -= j; //
            renderWaveform(prevBar.lb, prevBarPos);
          }

          for (int j = 0; j < numNextBars; j++) {
            DirectionalLightBar nextBar = blob.nextBars.get(j);
            float nextBarPos = blob.dlb.computeNextBarPos(blob.pos, nextBar);
            if (nextBar.forward)
              nextBarPos -= j; // shift the position to the left by the number of bars away it is actually at.
            else
              nextBarPos += j;
            renderWaveform(nextBar.lb, nextBarPos);
          }

          if (blob.dlb.forward) {
            blob.pos += speed.getValuef()/100f + blob.speed / 100f;
          } else {
            blob.pos -= speed.getValuef()/100f + blob.speed / 100f;
          }

          if (blob.pos <= 0.0 || blob.pos >= 1.0f) {
            needsCurrentBarUpdate = true;
          }
        }
      }

      if (needsCurrentBarUpdate) {
        blob.updateCurrentBar(nextBarKnob.getValuei());
      }
    }
    if (sparkle.getValueb()) {
      for (LightBar lb : IcosahedronModel.lightBars) {
        LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
      }
    }
  }

  public float[] renderWaveform(LightBar lb, float position) {
    if (waveKnob.getValueb())
      return LightBarRender1D.renderTriangle(colors, lb, position, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
    else
      return LightBarRender1D.renderSquare(colors, lb, position, widthKnob.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
  }
}
