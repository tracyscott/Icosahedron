package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.concurrent.ThreadLocalRandom;

public class RandomFace extends ColorPattern {

  public RandomFace(LX lx) {
    super(lx);
    addParameter(fpsKnob);
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

    int whichFace = ThreadLocalRandom.current().nextInt(0, IcosahedronModel.faces.length - 1);
    IcosahedronModel.Face f = IcosahedronModel.faces[whichFace];
    for (LightBar lb : f.getLightBars()) {
      LightBarRender1D.renderColor(colors, lb, getNewRGB());
    }
  }
}
