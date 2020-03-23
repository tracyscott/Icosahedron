package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.Icosahedron;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

public class FaceSelector extends LXPattern {

  DiscreteParameter faceNum = new DiscreteParameter("Face", 0, 0, 20);

  public FaceSelector(LX lx) {
    super(lx);
    addParameter(faceNum);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint point : lx.getModel().points) {
      colors[point.index] = LXColor.rgba(0, 0, 0, 255);
    }

    for (IcosahedronModel.Face f : IcosahedronModel.faces) {
      if (f == null) continue;
      if (f.faceNum == faceNum.getValuei()) {
        for (LightBar lb : f.getLightBars()) {
          for (LXPoint point : lb.points) {
            colors[point.index] = LXColor.rgba(255, 255, 255, 255);
          }
        }
      }
    }
  }
}
