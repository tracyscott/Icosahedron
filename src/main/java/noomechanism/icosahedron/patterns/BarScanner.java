package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class BarScanner extends LXPattern {
  public CompoundParameter pos = new CompoundParameter("pos", 0.0, 0.0, 1.0);
  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);

  public BarScanner(LX lx) {
    super(lx);
    addParameter(pos);
    addParameter(slope);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      LightBarRender1D.renderTriangle(colors, lb, pos.getValuef(), slope.getValuef());
    }
  }
}
