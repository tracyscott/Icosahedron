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
  public static final int NUM_LIGHT_BARS_PER_FIXTURE = 30;
  public static final int NUM_FIXTURES = 2;
  public static IcosahedronModel model;
  static public List<LightBar> allLightBars;
  static public List<IcosahedronFixture> fixtures;

  static public IcosahedronFixture smallIcosahedron;
  static public IcosahedronFixture largeIcosahedron;

  public static IcosahedronFixture getFixture(int fixtureNum) {
    return fixtures.get(fixtureNum);
  }

  public static IcosahedronModel createModel() {
    List<LXPoint> allPoints = new ArrayList<LXPoint>();
    allLightBars = new ArrayList<LightBar>();
    fixtures = new ArrayList<IcosahedronFixture>();

    // TODO(tracy): 4.75f radius should be computed from Icosahedron.lightBarParamsLength.
    smallIcosahedron = IcosahedronFixture.createIcosahedronFixture(4.75f);
    smallIcosahedron.rotateXAxis(100.8f);

    for (int i = 0; i < NUM_LIGHT_BARS_PER_FIXTURE; i++) {
      LightBar lb = new LightBar(i,
          Icosahedron.lightBarParamsLength,
          Icosahedron.lightBarParamsStartMargin,
          Icosahedron.lightBarParamsEndMargin,
          Icosahedron.lightBarParamsLeds,
          false,
          smallIcosahedron.edges[i]);
      lb.interpolate(smallIcosahedron.edges[i]);
      allLightBars.add(lb);
      smallIcosahedron.lightBars.add(lb);
      allPoints.addAll(lb.points);
    }
    fixtures.add(smallIcosahedron);

    if (NUM_FIXTURES > 1) {
      largeIcosahedron = IcosahedronFixture.createIcosahedronFixture(4.75f);
      largeIcosahedron.scale(2.0f, 2.0f, 2.0f);
      largeIcosahedron.rotateXAxis(100.8f);
      largeIcosahedron.rotateYAxis(60f);
      largeIcosahedron.translate(0f, 3.8f, 0f);
      for (int i = 0; i < NUM_LIGHT_BARS_PER_FIXTURE; i++) {
        // barNums for second fixture start at 30 and go to 59
        LightBar lb = new LightBar(i + 30,
            Icosahedron.lightBarParamsLength,
            Icosahedron.lightBarParamsStartMargin,
            Icosahedron.lightBarParamsEndMargin,
            Icosahedron.lightBarParamsLeds,
            false,
            largeIcosahedron.edges[i]);
        lb.interpolate(largeIcosahedron.edges[i]);
        allLightBars.add(lb);
        largeIcosahedron.lightBars.add(lb);
        allPoints.addAll(lb.points);
      }
      fixtures.add(largeIcosahedron);
    }

    model = new IcosahedronModel(allPoints);
    return model;
  }

  public IcosahedronModel(List<LXPoint> points) {
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

    exportPLY(points);
  }

  /**
   * Return all lightbars across all fixtures.
   * @return
   */
  static public List<LightBar> getAllLightBars() {
    return allLightBars;
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
