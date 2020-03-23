package noomechanism.icosahedron;

import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LightBar {
  private static final Logger logger = Logger.getLogger(LightBar.class.getName());

  public float length;
  public float startMargin;
  public float endMargin;
  public int numPoints;
  public boolean arch;
  public int barNum;
  public List<LBPoint> points;

  public LightBar(int barNum, float length, float startMargin, float endMargin, int numPoints, boolean arch) {
    this.barNum = barNum;
    this.length = length;
    this.numPoints = numPoints;
    this.arch = arch;
    this.startMargin = startMargin;
    this.endMargin = endMargin;

    points = new ArrayList<LBPoint>(numPoints);
    for (int i = 0; i < numPoints; i++) {
      if (!arch) {
        double xPos = ((float) i / (float) numPoints) * (length - (startMargin+endMargin)) + startMargin;
        points.add(new LBPoint(this, xPos, 0f, 0f, xPos));
      } else {
        float archRadius = 2.0f; // 2 meter radius arch
        float tPos = (float) i / (float) numPoints;
        // For Arch right to left we want tPos from 180 degrees to 0 degrees or
        /*
        points.add(new LXPoint(Math.cos(Math.toRadians(180f - tPos * 180f)) * length ,
            Math.sin(Math.toRadians(180f - tPos * 180f)) * length, 0f));
            */
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

    // TODO(tracy): compute startMargin and endMargin based on orientation.
    double vLength = Math.sqrt((double)((lengthX*lengthX) + (lengthY*lengthY) + (lengthZ * lengthZ)));
    logger.info("Computed edge length: " + vLength);

    for (int i = 0; i < numPoints; i++) {
      float tParam = (float) i / (float) (numPoints - 1);
      points.get(i).x = startX + lengthX * tParam;
      points.get(i).y = startY + lengthY * tParam;
      points.get(i).z = startZ + lengthZ * tParam;
    }

    edge.lightBar = this;
  }

  /**
   * Returns our points in wire order.  For lightbars, this is straightforward since it is the same
   * order in which we create the points.
   * @return Points in the order they are on the data line.
   */
  public List<LBPoint> pointsInWireOrder(){
    List<LBPoint> pointsWireOrder = new ArrayList<LBPoint>(points);
    return pointsWireOrder;
  }
}
