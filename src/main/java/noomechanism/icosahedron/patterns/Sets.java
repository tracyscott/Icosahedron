package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.BarSets;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.List;

public class Sets extends ColorPattern {

  public DiscreteParameter whichSet = new DiscreteParameter("set", 0, BarSets.allSets.length);
  public Sets(LX lx) {
    super(lx);
    addParameter(fpsKnob);
    addParameter(whichSet);
    addParameter(paletteKnob);
    addParameter(randomPaletteKnob);
    addParameter(hue);
    addParameter(saturation);
    addParameter(bright);
  }

  @Override
  public void renderFrame(double deltaMs) {
    clearLightBarsToBlack();
    int color = getNewRGB();
    List<LightBar> lbs = BarSets.getSet(whichSet.getValuei());
    for (LightBar lb: lbs) {
      LightBarRender1D.renderColor(colors, lb, color);
    }
  }


}
