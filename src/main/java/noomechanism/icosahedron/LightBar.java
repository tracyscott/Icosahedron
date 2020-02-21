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
}
