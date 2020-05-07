package noomechanism.icosahedron;

import heronarts.lx.color.LXColor;

import java.util.ArrayList;
import java.util.List;

public class Blob {
  public DirectionalLightBar dlb;
  public float pos = 0f;
  public float speed = 1f;
  public List<DirectionalLightBar> prevBars = new ArrayList<DirectionalLightBar>();
  public List<DirectionalLightBar> nextBars = new ArrayList<DirectionalLightBar>();

  // When rendering position parametrically from 0 to 1, we need a pre-computed set of lightbars
  // that we intend to render on.  See TopBottomT for an example of setting this up.
  public List<DirectionalLightBar> pathBars;

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

  public void reset(int lightBarNum, float initialPos, float randomSpeed, boolean forward) {
    pos = initialPos;
    dlb = new DirectionalLightBar(lightBarNum, forward);
    speed = randomSpeed * (float)Math.random();
    nextBars = new ArrayList<DirectionalLightBar>();
    prevBars = new ArrayList<DirectionalLightBar>();
  }

  public void renderBlob(int[] colors, float baseSpeed, float width, float slope,
                         float maxValue, int waveform, int whichJoint, boolean initialTail) {
    renderBlob(colors, baseSpeed, width, slope, maxValue, waveform, whichJoint, initialTail, LXColor.Blend.ADD);
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
                         float maxValue, int waveform, int whichJoint, boolean initialTail, LXColor.Blend blend) {
    boolean needsCurrentBarUpdate = false;
    for (LightBar lb : IcosahedronModel.lightBars) {
      if (dlb.lb.barNum == lb.barNum) {
        // -- Render on our target light bar --
        float minMax[] = renderWaveform(colors, dlb, pos, width, slope, maxValue, waveform, blend);

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
          renderWaveform(colors, prevBar, prevBarPos, width, slope, maxValue, waveform, blend);
        }

        for (int j = 0; j < numNextBars; j++) {
          DirectionalLightBar nextBar = nextBars.get(j);
          float nextBarPos = dlb.computeNextBarPos(pos, nextBar);
          if (nextBar.forward)
            nextBarPos -= j; // shift the position to the left by the number of bars away it is actually at.
          else
            nextBarPos += j;
          renderWaveform(colors, nextBar, nextBarPos, width, slope, maxValue, waveform, blend);
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
  public void renderBlobAtT(int[] colors, float paramT, float width, float slope,
                            float maxValue, int waveform, float maxGlobalPos) {
    renderBlobAtT(colors, paramT, width, slope, maxValue, waveform, 0f, maxGlobalPos);
  }

  /**
   * Renders a waveform on a pre-computed list of lightbars stored in pathBars.  Position is
   * defined parametrically from 0 to 1 where 1 is at the end of the last lightbar.  Automatically
   * adjusts to number of lightbars.
   * @param colors
   * @param paramT
   * @param width
   * @param slope
   * @param maxValue
   * @param waveform
   */
    public void renderBlobAtT(int[] colors, float paramT, float width, float slope,
    float maxValue, int waveform, float startMargin, float maxGlobalPos) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      int dlbNum = 0;
      for (DirectionalLightBar currentDlb : pathBars) {
        if (currentDlb.lb.barNum == lb.barNum && !currentDlb.disableRender) {
          // -- Render on our target light bar and adjust pos based on bar num.
          float localDlbPos = paramT * (maxGlobalPos + startMargin) - startMargin;
          localDlbPos -= dlbNum;
          if (!currentDlb.forward)
            localDlbPos = 1.0f - localDlbPos;

          renderWaveform(colors, currentDlb, localDlbPos, width, slope, maxValue, waveform, LXColor.Blend.ADD);
        }
        dlbNum++;
      }
    }
  }

  public float[] renderWaveform(int[] colors, DirectionalLightBar targetDlb, float position, float width, float slope,
                                float maxValue, int waveform, LXColor.Blend blend) {
    if (waveform == 0)
      return LightBarRender1D.renderTriangle(colors, targetDlb.lb, position, slope, maxValue, blend);
    else if (waveform == 1)
      return LightBarRender1D.renderSquare(colors, targetDlb.lb, position, width, maxValue, blend);
    else
      return LightBarRender1D.renderStepDecay(colors, targetDlb.lb, position, width, slope,
          maxValue, targetDlb.forward, LXColor.Blend.ADD);
  }
}
