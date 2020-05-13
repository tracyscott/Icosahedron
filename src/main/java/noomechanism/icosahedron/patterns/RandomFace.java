package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.concurrent.ThreadLocalRandom;

public class RandomFace extends ColorPattern {

  protected int previousFace = -1;

  public RandomFace(LX lx) {
    super(lx);
    addParameter(fpsKnob);
    addParameter(bangOn);
    addParameter(bangFrames);
    addParameter(bangFade);
    addParameter(bangClear);
    addParameter(fbang);
    addParameter(paletteKnob);
    addParameter(randomPaletteKnob);
    addParameter(hue);
    addParameter(saturation);
    addParameter(bright);
  }

  @Override
  public void renderFrame(double deltaMs) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      LightBarRender1D.renderColor(colors, lb, LXColor.BLACK);
    }

    int whichFace;
    do {
      whichFace = ThreadLocalRandom.current().nextInt(0, IcosahedronModel.faces.length - 1);
    } while (whichFace == previousFace);

    IcosahedronModel.Face f = IcosahedronModel.faces[whichFace];
    for (LightBar lb : f.getLightBars()) {
      float maxValue = 1.0f;
      // If a bang is running, allow for a fade out over the number of bang frames.
      if (bangIsRunning()) {
        maxValue = 1f - ((float)currentBangFrames/(float)(bangFrames.getValuei()-1f) * bangFade.getValuef());
      }
      LightBarRender1D.renderColor(colors, lb, getNewRGB(), maxValue);
    }
    previousFace = whichFace;
  }
}
