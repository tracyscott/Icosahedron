package noomechanism.icosahedron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IcosahedronFixture {
  public Point3D[] vertices;
  public Edge[] edges;
  public Face[] faces;
  public List<LightBar> lightBars;

  /**
   * Spherical Coordinates construction.
   * The locations of the vertices of a regular icosahedron can be described using spherical coordinates, for instance
   * as latitude and longitude. If two vertices are taken to be at the north and south poles (latitude ±90°), then the
   * other ten vertices are at latitude ±arctan(1/2) ≈ ±26.57°. These ten vertices are at evenly spaced
   * longitudes (36° apart), alternating between north and south latitudes.
   * @return
   */
  static public IcosahedronFixture createIcosahedronFixture(float radius) {
    IcosahedronFixture fixture = new IcosahedronFixture();
    fixture.vertices = new Point3D[12];
    fixture.edges = new Edge[30];
    fixture.faces = new Face[20];
    fixture.lightBars = new ArrayList<LightBar>();

    fixture.vertices[0] = new Point3D(0f, radius, 0f);
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
      fixture.vertices[i+1] = new Point3D(
          (float)(radius * Math.sin(ninetyRadians - latitude) * Math.sin(polarAngle)),
          (float)(radius * Math.cos(ninetyRadians - latitude)),
          -(float)(radius * Math.sin(ninetyRadians - latitude) * Math.cos(polarAngle)));
    }
    // Lower hemisphere points.
    latitude = Math.toRadians(-latitudeDegrees);
    for (int i = 0; i < 5; i++) {
      double polarAngle = Math.toRadians(i * longitudeIncr * 2.0 + longitudeIncr);  // ISO azimuth
      fixture.vertices[i+6] = new Point3D(
          (float)(radius * Math.sin(ninetyRadians - latitude) * Math.sin(polarAngle)),
          (float)(radius * Math.cos(ninetyRadians - latitude)),
          -(float)(radius * Math.sin(ninetyRadians - latitude) * Math.cos(polarAngle)));
    }
    fixture.vertices[11] = new Point3D(0f, -radius, 0f);

    // Edges
    // One edge from top to first row. 5 total
    int edgeNum = 0;
    //for (int i = 1; i < 6; i++) {
    //  edges[edgeNum++] = new Edge(vertices[0], vertices[i]);
    //}
    //hard-coded for ease of reversing directions
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[1], fixture.vertices[0]); //0
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[0], fixture.vertices[2]); //1
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3], fixture.vertices[0]); //2
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[4], fixture.vertices[0]); //3
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[0], fixture.vertices[5]); //4

    // One edge between all vertices in top row
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[2], fixture.vertices[1]); //5
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3], fixture.vertices[2]); //6
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3], fixture.vertices[4]); //7
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[5], fixture.vertices[4]); //8
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[5], fixture.vertices[1]); //9

    int offset = 5;
    // Edges from top row to bottom row alternate vertices.
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[1], fixture.vertices[1+offset]); //10
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[offset+1], fixture.vertices[2]); //11
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[2], fixture.vertices[2+offset]); //12
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3], fixture.vertices[2+offset]); //13
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3], fixture.vertices[3+offset]); //14
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[3+offset], fixture.vertices[4]); //15
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[4], fixture.vertices[4+offset]); //16
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[4+offset], fixture.vertices[5]); //17
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[5+offset], fixture.vertices[5]); //18
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[5+offset], fixture.vertices[1]); //19

    // One edge between all fixture.vertices bottom row
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[7], fixture.vertices[6]); //20
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[7], fixture.vertices[8]); //21
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[9], fixture.vertices[8]); //22
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[10], fixture.vertices[9]); //23
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[10], fixture.vertices[6]); //24

    // One edge from bottom to bottom row points
    //for (int i = 0; i < 5; i++) {
    //  fixture.edges[edgeNum++] = new Edge(fixture.vertices[i+6], fixture.vertices[11]);
    //}
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[6], fixture.vertices[11]); //25
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[11], fixture.vertices[7]); //26
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[8], fixture.vertices[11]); //27
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[11], fixture.vertices[9]); //28
    fixture.edges[edgeNum++] = new Edge(fixture.vertices[10], fixture.vertices[11]); //29

    Edge.computeAdjacentEdges(fixture.edges);
    Edge.sortEdges(fixture.edges);

    int faceNum = 0;
    // Top cone
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[0], fixture.edges[5], fixture.edges[1]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[1], fixture.edges[6], fixture.edges[2]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[2], fixture.edges[7], fixture.edges[3]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[3], fixture.edges[8], fixture.edges[4]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[4], fixture.edges[9], fixture.edges[0]);
    faceNum++;

    // Body is 10 fixture.faces.  For top row points, we take the edge between
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

    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[10], fixture.edges[11], fixture.edges[5]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[20], fixture.edges[12], fixture.edges[11]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[12], fixture.edges[13], fixture.edges[6]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[21], fixture.edges[14], fixture.edges[13]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[14], fixture.edges[15], fixture.edges[7]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[22], fixture.edges[15], fixture.edges[16]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[16], fixture.edges[17], fixture.edges[8]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[23], fixture.edges[17], fixture.edges[18]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[18], fixture.edges[19], fixture.edges[9]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[24], fixture.edges[10], fixture.edges[19]);
    faceNum++;

    // bottom cone
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[20], fixture.edges[25], fixture.edges[26]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[21], fixture.edges[26], fixture.edges[27]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[22], fixture.edges[27], fixture.edges[28]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[23], fixture.edges[28], fixture.edges[29]);
    faceNum++;
    fixture.faces[faceNum] = new Face(faceNum, fixture.edges[24], fixture.edges[29], fixture.edges[25]);
    faceNum++;

    // Populate our adjacent faces list.
    for (int i = 0; i < faceNum; i++) {
      fixture.faces[i].findAdjacentFaces(fixture.faces);
    }

    Connector.computeConnectors(fixture.edges);
    return fixture;
  }

  public void rotateXAxis(float degrees) {
    // rotate 100.8 degrees around X
    // y' = y*cos q - z*sin q
    // z' = y*sin q + z*cos q
    // x' = x
    // TODO(tracy): Properly compute this, heh.
    float xAxisRotate = degrees;
    for (Point3D p : vertices) {
      float yOrig = p.y;
      p.y = (float) (-p.z * Math.sin(Math.toRadians(xAxisRotate)) + p.y * Math.cos(Math.toRadians(xAxisRotate)));
      p.z = (float) (yOrig * Math.sin(Math.toRadians(xAxisRotate)) + p.z * Math.cos(Math.toRadians(xAxisRotate)));
    }
  }

  public void rotateYAxis(float degrees) {
    for (Point3D p : vertices) {
      float xOrig = p.x;
      p.x = (float) (p.z * Math.sin(Math.toRadians(degrees)) + p.x * Math.cos(Math.toRadians(degrees)));
      p.z = (float) (-xOrig * Math.sin(Math.toRadians(degrees)) + p.z * Math.cos(Math.toRadians(degrees)));
    }
  }

  public void scale(float x, float y, float z) {
    for (Point3D p : vertices) {
      p.x = x * p.x;
      p.y = y * p.y;
      p.z = z * p.z;
    }
  }

  public void translate(float x, float y, float z) {
    for (Point3D p : vertices) {
      p.x += x;
      p.y += y;
      p.z += z;
    }
  }

  public static class Joint {
    public Joint(Edge e, boolean isStartPoint) {
      edge = e;
      isAdjacentEdgeAStartPoint = isStartPoint;
    }

    public Point3D getJointPt() {
      if (isAdjacentEdgeAStartPoint)
        return edge.a2;
      else
        return edge.b2;
    }

    public Point3D getFarPt() {
      if (isAdjacentEdgeAStartPoint)
        return edge.b2;
      else
        return edge.a2;
    }

    /**
     * Given an array of joints and a Point3D, return a sorted list where the joints are sorted
     * by the distance to their farPt() (i.e. not joint point) to the given point).  The two edges on
     * the outside of a joint are the closest to the reference edge.  The edges connected to the interior
     * of the joint we have their far points a greater distance from the far point of the reference edge.
     *
     * @param joints
     * @param pt
     * @return
     */
    static public List<Joint> sortByDistanceFrom(Joint[] joints, Point3D pt) {
      List<Joint> sortedJoints = new ArrayList<Joint>();
      for (int i = 0; i < joints.length; i++) {
        boolean inserted = false;
        for (int j = 0; j < sortedJoints.size(); j++) {
          if (joints[i].getFarPt().distanceTo(pt) < sortedJoints.get(j).getFarPt().distanceTo(pt)) {
            sortedJoints.add(j, joints[i]);
            inserted = true;
            break;
          }
        }
        if (!inserted) sortedJoints.add(joints[i]);
      }
      return sortedJoints;
    }

    public Edge edge;
    public boolean isAdjacentEdgeAStartPoint;
    public float sortAngle;
  }

  public static class SphericalCoord {
    public float r;
    public float inclination;
    public float azimuth;
  }

  public static class Point3D {
    public Point3D(float x, float y, float z) {
      this.x = x; this.y = y; this.z = z;
    }

    public Point3D(Point3D p) {
      x = p.x;
      y = p.y;
      z = p.z;
    }

    public void scale(float x, float y, float z) {
      this.x *= x; this.y *= y; this.z *= z;
    }

    public void rotate(float angle) {
      float xNew = x * (float)Math.cos(angle) - y * (float)Math.sin(angle);
      float yNew = x * (float)Math.sin(angle) + y * (float)Math.cos(angle);
      x = xNew;
      y = yNew;
    }

    public float computePolarAngle() {
      return (float)Math.atan2(z, x);
    }

    public void translate(float x, float y, float z) {
      this.x += x;
      this.y += y;
      this.z += z;
    }

    public SphericalCoord computeSphericalCoord() {
      SphericalCoord sc = new SphericalCoord();
      sc.r = (float)Math.sqrt(x * x + y * y + z * z);
      sc.azimuth = (float)Math.atan2(z, x);
      sc.inclination = (float)Math.acos(y/sc.r);
      return sc;
    }

    public void rotateZAxis(float angle) {
      float newX = x * (float)Math.cos(angle) + y * (float)Math.sin(angle);
      float newY = - x * (float)Math.sin(angle) + y * (float)Math.cos(angle);
      x = newX;
      y = newY;
    }

    public void rotateYAxis(float angle) {
      float newX = x * (float)Math.cos(angle) - z * (float)Math.sin(angle);
      float newZ = x * (float)Math.sin(angle) + z * (float)Math.cos(angle);
      x = newX;
      z = newZ;
    }

    public void rotateXAxis(float angle) {
      float newY = y * (float)Math.cos(angle) + z * (float)Math.sin(angle);
      float newZ = -y * (float)Math.sin(angle) + z * (float)Math.cos(angle);
      y = newY;
      z = newZ;
    }

    public void projectXYPlane() {
      z = 0f;
    }

    public void projectXZPlane() {
      y = 0f;
    }

    public void projectYZPlane() {
      x = 0f;
    }

    public float length() {
      return (float)Math.sqrt(x*x + y*y + z*z);
    }

    public float distanceTo(Point3D p) {
      return (float)Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) + (z-p.z)*(z-p.x));
    }

    public float dotProduct(Point3D p) {
      float dotProduct = x * p.x + y * p.y + z * p.z;
      return dotProduct;
    }

    public float angle(Point3D p) {
      return (float)Math.acos(dotProduct(p) / (length() * p.length()));
    }

    public float x;
    public float y;
    public float z;
  }

  public static class Edge {
    public Edge(Point3D a, Point3D b) {
      this.a = a; this.b =b; this.a2 = new Point3D(a.x, a.y, a.z); this.b2 = new Point3D(b.x, b.y, b.z);
    }

    public Point3D a;
    public Point3D b;
    // These are working coordinates that we operate on while computing the adjacent edges sorting order.
    public Point3D a2;
    public Point3D b2;

    public LightBar lightBar;
    public Joint[] myStartPointJoints = new Joint[4];
    public Joint[] myEndPointJoints = new Joint[4];

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

    public void resetSortParams() {
      a2 = new Point3D(a.x, a.y, a.z);
      b2 = new Point3D(b.x, b.y, b.z);
    }

    public void projectXYPlane() {
      a2.z = 0f;
      b2.z = 0f;
    }

    public void translate(float x, float y) {
      a2.x += x;
      b2.y += y;
    }

    public void translate(float x, float y, float z) {
      a2.translate(x, y, z);
      b2.translate(x, y, z);
    }

    public void rotate(float angle) {
      a2.rotate(angle);
      b2.rotate(angle);
    }

    public static void computeAdjacentEdges(Edge[] edges) {
      for (Edge thisEdge : edges) {
        int currentStartJointNum = 0;
        int currentEndJointNum = 0;
        for (Edge otherEdge: edges) {
          if (thisEdge == otherEdge)
            continue;
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


    public String getStartConnectorKey() {
      return getConnectorKey(myStartPointJoints);
    }

    public String getEndConnectorKey() {
      return getConnectorKey(myEndPointJoints);
    }

    public String getConnectorKey(Joint[] joints) {
      List<String> barNums = new ArrayList<String>();
      barNums.add("" + lightBar.barNum);
      barNums.add("" + joints[0].edge.lightBar.barNum);
      barNums.add("" + joints[1].edge.lightBar.barNum);
      barNums.add("" + joints[2].edge.lightBar.barNum);
      barNums.add("" + joints[3].edge.lightBar.barNum);
      Collections.sort(barNums);
      String barNumKey = "";
      for (String s : barNums) {
        barNumKey += s + "-";
      }
      return barNumKey;
    }

    /**
     * Sort our adjacent edges at each end.  The edges are sorted in left to right order from the perspective of this
     * edge and looking towards the joint.  First, we project all edge endpoints into the XY plane.  Since we don't
     * have any strictly vertical edges, this will preserve the angle we care about.  Next, we translate the position
     * of the joint to the origin.  This will give us a series of 2D lines joining at the origin.  Next, we compute
     * the rotation angle of the non-joint endpoint of our reference edge in polar coordinates.  We rotate all
     * edges by the negative of the angle to align our reference edge with the X axis. Next, we compute the polar
     * coordinate angles of the adjacent edges and then reverse sort them for left to right orientation.
     */
    static public void sortEdges(Edge[] edges) {
      for (Edge edge : edges) {
        edge.sortAdjacentEdges();
      }
    }

    public void sortAdjacentEdges() {
      // We pass new points here
      sortAdjacentEdges(new Point3D(b), myEndPointJoints, true);
      sortAdjacentEdges(new Point3D(a), myStartPointJoints, false);
    }

    public void sortAdjacentEdges(Point3D jointPoint, Joint[] joints, boolean isBigEnd) {
      translate(-jointPoint.x, -jointPoint.y, -jointPoint.z);
      Point3D referenceEndPt;
      if (isBigEnd) {
        referenceEndPt = a2;
      } else {
        referenceEndPt = b2;
      }
      SphericalCoord sc = referenceEndPt.computeSphericalCoord();

      // Rotate to put the reference end point on to the positive x axis.
      float azimuthRotation = -sc.azimuth;
      float inclinationRotation = ((float)Math.PI/2.0f - sc.inclination);
      referenceEndPt.rotateYAxis(azimuthRotation);
      referenceEndPt.rotateZAxis(inclinationRotation);

      float centroidX = 0f;
      float centroidY = 0f;
      float centroidZ = 0f;
      for (Joint j : joints) {
        j.edge.translate(-jointPoint.x, -jointPoint.y, -jointPoint.z);
        Point3D farPt = j.getFarPt();
        farPt.rotateYAxis(azimuthRotation);
        farPt.rotateZAxis(inclinationRotation);
        centroidX += farPt.x;
        centroidY += farPt.y;
        centroidZ += farPt.z;
      }
      // Compute the centroid
      centroidX = centroidX / 4f;
      centroidY = centroidY / 4f;
      centroidZ = centroidZ / 4f;
      Point3D centroid = new Point3D(centroidX, centroidY, centroidZ);

      // Now we need to compute how much to rotate around the X Axis to make the centroid parallel to the Y axis.
      float angle;
      Point3D centroidYZ = new Point3D(centroid);
      centroidYZ.projectYZPlane();
      Point3D yAxis = new Point3D(0, 1, 0);
      angle = centroidYZ.angle(yAxis);
      if (centroidZ < 0f) {
        angle = -angle;
      }

      float angleYAxisOrient = angle;
      for (Joint j : joints) {
        Point3D farPt = j.getFarPt();
        farPt.rotateXAxis(angleYAxisOrient);
        farPt.projectXZPlane();
      }

      // Now we need to compute the angles of the end points of the adjacent edges.  Whether that is
      // the 'a' point or 'b' point depends on the directional orientation of the connected edge.
      for (Joint j : joints) {
        Point3D farPt = j.getFarPt();
        j.sortAngle = farPt.computePolarAngle();
        if (j.sortAngle < 0f) {
          j.sortAngle += 2.0f * Math.PI;
        }
      }

      List<Joint> sortedJoints = new ArrayList<Joint>();
      for (int i = 0; i < joints.length; i++) {
        boolean inserted = false;
        for (int j = 0; j < sortedJoints.size(); j++) {
          if (joints[i].sortAngle < sortedJoints.get(j).sortAngle) {
            sortedJoints.add(j, joints[i]);
            inserted = true;
            break;
          }
        }
        if (!inserted) sortedJoints.add(joints[i]);
      }
      for (int i = 0; i < sortedJoints.size(); i++) {
        joints[i] = sortedJoints.get(i);
        joints[i].sortAngle = 0f;
        joints[i].edge.resetSortParams();
      }
      resetSortParams();
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

    public boolean isFaceIdAdjacent(int faceId, Face[] faces) {
      Face f = faces[faceId];
      return isAdjacent(f);
    }

    public void findAdjacentFaces(Face[] faces) {
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
}
