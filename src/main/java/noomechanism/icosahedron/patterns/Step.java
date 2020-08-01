package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class Step extends LXPattern {
  public CompoundParameter pos = new CompoundParameter("pos", 0.0, 0.0, 1.0);
  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 40.0);
  public CompoundParameter maxV = new CompoundParameter("maxv", 1.0, 0.0, 1.0);
  public BooleanParameter forward = new BooleanParameter("fwd", true);

  public Step(LX lx) {
    super(lx);
    addParameter(pos);
    addParameter(slope);
    addParameter(maxV);
    addParameter(forward);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      LightBarRender1D.renderStepDecay(colors, lb, pos.getValuef(), 0.2f, slope.getValuef(), maxV.getValuef(), forward.getValueb(), LXColor.Blend.ADD);
    }
  }
}
