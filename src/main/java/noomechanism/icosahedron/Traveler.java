package noomechanism.icosahedron;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

import java.util.concurrent.ThreadLocalRandom;

public class Traveler extends LXPattern {
  public CompoundParameter pos = new CompoundParameter("pos", 0.0, 0.0, 1.0);
  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter maxValue = new CompoundParameter("maxv", 1.0, 0.0, 1.0);
  public CompoundParameter speed = new CompoundParameter("speed", 1.0, 0.0, 10.0);

  public int currentBarNum = 0;
  public float step;
  public boolean forward = true;

  public Traveler(LX lx) {
    super(lx);
    addParameter(pos);
    addParameter(slope);
    addParameter(maxValue);
    addParameter(speed);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    int newCurrentLightBarNum = -1;
    for (LightBar lb : IcosahedronModel.lightBars) {
      if (currentBarNum == lb.barNum) {
        LightBarRender1D.renderTriangle(colors, lb, pos.getValuef(), slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
        LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
        float p = pos.getValuef();
        if (forward) {
          p += speed.getValuef() / 100f;
        } else {
          p -= speed.getValuef() / 100f;
        }
        // If we are going off the end of our start point, look for a new lightbar
        if (p <= 0.0) {
          int jointNum = ThreadLocalRandom.current().nextInt(5);
          IcosahedronModel.Edge nextEdge = IcosahedronModel.edges[currentBarNum].myStartPointJoints[jointNum].edge;
          forward = IcosahedronModel.edges[currentBarNum].myStartPointJoints[jointNum].isAdjacentEdgeAStartPoint;
          newCurrentLightBarNum = nextEdge.lightBar.barNum;
          if (forward) {
            p = 0.0f;
          } else {
            p = 1.0f;
          }
        } else if (p >= 1.0f) {
          // We are going off the end of our end point, look for a new lightbar.
          int jointNum = ThreadLocalRandom.current().nextInt(5);
          IcosahedronModel.Edge nextEdge = IcosahedronModel.edges[currentBarNum].myEndPointJoints[jointNum].edge;
          forward = IcosahedronModel.edges[currentBarNum].myEndPointJoints[jointNum].isAdjacentEdgeAStartPoint;
          newCurrentLightBarNum = nextEdge.lightBar.barNum;
          if (forward) {
            p = 0.0f;
          } else {
            p = 1.0f;
          }
        }
        pos.setValue(p);
      }
    }
    if (newCurrentLightBarNum != -1) {
      currentBarNum = newCurrentLightBarNum;
    }
  }
}
