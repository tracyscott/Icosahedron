package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

public class BarSelector extends LXPattern {

  DiscreteParameter barNum = new DiscreteParameter("BarNum", 0, 0, IcosahedronModel.NUM_LIGHT_BARS);

  public BarSelector(LX lx) {
    super(lx);
    addParameter(barNum);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      if (lb.barNum == barNum.getValuei()) {
        for (LXPoint point: lb.points) {
          colors[point.index] = LXColor.rgba(255, 255, 255, 255);
        }
      } else {
        for (LXPoint point: lb.points) {
          colors[point.index] = LXColor.rgba(0, 0, 0, 255);
        }
      }
    }
  }
}
