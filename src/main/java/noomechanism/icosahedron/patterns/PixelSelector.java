package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

public class PixelSelector extends LXPattern {

  DiscreteParameter barNum = new DiscreteParameter("BarNum", 0, 0, IcosahedronModel.NUM_LIGHT_BARS);
  DiscreteParameter pixNum = new DiscreteParameter("PixNum", 0, 0, 150);
  BooleanParameter caps = new BooleanParameter("caps", false).setDescription("Debugging caps");

  public PixelSelector(LX lx) {
    super(lx);
    addParameter(barNum);
    addParameter(pixNum);
    addParameter(caps);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      if (lb.barNum == barNum.getValuei()) {
        int ptNum = 0;
        for (LXPoint point: lb.points) {
          if (caps.getValueb() && ptNum == 0)
            colors[point.index] = LXColor.rgba(255, 0, 0, 255);
          else if (caps.getValueb() && (ptNum == 1 || ptNum == lb.points.size() - 2))
            colors[point.index] = LXColor.rgba(0, 0, 255, 255);
          else if (caps.getValueb() && (ptNum == lb.points.size() - 1))
            colors[point.index] = LXColor.rgba(0, 255, 0, 255);
          else if (ptNum == pixNum.getValuei())
            colors[point.index] = LXColor.rgba(255, 255, 255, 255);
          else
            colors[point.index] = LXColor.rgba(0, 0, 0, 255);
          ptNum++;
        }
      } else {
        for (LXPoint point: lb.points) {
          colors[point.index] = LXColor.rgba(0, 0, 0, 255);
        }
      }
    }
  }
}
