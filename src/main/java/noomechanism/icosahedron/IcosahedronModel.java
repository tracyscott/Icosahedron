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
  public static Face[] faces;

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

  public static class Joint {
    public Joint(Edge e, boolean isStartPoint) {
      edge = e;
      isAdjacentEdgeAStartPoint = isStartPoint;
    }

    Edge edge;
    boolean isAdjacentEdgeAStartPoint;
  }

  public static class Edge {
    public Edge(Point3D a, Point3D b) {
      this.a = a; this.b =b;
    }

    public Point3D a;
    public Point3D b;
    public LightBar lightBar;
    public Joint[] myStartPointJoints = new Joint[5];
    public Joint[] myEndPointJoints = new Joint[5];

    public int isEdgeAdjacentStart(Edge edge) {
      return isEdgeAdjacent(a, edge);
    }

    public int isEdgeAdjacentEnd(Edge edge) {
      return isEdgeAdjacent(b, edge);
    }

    public int isEdgeAdjacent(Point3D pt, Edge edge) {
      if (edge.a == pt) {
        return 1;
      } else if (edge.b == pt) {
        return 2;
      }
      return 0;
    }

    public static void computeAdjacentEdges(Edge[] edges) {
      for (Edge thisEdge : edges) {
        int currentStartJointNum = 0;
        int currentEndJointNum = 0;
        for (Edge otherEdge: edges) {
          int adjacentValue = thisEdge.isEdgeAdjacentStart(otherEdge);
          if (adjacentValue == 1) {
            thisEdge.myStartPointJoints[currentStartJointNum++] = new Joint(otherEdge, true);
          } else if (adjacentValue == 2) {
            thisEdge.myStartPointJoints[currentStartJointNum++] = new Joint(otherEdge, false);
          }
          adjacentValue = thisEdge.isEdgeAdjacentEnd(otherEdge);
          if (adjacentValue == 1) {
            thisEdge.myEndPointJoints[currentEndJointNum++] = new Joint(otherEdge, true);
          } else if (adjacentValue == 2) {
            thisEdge.myEndPointJoints[currentEndJointNum++] = new Joint(otherEdge, false);
          }
        }
      }
    }
  }

  /**
   * Represents one triangular face.
   */
  public static class Face {
    public Face(int faceNum, Edge a, Edge b, Edge c) {
      this.faceNum = faceNum; this.a = a; this.b = b; this.c = c;
    }

    public List<LightBar> getLightBars() {
      List<LightBar> bars = new ArrayList<LightBar>();
      bars.add(a.lightBar);
      bars.add(b.lightBar);
      bars.add(c.lightBar);
      return bars;
    }

    public boolean isAdjacent(Face f) {
      if (f.a == a || f.b == a || f.c == a) return true;
      if (f.a == b || f.b == b || f.c == b) return true;
      if (f.a == c || f.b == c || f.c == c) return true;
      return false;
    }

    public boolean isFaceIdAdjacent(int faceId) {
      Face f = faces[faceId];
      return isAdjacent(f);
    }

    public void findAdjacentFaces() {
      for (int i = 0; i < 20; i++) {
        Face f = faces[i];
        if (f.faceNum == faceNum) continue;
        if (isAdjacent(f))
          adjacentFaces.add(f);
      }
    }

    public int faceNum;
    public Edge a;
    public Edge b;
    public Edge c;
    public List<Face> adjacentFaces = new ArrayList();
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
    faces = new Face[20];

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

    // rotate 100.8 degrees around X
    // y' = y*cos q - z*sin q
    // z' = y*sin q + z*cos q
    // x' = x
    // TODO(tracy): Properly compute this, heh.
    float xAxisRotate = 100.8f;
    for (Point3D p : vertices) {
      float yOrig = p.y;
      p.y = (float)(-p.z * Math.sin(Math.toRadians(xAxisRotate)) + p.y * Math.cos(Math.toRadians(xAxisRotate)));
      p.z = (float)(yOrig * Math.sin(Math.toRadians(xAxisRotate)) + p.z * Math.cos(Math.toRadians(xAxisRotate)));
    }

    // Edges
    // One edge from top to first row. 5 total
    int edgeNum = 0;
    //for (int i = 1; i < 6; i++) {
    //  edges[edgeNum++] = new Edge(vertices[0], vertices[i]);
    //}
    //hard-coded for ease of reversing directions
    edges[edgeNum++] = new Edge(vertices[1], vertices[0]); //0
    edges[edgeNum++] = new Edge(vertices[0], vertices[2]); //1
    edges[edgeNum++] = new Edge(vertices[3], vertices[0]); //2
    edges[edgeNum++] = new Edge(vertices[4], vertices[0]); //3
    edges[edgeNum++] = new Edge(vertices[0], vertices[5]); //4

    // One edge between all vertices in top row
    edges[edgeNum++] = new Edge(vertices[2], vertices[1]); //5
    edges[edgeNum++] = new Edge(vertices[3], vertices[2]); //6
    edges[edgeNum++] = new Edge(vertices[3], vertices[4]); //7
    edges[edgeNum++] = new Edge(vertices[5], vertices[4]); //8
    edges[edgeNum++] = new Edge(vertices[5], vertices[1]); //9

    int offset = 5;
    // Edges from top row to bottom row alternate vertices.
    edges[edgeNum++] = new Edge(vertices[1], vertices[1+offset]); //10
    edges[edgeNum++] = new Edge(vertices[offset+1], vertices[2]); //11
    edges[edgeNum++] = new Edge(vertices[2], vertices[2+offset]); //12
    edges[edgeNum++] = new Edge(vertices[3], vertices[2+offset]); //13
    edges[edgeNum++] = new Edge(vertices[3], vertices[3+offset]); //14
    edges[edgeNum++] = new Edge(vertices[3+offset], vertices[4]); //15
    edges[edgeNum++] = new Edge(vertices[4], vertices[4+offset]); //16
    edges[edgeNum++] = new Edge(vertices[4+offset], vertices[5]); //17
    edges[edgeNum++] = new Edge(vertices[5+offset], vertices[5]); //18
    edges[edgeNum++] = new Edge(vertices[5+offset], vertices[1]); //19

    // One edge between all vertices bottom row
    edges[edgeNum++] = new Edge(vertices[7], vertices[6]); //20
    edges[edgeNum++] = new Edge(vertices[7], vertices[8]); //21
    edges[edgeNum++] = new Edge(vertices[9], vertices[8]); //22
    edges[edgeNum++] = new Edge(vertices[10], vertices[9]); //23
    edges[edgeNum++] = new Edge(vertices[10], vertices[6]); //24

    // One edge from bottom to bottom row points
    //for (int i = 0; i < 5; i++) {
    //  edges[edgeNum++] = new Edge(vertices[i+6], vertices[11]);
    //}
    edges[edgeNum++] = new Edge(vertices[6], vertices[11]); //25
    edges[edgeNum++] = new Edge(vertices[11], vertices[7]); //26
    edges[edgeNum++] = new Edge(vertices[8], vertices[11]); //27
    edges[edgeNum++] = new Edge(vertices[11], vertices[9]); //28
    edges[edgeNum++] = new Edge(vertices[10], vertices[11]); //29

    Edge.computeAdjacentEdges(edges);
    
    int faceNum = 0;
    // Top cone
    faces[faceNum] = new Face(faceNum, edges[0], edges[5], edges[1]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[1], edges[6], edges[2]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[2], edges[7], edges[3]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[3], edges[8], edges[4]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[4], edges[9], edges[0]);
    faceNum++;

    // Body is 10 faces.  For top row points, we take the edge between
    // the top row and the 36degree shifted point on the bottom row.
    // The edge between that bottom row point and the adjacent top row point, and
    // finally another edge from the new top row point back to the original point.
    // Next, starting at the bottom row point, we take the edge to the adjacent
    // bottom row point, and then the edge to the previous top row point, and finally
    // the edge from that top row point back down to the original bottom row point.
    //
    // i.e. in the diagram below the first face is AB,BC,CA and the second face is
    // BD, DC, CB.  We use the standard counter-clockwise vertex order winding.
    //
    //  A --------  C     C  /\
    //     \    /           /  \
    //      \  /           /    \
    //    B  \/         B -------- D
    //
    //int firstTopRingPt =

    faces[faceNum] = new Face(faceNum, edges[10], edges[11], edges[5]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[20], edges[12], edges[11]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[12], edges[13], edges[6]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[21], edges[14], edges[13]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[14], edges[15], edges[7]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[22], edges[15], edges[16]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[16], edges[17], edges[8]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[23], edges[17], edges[18]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[18], edges[19], edges[9]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[24], edges[10], edges[19]);
    faceNum++;

    // bottom cone
    faces[faceNum] = new Face(faceNum, edges[20], edges[25], edges[26]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[21], edges[26], edges[27]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[22], edges[27], edges[28]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[23], edges[28], edges[29]);
    faceNum++;
    faces[faceNum] = new Face(faceNum, edges[24], edges[29], edges[25]);
    faceNum++;

    // Populate our adjacent faces list.
    for (int i = 0; i < faceNum; i++) {
      faces[i].findAdjacentFaces();
    }

    return vertices;
  }

  public static IcosahedronModel createModel() {
    // TODO(tracy): 4.75f radius should be computed from Icosahedron.lightBarParamsLength.
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
