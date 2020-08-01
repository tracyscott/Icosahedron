package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.*;

import java.util.ArrayList;
import java.util.List;

public class BarSelector extends LXPattern {

  DiscreteParameter barNum = new DiscreteParameter("BarNum", 0, -1, IcosahedronModel.NUM_LIGHT_BARS);
  BooleanParameter caps = new BooleanParameter("caps", false).setDescription("Debugging caps");
  BooleanParameter directionCaps = new BooleanParameter("dircaps", false).setDescription("Direction caps");
  BooleanParameter joint = new BooleanParameter("joint", false).setDescription("Color code the joint");
  BooleanParameter showNum = new BooleanParameter("showNum", false).setDescription("Encode bar number on bar");

  public BarSelector(LX lx) {
    super(lx);
    addParameter(barNum);
    addParameter(caps);
    addParameter(directionCaps);
    addParameter(joint);
    addParameter(showNum);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      for (LXPoint point : lb.points) {
        colors[point.index] = LXColor.rgba(0, 0, 0, 255);
      }
    }
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      if (lb.barNum == barNum.getValuei() || barNum.getValuei() == -1) {
        int ptNum = 0;
        for (LXPoint point: lb.points) {
          if ((caps.getValueb() && ptNum == 0) ||
              (directionCaps.getValueb() && ptNum < 10))
            colors[point.index] = LXColor.rgba(255, 0, 0, 255);
          else if (caps.getValueb() && (ptNum == 1 || ptNum == lb.points.size() - 2))
            colors[point.index] = LXColor.rgba(0, 0, 255, 255);
          else if (caps.getValueb() && (ptNum == lb.points.size() - 1) ||
              (directionCaps.getValueb() && ptNum > lb.points.size() - 10))
            colors[point.index] = LXColor.rgba(0, 255, 0, 255);
          else
            colors[point.index] = LXColor.rgba(255, 255, 255, 255);
          ptNum++;
        }
        if (showNum.getValueb()) renderBarNum(lb);
        if (joint.getValueb()) {
          // Color code the joint connections.
          LightBarRender1D.renderColor(colors, lb.edge.myStartPointJoints[0].edge.lightBar,
              255, 0, 0, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myStartPointJoints[1].edge.lightBar,
              0, 255, 0, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myStartPointJoints[2].edge.lightBar,
              0, 0, 255, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myStartPointJoints[3].edge.lightBar,
              255, 255, 0, 255);

          LightBarRender1D.renderColor(colors, lb.edge.myEndPointJoints[0].edge.lightBar,
              255, 0, 0, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myEndPointJoints[1].edge.lightBar,
              0, 255, 0, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myEndPointJoints[2].edge.lightBar,
              0, 0, 255, 255);
          LightBarRender1D.renderColor(colors, lb.edge.myEndPointJoints[3].edge.lightBar,
              255, 255, 0, 255);
        }
      } else {

      }
    }
  }

  public void renderBarNum(LightBar lb) {
    int numFiveBlobs = lb.barNum / 5;
    int numOneBlobs = lb.barNum % 5;
    List<Blob> fiveBlobs = new ArrayList<Blob>();
    for (int i = 0; i < numFiveBlobs; i++) {
      Blob blob = new Blob();
      blob.dlb = new DirectionalLightBar(lb.barNum, true);
      blob.pos = (20.0f + i * 15.0f)/(float)lb.numPoints;
      fiveBlobs.add(blob);
    }

    List<Blob> oneBlobs = new ArrayList<Blob>();
    for (int i = 0; i < numOneBlobs; i++) {
      Blob blob = new Blob();
      blob.dlb = new DirectionalLightBar(lb.barNum, true);
      blob.pos = (20.0f + 15.0f * numFiveBlobs + 8.0f * i)/(float)lb.numPoints;
      oneBlobs.add(blob);
    }

    for (Blob b : fiveBlobs) {
      b.renderBlob(colors, 0f, 10.f/(float)lb.numPoints, 50.0f, 1.0f, 1,
          0, false, LXColor.Blend.SUBTRACT, 0, 0f, 0f);
    }

    for (Blob b : oneBlobs) {
      b.renderBlob(colors, 0f, 4f/(float)lb.numPoints, 50.0f, 1.0f, 1,
          0, false, LXColor.Blend.SUBTRACT, 0, 0f, 0f);
    }
  }
}
