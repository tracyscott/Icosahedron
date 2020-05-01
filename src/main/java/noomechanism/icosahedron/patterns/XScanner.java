package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class XScanner extends LXPattern {
  public CompoundParameter thickness = new CompoundParameter("thck", 1.0, 0.0, 5.0);
  public CompoundParameter pos = new CompoundParameter("xPos", 0.0, -5.0, 5.0);

  public XScanner(LX lx) {
    super(lx);
    addParameter(thickness);
    addParameter(pos);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      if (pt.x < pos.getValuef() + thickness.getValuef()/2f &&
          pt.x > pos.getValuef() - thickness.getValuef()/2f) {
        colors[pt.index] = LXColor.rgba(255, 255, 255, 255);
      } else {
        colors[pt.index] = LXColor.rgba(0, 0, 0, 255);
      }
    }
  }
}
