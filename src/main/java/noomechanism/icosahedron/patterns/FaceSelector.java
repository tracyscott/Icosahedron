package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronFixture;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

public class FaceSelector extends LXPattern {

  DiscreteParameter faceNum = new DiscreteParameter("Face", 0, 0, 20);
  BooleanParameter adjacent = new BooleanParameter("adj", false);

  public FaceSelector(LX lx) {
    super(lx);
    addParameter(faceNum);
    addParameter(adjacent);
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint point : lx.getModel().points) {
      colors[point.index] = LXColor.rgba(0, 0, 0, 255);
    }

    for (IcosahedronFixture.Face f : IcosahedronModel.smallIcosahedron.faces) {
      if (f == null) continue;
      if ((f.faceNum == faceNum.getValuei() && !adjacent.getValueb()) ||
          (f.faceNum == faceNum.getValuei() || (adjacent.getValueb() && f.isFaceIdAdjacent(faceNum.getValuei(), IcosahedronModel.smallIcosahedron.faces))))
          {
        for (LightBar lb : f.getLightBars()) {
          for (LXPoint point : lb.points) {
            colors[point.index] = LXColor.rgba(255, 255, 255, 255);
          }
        }
      }
    }
  }
}
