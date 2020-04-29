package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.Icosahedron;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

public class BarSelector extends LXPattern {

  DiscreteParameter barNum = new DiscreteParameter("BarNum", 0, -1, IcosahedronModel.NUM_LIGHT_BARS);
  BooleanParameter caps = new BooleanParameter("caps", false).setDescription("Debugging caps");
  BooleanParameter directionCaps = new BooleanParameter("dircaps", false).setDescription("Direction caps");
  BooleanParameter joint = new BooleanParameter("joint", false).setDescription("Color code the joint");

  public BarSelector(LX lx) {
    super(lx);
    addParameter(barNum);
    addParameter(caps);
    addParameter(directionCaps);
    addParameter(joint);
  }

  @Override
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.lightBars) {
      for (LXPoint point : lb.points) {
        colors[point.index] = LXColor.rgba(0, 0, 0, 255);
      }
    }
    for (LightBar lb : IcosahedronModel.lightBars) {
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
        if (joint.getValueb()) {
          // Color code the joint connections.
          int firstBarNum = IcosahedronModel.edges[lb.barNum].myStartPointJoints[0].edge.lightBar.barNum;
          LightBar jlb = IcosahedronModel.lightBars.get(firstBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(255, 0, 0, 255);
          }
          int secondBarNum = IcosahedronModel.edges[lb.barNum].myStartPointJoints[1].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(secondBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(0, 255, 0, 255);
          }
          int thirdBarNum = IcosahedronModel.edges[lb.barNum].myStartPointJoints[2].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(thirdBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(0, 0, 255, 255);
          }
          int fourthBarNum = IcosahedronModel.edges[lb.barNum].myStartPointJoints[3].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(fourthBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(255, 255, 0, 255);
          }

          firstBarNum = IcosahedronModel.edges[lb.barNum].myEndPointJoints[0].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(firstBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(255, 0, 0, 255);
          }
          secondBarNum = IcosahedronModel.edges[lb.barNum].myEndPointJoints[1].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(secondBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(0, 255, 0, 255);
          }
          thirdBarNum = IcosahedronModel.edges[lb.barNum].myEndPointJoints[2].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(thirdBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(0, 0, 255, 255);
          }
          fourthBarNum = IcosahedronModel.edges[lb.barNum].myEndPointJoints[3].edge.lightBar.barNum;
          jlb = IcosahedronModel.lightBars.get(fourthBarNum);
          for (LXPoint point: jlb.points) {
            colors[point.index] = LXColor.rgba(255, 255, 0, 255);
          }
        }
      } else {

      }
    }
  }
}
