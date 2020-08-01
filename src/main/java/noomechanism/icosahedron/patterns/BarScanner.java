package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class BarScanner extends LXPattern {
  public CompoundParameter pos = new CompoundParameter("pos", 0.0, 0.0, 1.0);
  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter maxValue = new CompoundParameter("maxv", 1.0, 0.0, 1.0);

  public BarScanner(LX lx) {
    super(lx);
    addParameter(pos);
    addParameter(slope);
    addParameter(maxValue);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      LightBarRender1D.renderTriangle(colors, lb, pos.getValuef(), slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
      LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
    }
  }
}
