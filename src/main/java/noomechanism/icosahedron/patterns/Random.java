package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class Random extends LXPattern {

  public Random(LX lx) {
    super(lx);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      LightBarRender1D.randomGray(colors, lb);
    }
  }
}
