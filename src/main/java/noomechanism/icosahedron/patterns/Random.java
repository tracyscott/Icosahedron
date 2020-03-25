package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class Random extends LXPattern {

  public Random(LX lx) {
    super(lx);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    for (LightBar lb : IcosahedronModel.lightBars) {
      LightBarRender1D.randomGray(colors, lb, LXColor.Blend.ADD);
    }
  }
}
