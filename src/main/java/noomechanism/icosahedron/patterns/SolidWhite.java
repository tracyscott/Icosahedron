package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXLayer;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.transform.LXProjection;
import heronarts.lx.transform.LXVector;

import static java.lang.Math.abs;

public class SolidWhite extends LXPattern {

  public SolidWhite(LX lx) {
    super(lx);
  }

  public void run(double deltaMs) {
    int i = 0;
    for (LXPoint p : model.points) {
      colors[p.index] = LXColor.WHITE;
      ++i;
    }
  }
}