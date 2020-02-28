package noomechanism.icosahedron;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jengineering.sjmply.PLY;
import org.jengineering.sjmply.PLYElementList;
import org.jengineering.sjmply.PLYFormat;
import org.jengineering.sjmply.PLYType;

public class IcosahedronModel extends LXModel {
  private static final Logger logger = Logger.getLogger(IcosahedronModel.class.getName());

  public static double minX = Float.MAX_VALUE;
  public static double minY = Float.MAX_VALUE;
  public static double maxX = Float.MIN_VALUE;
  public static double maxY = Float.MIN_VALUE;
  public static double computedWidth = 1f;
  public static double computedHeight= 1f;
  public static final int NUM_LIGHT_BARS = 30;
  public static Point3D[] unitIcosahedron;
  public static IcosahedronModel model;

  public List<LightBar> lightBars;

  public static class Point3D {
    public Point3D(float x, float y, float z) {
      this.x = x; this.y = y; this.z = z;
    }
    public void scale(float x, float y, float z) {
      this.x *= x; this.y *= y; this.z *= z;
    }
    public float x;
    public float y;
    public float z;
  }

  // Icosahedron vertices via
  // http://rbwhitaker.wikidot.com/index-and-vertex-buffers
  //

  public static Point3D[] createIcosahedronVertices(float scale) {
    // A temporary array, with 12 items in it, because
    // the icosahedron has 12 distinct vertices
    Point3D[] vertices = new Point3D[12];

    // vertex position and color information for icosahedron
    vertices[0] = new Point3D(-0.26286500f, 0.0000000f, 0.42532500f);
    vertices[0].scale(scale, scale, scale);
    vertices[1] = new Point3D(0.26286500f, 0.0000000f, 0.42532500f);
    vertices[1].scale(scale, scale, scale);
    vertices[2] = new Point3D(-0.26286500f, 0.0000000f, -0.42532500f);
    vertices[2].scale(scale, scale, scale);
    vertices[3] = new Point3D(0.26286500f, 0.0000000f, -0.42532500f);
    vertices[3].scale(scale, scale, scale);
    vertices[4] = new Point3D(0.0000000f, 0.42532500f, 0.26286500f);
    vertices[4].scale(scale, scale, scale);
    vertices[5] = new Point3D(0.0000000f, 0.42532500f, -0.26286500f);
    vertices[5].scale(scale, scale, scale);
    vertices[6] = new Point3D(0.0000000f, -0.42532500f, 0.26286500f);
    vertices[6].scale(scale, scale, scale);
    vertices[7] = new Point3D(0.0000000f, -0.42532500f, -0.26286500f);
    vertices[7].scale(scale, scale, scale);
    vertices[8] = new Point3D(0.42532500f, 0.26286500f, 0.0000000f);
    vertices[8].scale(scale, scale, scale);
    vertices[9] = new Point3D(-0.42532500f, 0.26286500f, 0.0000000f);
    vertices[9].scale(scale, scale, scale);
    vertices[10] = new Point3D(0.42532500f, -0.26286500f, 0.0000000f);
    vertices[10].scale(scale, scale, scale);
    vertices[11] = new Point3D(-0.42532500f, -0.26286500f, 0.0000000f);
    vertices[11].scale(scale, scale, scale);

    return vertices;
  }

  public static IcosahedronModel createModel() {
    unitIcosahedron = createIcosahedronVertices(6.0f);
    List<LXPoint> allPoints = new ArrayList<LXPoint>();
    List<LightBar> lightBars = new ArrayList<LightBar>();
    for (int i = 0; i < NUM_LIGHT_BARS; i++) {
      LightBar lb = new LightBar(5.0f, 150);
      lb.translate(0f, i * 0.2f, 0f);
      lightBars.add(lb);
      allPoints.addAll(lb.points);
    }

    model = new IcosahedronModel(allPoints, lightBars);
    return model;
  }

  public IcosahedronModel(List<LXPoint> points, List<LightBar> lightBars) {
    super(points);
    // Compute some stats on our points.
    int pointCount = 0;
    for (LXPoint p : points) {
      if (p.x < minX) minX = p.x;
      if (p.y < minY) minY = p.y;
      if (p.x > maxX) maxX = p.x;
      if (p.y > maxY) maxY = p.y;
      pointCount++;
    }

    logger.info("Total points: " + pointCount);

    computedWidth = maxX - minX;
    computedHeight = maxY - minY;
    this.lightBars = lightBars;

    exportPLY(points);
  }

  public static void exportPLY(List<LXPoint> points) {
    PLY plyOut = new PLY(PLYFormat.BINARY_LITTLE_ENDIAN, "1.0");
    PLYElementList plyPoints = new PLYElementList(points.size());
    plyOut.elements.put("vertex", plyPoints);
    plyPoints.addProperty(PLYType.FLOAT32, "x");
    float[] xCoords = plyPoints.property(PLYType.FLOAT32,"x");
    plyPoints.addProperty(PLYType.FLOAT32, "y");
    float[] yCoords = plyPoints.property(PLYType.FLOAT32, "y");
    plyPoints.addProperty(PLYType.FLOAT32, "z");
    float[] zCoords = plyPoints.property(PLYType.FLOAT32, "z");
    plyPoints.addProperty(PLYType.FLOAT32, "nx");
    float[] nx = plyPoints.property(PLYType.FLOAT32, "nx");
    plyPoints.addProperty(PLYType.FLOAT32, "ny");
    float[] ny = plyPoints.property(PLYType.FLOAT32, "ny");
    plyPoints.addProperty(PLYType.FLOAT32, "nz");
    float[] nz = plyPoints.property(PLYType.FLOAT32, "nz");
    plyPoints.addProperty(PLYType.UINT8, "red");
    byte[] redValues = plyPoints.property(PLYType.UINT8, "red");
    plyPoints.addProperty(PLYType.UINT8, "green");
    byte[] greenValues = plyPoints.property(PLYType.UINT8, "green");
    plyPoints.addProperty(PLYType.UINT8, "blue");
    byte[] blueValues = plyPoints.property(PLYType.UINT8, "blue");
    plyPoints.addProperty(PLYType.UINT8, "alpha");
    byte[] alphaValues = plyPoints.property(PLYType.UINT8, "alpha");

    int i = 0;
    for (LXPoint p : points) {
      xCoords[i] = p.x;
      yCoords[i] = p.y;
      zCoords[i] = p.z;
      nx[i] = 0f;
      ny[i] = 0f;
      nz[i] = 0f;
      redValues[i] = 127;
      greenValues[i] = 127;
      blueValues[i] = 127;
      alphaValues[i] = 127;
      i++;
    }
    Path out = Paths.get("lxpoints.ply");
    try {
      plyOut.save(out);
    } catch (IOException ioex) {
      logger.log(Level.SEVERE, ioex.getMessage());
    }
  }
}
