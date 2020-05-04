package noomechanism.icosahedron;

import heronarts.lx.color.LXColor;

import java.util.ArrayList;
import java.util.List;

public class Blob {
  public DirectionalLightBar dlb;
  public float pos = 0f;
  public float speed = 1f;
  public List<DirectionalLightBar> prevBars;
  public List<DirectionalLightBar> nextBars;

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

  public void reset(int lightBarNum, float initialPos, float randomSpeed) {
    pos = initialPos;
    dlb = new DirectionalLightBar(lightBarNum, true);
    speed = randomSpeed * (float)Math.random();
    nextBars = new ArrayList<DirectionalLightBar>();
    prevBars = new ArrayList<DirectionalLightBar>();
  }

  /**
   * Renders a 'blob'.  Could we a number of different 'waveforms' centered at the current
   * position.  Position will be incremented by baseSpeed + the blobs random speed component.
   * This method will handle making sure there are enough DirectionalLightBars so that the
   * waveform can be rendered across multiple lightbars.
   * @param colors
   * @param baseSpeed
   * @param width
   * @param slope
   * @param maxValue
   * @param waveform
   * @param whichJoint
   */
  public void renderBlob(int[] colors, float baseSpeed, float width, float slope,
                         float maxValue, int waveform, int whichJoint, boolean initialTail) {
    boolean needsCurrentBarUpdate = false;
    for (LightBar lb : IcosahedronModel.lightBars) {
      if (dlb.lb.barNum == lb.barNum) {
        // -- Render on our target light bar --
        float minMax[] = renderWaveform(colors, dlb, pos, width, slope, maxValue, waveform);

        // -- Fix up the set of lightbars that we are rendering over.
        int numPrevBars = -1 * (int)Math.floor(minMax[0]);
        int numNextBars = (int)Math.ceil(minMax[1] - 1.0f);
        if (!dlb.forward) {
          int oldNumNextBars = numNextBars;
          numNextBars = numPrevBars;
          numPrevBars = oldNumNextBars;
        }
        // We need to handle the initial case, so we might need to add multiple next bars to our list.
        while (nextBars.size() < numNextBars) {
          DirectionalLightBar nextDlb;
          if (nextBars.size() == 0)
            nextDlb = dlb.chooseNextBar(whichJoint);
          else
            nextDlb = nextBars.get(nextBars.size() - 1).chooseNextBar(whichJoint);
          nextBars.add(nextDlb);
        }

        // Pre-populate the previous lightbars if we want an initial tail.  Otherwise
        // these will be populated as we update the current bar to the next bar.
        while (initialTail && (prevBars.size() < numPrevBars)) {
          DirectionalLightBar prevDlb;
          if (prevBars.size() == 0)
            prevDlb = dlb.choosePrevBar(whichJoint);
          else
            prevDlb = prevBars.get(prevBars.size() - 1).choosePrevBar(whichJoint);
          prevBars.add(prevDlb);
        }

        // Garbage collect any old bars.
        // TODO(tracy): We should trim both nextBars and prevBars each time so for example if our slope changes
        // dynamically, we might want to reduce our prevBars and nextBars list.  It is only an optimization since
        // we will just render black in ADD mode which should have no effect but is just inefficient.
        if (prevBars.size() > numPrevBars) {
          prevBars.remove(prevBars.size() - 1);
        }

        // For the number of previous bars, render on each bar
        for (int j = 0; j < numPrevBars && j < prevBars.size(); j++) {
          DirectionalLightBar prevBar = prevBars.get(j);
          // We need to compute the next bar pos but we need to account for any intermediate bars.
          float prevBarPos = dlb.computePrevBarPos(pos, prevBar);
          // LightBar lengths are normalized to 1.0, so we need to shift our compute distance based on
          // whether there are any intermediate lightbars.
          if (prevBar.forward) prevBarPos += j;
          else prevBarPos -= j; //
          renderWaveform(colors, prevBar, prevBarPos, width, slope, maxValue, waveform);
        }

        for (int j = 0; j < numNextBars; j++) {
          DirectionalLightBar nextBar = nextBars.get(j);
          float nextBarPos = dlb.computeNextBarPos(pos, nextBar);
          if (nextBar.forward)
            nextBarPos -= j; // shift the position to the left by the number of bars away it is actually at.
          else
            nextBarPos += j;
          renderWaveform(colors, nextBar, nextBarPos, width, slope, maxValue, waveform);
        }

        if (dlb.forward) {
          pos += (baseSpeed + speed)/100f;
        } else {
          pos -= (baseSpeed + speed)/100f;
        }

        if (pos <= 0.0 || pos >= 1.0f) {
          needsCurrentBarUpdate = true;
        }
      }
    }

    if (needsCurrentBarUpdate) {
      updateCurrentBar(whichJoint);
    }
  }

  public float[] renderWaveform(int[] colors, DirectionalLightBar targetDlb, float position, float width, float slope,
                                float maxValue, int waveform) {
    if (waveform == 0)
      return LightBarRender1D.renderTriangle(colors, targetDlb.lb, position, slope, maxValue, LXColor.Blend.ADD);
    else if (waveform == 1)
      return LightBarRender1D.renderSquare(colors, targetDlb.lb, position, width, maxValue, LXColor.Blend.ADD);
    else
      return LightBarRender1D.renderStepDecay(colors, targetDlb.lb, position, width, slope,
          maxValue, targetDlb.forward, LXColor.Blend.ADD);
  }
}
