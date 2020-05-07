package noomechanism.icosahedron;

import java.util.concurrent.ThreadLocalRandom;

public class DirectionalLightBar {
  public DirectionalLightBar(int lbNum, boolean forward) {
    lb = IcosahedronModel.lightBars.get(lbNum);
    this.forward = forward;
    disableRender = false;
  }
  public LightBar lb;
  public boolean forward;
  public boolean disableRender;


  public DirectionalLightBar chooseNextBar(int jointSelector) {
    if (forward) {
      return chooseBarFromJoints(lb.edge.myEndPointJoints, jointSelector);
    } else {
      return chooseBarFromJoints(lb.edge.myStartPointJoints, jointSelector);
    }
  }

  public DirectionalLightBar choosePrevBar(int jointSelector) {
    if (forward) {
      return chooseBarFromJoints(lb.edge.myStartPointJoints, jointSelector);
    } else {
      return chooseBarFromJoints(lb.edge.myEndPointJoints, jointSelector);
    }
  }

  /**
   * Given an array of joints, select the next light bar.
   * @param joints Array of joints to select from.
   * @param jointSelector Which joint to select the next bar from.  If -1, then choose a random joint.
   */
  static public DirectionalLightBar chooseBarFromJoints(IcosahedronModel.Joint[] joints, int jointSelector) {
    int jointNum = jointSelector;
    if (jointNum == -1)
      jointNum = ThreadLocalRandom.current().nextInt(4);
    IcosahedronModel.Edge nextEdge = joints[jointNum].edge;
    DirectionalLightBar dlb = new DirectionalLightBar(nextEdge.lightBar.barNum,
        joints[jointNum].isAdjacentEdgeAStartPoint);
    return dlb;
  }

  public float computeNextBarPos(float pos, DirectionalLightBar nextBar) {
    float distanceToJoint = 1.0f - pos;
    if (!forward) {
      distanceToJoint = pos;
    }
    if (nextBar.forward) {
      return -distanceToJoint;
    } else {
      return 1.0f + distanceToJoint;
    }
  }

  public float computePrevBarPos(float pos, DirectionalLightBar prevBar) {
    // For the previous bar, in the straightforward case, the distance to this joint will be the current
    // position on the bar since the joint will be at 0.0.  If the current bar is not forward, the position
    // at the joint with the previous bar (bar is too the left) is actually 1.0 so the distance is 1.0 - pos.
    float distanceToJoint = pos;
    if (!forward) {
      distanceToJoint = 1.0f - pos;
    }
    // If the previous bar is oriented normally, then off to the right will be 1.0 + distance.
    // If the previous bar is backwards, then off to the right will be 0 - distance.
    if (prevBar.forward) {
      return 1.0f + distanceToJoint;
    } else {
      return -distanceToJoint;
    }
  }
}