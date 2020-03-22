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

import heronarts.lx.studio.LXStudio;
import noomechanism.icosahedron.ui.UILightBarConfig;
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
  public static final int NUM_ARCH_LIGHT_BARS = 15;
  public static final int NUM_LIGHT_BARS = 30;
  public static Point3D[] unitIcosahedron;
  public static IcosahedronModel model;
  public static Point3D[] vertices;
  public static Edge[] edges;

  static public List<LightBar> lightBars;

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

  public static class Edge {
    public Edge(Point3D a, Point3D b) {
      this.a = a; this.b =b;
    }

    public Point3D a;
    public Point3D b;
  }

  // Icosahedron vertices via
  // http://rbwhitaker.wikidot.com/index-and-vertex-buffers
  // This didn't work very well, leaving it for reference.  Instead
  // we build it through spherical coordinates in another method.
  // Will delete this soon.
  //
  public static Point3D[] brokenCreate(float scale) {
    // A temporary array, with 12 items in it, because
    // the icosahedron has 12 distinct vertices
    vertices = new Point3D[12];
    edges = new Edge[30];

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

    // Build edges
    edges[0] = new Edge(vertices[0], vertices[6]);
    edges[1] = new Edge(vertices[6], vertices[1]);
    edges[2] = new Edge(vertices[0], vertices[11]);
    edges[3] = new Edge(vertices[11], vertices[6]);
    edges[4] = new Edge(vertices[1], vertices[4]);
    edges[5] = new Edge(vertices[4], vertices[0]);
    edges[6] = new Edge(vertices[1], vertices[8]);
    edges[7] = new Edge(vertices[8], vertices[4]);
    edges[8] = new Edge(vertices[1], vertices[10]);
    edges[9] = new Edge(vertices[10], vertices[8]);
    edges[10] = new Edge(vertices[2], vertices[5]);
    edges[11] = new Edge(vertices[5], vertices[3]);
    edges[12] = new Edge(vertices[2], vertices[9]);
    edges[13] = new Edge(vertices[9], vertices[5]);
    edges[14] = new Edge(vertices[2], vertices[11]);
    edges[15] = new Edge(vertices[11], vertices[9]);
    edges[16] = new Edge(vertices[3], vertices[7]);
    edges[17] = new Edge(vertices[7], vertices[2]);
    edges[18] = new Edge(vertices[3], vertices[10]);
    edges[19] = new Edge(vertices[10], vertices[7]);
    edges[20] = new Edge(vertices[4], vertices[8]);
    edges[21] = new Edge(vertices[8], vertices[5]);
    edges[22] = new Edge(vertices[4], vertices[9]);
    edges[23] = new Edge(vertices[9], vertices[0]);
    // already above at 21 edges[25] = new Edge(vertices[5], vertices[8]);
    edges[24] = new Edge(vertices[8], vertices[3]);
    // already at 13 edges[] = new Edge(vertices[5], vertices[9]);
    // already at 22 edges[26] = new Edge(vertices[9], vertices[4]);
    edges[25] = new Edge(vertices[6], vertices[10]);
    edges[26] = new Edge(vertices[10], vertices[1]);
    // 6 and 11 at 3
    edges[27] = new Edge(vertices[11], vertices[7]);
    // 7 and 10 at 19
    // 10 and 6 at 25
    // 7 and 11 at 27
    // 2 and 11 at 14
    // 8 and 10 at 9
    // 10 and 3 at 18
    edges[28] = new Edge(vertices[9], vertices[11]);
    // 11 and 0 at 2
    edges[29] = new Edge(vertices[0], vertices[6]);

    return vertices;
  }

  /**
   * Spherical Coordinates construction.
   * The locations of the vertices of a regular icosahedron can be described using spherical coordinates, for instance
   * as latitude and longitude. If two vertices are taken to be at the north and south poles (latitude ±90°), then the
   * other ten vertices are at latitude ±arctan(1/2) ≈ ±26.57°. These ten vertices are at evenly spaced
   * longitudes (36° apart), alternating between north and south latitudes.
   * @return
   */
  public static Point3D[] createIcosahedronVerticesEdges(float radius) {
    vertices = new Point3D[12];
    edges = new Edge[30];

    vertices[0] = new Point3D(0f, radius, 0f);
    // top band of 5 points
    double latitudeDegrees = 26.57;
    double latitude = Math.toRadians(latitudeDegrees);
    double longitudeIncr = 36.0;
    double ninetyRadians = Math.toRadians(90f);

    // https://en.wikipedia.org/wiki/Spherical_coordinate_system
    // 3D graphics converted to Mathematical spherical coordinates (Physics model on the page).
    // X is is -Z, Y is X, Z is Y.
    // our construction latitude is 90 degrees - theta degrees from theta on wikipedia page.
    for (int i = 0; i < 5; i++) {
      double polarAngle = Math.toRadians(i * longitudeIncr * 2.0);  // ISO azimuth
      vertices[i+1] = new Point3D(
          (float)(radius * Math.sin(ninetyRadians - latitude) * Math.sin(polarAngle)),
          (float)(radius * Math.cos(ninetyRadians - latitude)),
          -(float)(radius * Math.sin(ninetyRadians - latitude) * Math.cos(polarAngle)));
    }
    // Lower hemisphere points.
    latitude = Math.toRadians(-latitudeDegrees);
    for (int i = 0; i < 5; i++) {
      double polarAngle = Math.toRadians(i * longitudeIncr * 2.0 + longitudeIncr);  // ISO azimuth
      vertices[i+6] = new Point3D(
          (float)(radius * Math.sin(ninetyRadians - latitude) * Math.sin(polarAngle)),
          (float)(radius * Math.cos(ninetyRadians - latitude)),
          -(float)(radius * Math.sin(ninetyRadians - latitude) * Math.cos(polarAngle)));
    }
    vertices[11] = new Point3D(0f, -radius, 0f);

    // rotate 90 degrees around X
    // y' = y*cos q - z*sin q
    // z' = y*sin q + z*cos q
    // x' = x
    for (Point3D p : vertices) {
      float yOrig = p.y;
      p.y = (float)(-p.z * Math.sin(Math.toRadians(90)) + p.y * Math.cos(Math.toRadians(90)));
      p.z = (float)(yOrig * Math.sin(Math.toRadians(90)) + p.z * Math.cos(Math.toRadians(90)));
    }

    // Edges
    // One edge from top to first row.
    int edgeNum = 0;
    for (int i = 1; i < 6; i++) {
      edges[edgeNum++] = new Edge(vertices[0], vertices[i]);
    }
    // One edge between all vertices in top row
    edges[edgeNum++] = new Edge(vertices[1], vertices[2]);
    edges[edgeNum++] = new Edge(vertices[2], vertices[3]);
    edges[edgeNum++] = new Edge(vertices[3], vertices[4]);
    edges[edgeNum++] = new Edge(vertices[4], vertices[5]);
    edges[edgeNum++] = new Edge(vertices[5], vertices[1]);

    int offset = 5;
    // Edges from top row to bottom row alternate vertices.
    edges[edgeNum++] = new Edge(vertices[1], vertices[1+offset]);
    edges[edgeNum++] = new Edge(vertices[offset+1], vertices[2]);
    edges[edgeNum++] = new Edge(vertices[2], vertices[2+offset]);
    edges[edgeNum++] = new Edge(vertices[2+offset], vertices[3]);
    edges[edgeNum++] = new Edge(vertices[3], vertices[3+offset]);
    edges[edgeNum++] = new Edge(vertices[3+offset], vertices[4]);
    edges[edgeNum++] = new Edge(vertices[4], vertices[4+offset]);
    edges[edgeNum++] = new Edge(vertices[4+offset], vertices[5]);
    edges[edgeNum++] = new Edge(vertices[5], vertices[5+offset]);
    edges[edgeNum++] = new Edge(vertices[5+offset], vertices[1]);

    // One edge between all vertices bottom row
    edges[edgeNum++] = new Edge(vertices[6], vertices[7]);
    edges[edgeNum++] = new Edge(vertices[7], vertices[8]);
    edges[edgeNum++] = new Edge(vertices[8], vertices[9]);
    edges[edgeNum++] = new Edge(vertices[9], vertices[10]);
    edges[edgeNum++] = new Edge(vertices[10], vertices[6]);

    // One edge from bottom to bottom row points
    for (int i = 0; i < 5; i++) {
      edges[edgeNum++] = new Edge(vertices[i+6], vertices[11]);
    }
    return vertices;
  }

  public static IcosahedronModel createModel() {
    unitIcosahedron = createIcosahedronVerticesEdges(4.75f);
    List<LXPoint> allPoints = new ArrayList<LXPoint>();
    List<LightBar> lightBars = new ArrayList<LightBar>();
    for (int i = 0; i < NUM_LIGHT_BARS; i++) {
      LightBar lb = new LightBar(i,
          Icosahedron.lightBarParamsLength,
          Icosahedron.lightBarParamsStartMargin,
          Icosahedron.lightBarParamsEndMargin,
          Icosahedron.lightBarParamsLeds,
          false);
      lb.interpolate(edges[i]);
      lightBars.add(lb);
      allPoints.addAll(lb.points);
    }

    model = new IcosahedronModel(allPoints, lightBars);
    return model;
  }

  public static IcosahedronModel createArchModel() {
    List<LXPoint> allPoints = new ArrayList<LXPoint>();
    List<LightBar> lightBars = new ArrayList<LightBar>();
    for (int i = 0; i < NUM_ARCH_LIGHT_BARS; i++) {
      LightBar lb = new LightBar(i,
          Icosahedron.lightBarParamsLength,
          Icosahedron.lightBarParamsStartMargin,
          Icosahedron.lightBarParamsEndMargin,
          Icosahedron.lightBarParamsLeds,
          true);
      lb.translate(0f, 0f, i * 0.2f);
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
