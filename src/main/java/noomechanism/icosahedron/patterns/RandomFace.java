package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronFixture;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.concurrent.ThreadLocalRandom;

public class RandomFace extends ColorPattern {

  protected int previousFace = -1;

  public DiscreteParameter fixture = new DiscreteParameter("fixture", 0, 0, 1);

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
    addParameter(fixture);
  }

  @Override
  public void onActive() {
    fixture.setRange(0, IcosahedronModel.fixtures.size());
  }

  @Override
  public void renderFrame(double deltaMs) {
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      LightBarRender1D.renderColor(colors, lb, LXColor.BLACK);
    }
    int whichFixture = fixture.getValuei();
    IcosahedronFixture fixture = IcosahedronModel.getFixture(whichFixture);

    int whichFace;
    do {
      whichFace = ThreadLocalRandom.current().nextInt(0, fixture.faces.length - 1);
    } while (whichFace == previousFace);

    IcosahedronFixture.Face f = fixture.faces[whichFace];
    for (LightBar lb : f.getLightBars()) {
      float maxValue = 1.0f;
      // If a bang is running, allow for a fade out over the number of bang frames.
      if (bangIsRunning()) {
        maxValue = bangFadeLevel();
      }
      LightBarRender1D.renderColor(colors, lb, getNewRGB(), maxValue);
    }
    previousFace = whichFace;
  }
}
