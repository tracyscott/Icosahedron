package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.*;

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
  public DiscreteParameter waveKnob = new DiscreteParameter("wave", 0, 0, 3).setDescription("Waveform type");
  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");

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
    resetBlobs();
  }

  public void resetBlobs() {
    for (int i = 0; i < MAX_BLOBS; i++) {
      blobs[i] = new Blob();
      blobs[i].reset(i%30, 0.0f, randSpeed.getValuef(), true);
    }
  }

  /**
   * onActive is called when the pattern starts playing and becomes the active pattern.  Here we re-assigning
   * our speeds to generate some randomness in the speeds.
   */
  @Override
  public void onActive() {
    resetBlobs();
  }

  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }

    for (int i = 0; i < numBlobs.getValuei(); i++) {
      blobs[i].renderBlob(colors, speed.getValuef(), widthKnob.getValuef(), slope.getValuef(), maxValue.getValuef(),
          waveKnob.getValuei(), nextBarKnob.getValuei(), false);
    }
  }
}
