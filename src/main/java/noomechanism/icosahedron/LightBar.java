package noomechanism.icosahedron;

import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class LightBar {

  public float length;
  public int numPoints;
  public boolean arch;
  public List<LXPoint> points;

  public LightBar(float length, int numPoints, boolean arch) {
    this.length = length;
    this.numPoints = numPoints;
    this.arch = arch;

    points = new ArrayList<LXPoint>(numPoints);
    for (int i = 0; i < numPoints; i++) {
      if (!arch) {
        points.add(new LXPoint(((float) i / (float) numPoints) * length, 0f, 0f));
      } else {
        float archRadius = 2.0f; // 2 meter radius arch
        float tPos = (float) i / (float) numPoints;
        // For Arch right to left we want tPos from 180 degrees to 0 degrees or
        points.add(new LXPoint(Math.cos(Math.toRadians(180f - tPos * 180f)) * length ,
            Math.sin(Math.toRadians(180f - tPos * 180f)) * length, 0f));
      }
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
   * Interpolates the points along the given edge, based on the number of points.
   * @param edge An edge in 3D space.
   */
  public void interpolate(IcosahedronModel.Edge edge) {
    float startX = edge.a.x;
    float startY = edge.a.y;
    float startZ = edge.a.z;
    float finishX = edge.b.x;
    float finishY = edge.b.y;
    float finishZ = edge.b.z;
    float lengthX = finishX - startX;
    float lengthY = finishY - startY;
    float lengthZ = finishZ - startZ;

    for (int i = 0; i < numPoints; i++) {
      float tParam = (float) i / (float) (numPoints - 1);
      points.get(i).x = startX + lengthX * tParam;
      points.get(i).y = startY + lengthY * tParam;
      points.get(i).z = startZ + lengthZ * tParam;
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
