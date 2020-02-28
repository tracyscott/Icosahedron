package noomechanism.icosahedron;

import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class LightBar {

  public float length;
  public int numPoints;
  public List<LXPoint> points;

  public LightBar(float length, int numPoints) {
    this.length = length;
    this.numPoints = numPoints;

    points = new ArrayList<LXPoint>(numPoints);
    for (int i = 0; i < numPoints; i++) {
      points.add(new LXPoint( ((float)i/(float)numPoints) * length, 0f, 0f));
    }
  }

  public void translate(float x, float y, float z) {
    for (LXPoint pt : points) {
      pt.x += x;
      pt.y += y;
      pt.z += z;
    }
  }

  /**
   * Returns our points in wire order.  For lightbars, this is straightforward since it is the same
   * order in which we create the points.
   * @return Points in the order they are on the data line.
   */
  public List<LXPoint> pointsInWireOrder(){
    List<LXPoint> pointsWireOrder = new ArrayList<LXPoint>(points);
    return pointsWireOrder;
  }
}
