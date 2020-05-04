package noomechanism.icosahedron;

import heronarts.lx.parameter.LXParameter;
import noomechanism.icosahedron.ui.UIPixliteConfig;
import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.output.ArtSyncDatagram;
import heronarts.lx.output.LXDatagramOutput;
import noomechanism.icosahedron.ui.UIUnityOut;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Handles output from our 'colors' buffer to our DMX lights.  Currently using E1.31.
 */
public class Output {
  private static final Logger logger = Logger.getLogger(Output.class.getName());

  public static LXDatagramOutput datagramOutput = null;

  public static final int MAX_OUTPUTS = 32;  // 32 outputs in expanded mode.

  public static List<List<Integer>> outputs = new ArrayList<List<Integer>>(MAX_OUTPUTS);

  public static String artnetIpAddress = "192.168.2.123"; // 204"; //"192.168.137.149";
  public static int artnetPort = 6454;

  // Output for Unity, configured via ui/UIUnityOut.
  public static LXDatagramOutput unityOutput = null;

  // TODO(tracy): We need to put out the points in the same order for the CNC-based panels that we did for
  // the dimensions-based generated panels.
  public static void configureUnityArtNet(LX lx) {
    String unityIpAddress = Icosahedron.unityOut.getStringParameter(UIUnityOut.OUT_1_IP).getString();
    logger.log(Level.INFO, "Using ArtNet: " + unityIpAddress + ":" + artnetPort);

    if (unityOutput != null) {
      lx.engine.output.removeChild(unityOutput);
    }

    List<LXPoint> points = lx.getModel().getPoints();
    int numUniverses = (int)Math.ceil(((double)points.size())/170.0);
    logger.info("Num universes: " + numUniverses);
    List<ArtNetDatagram> datagrams = new ArrayList<ArtNetDatagram>();
    int totalPointsOutput = 0;

    for (int univNum = 0; univNum < numUniverses; univNum++) {
      int[] dmxChannelsForUniverse = new int[170];
      for (int i = 0; i < 170 && totalPointsOutput < points.size(); i++) {
        LXPoint p = points.get(univNum*170 + i);
        dmxChannelsForUniverse[i] = p.index;
        totalPointsOutput++;
      }

      ArtNetDatagram artnetDatagram = new ArtNetDatagram(dmxChannelsForUniverse, univNum);
      try {
        artnetDatagram.setAddress(unityIpAddress).setPort(artnetPort);
      } catch (UnknownHostException uhex) {
        logger.log(Level.SEVERE, "Configuring ArtNet: " + artnetIpAddress, uhex);
      }
      datagrams.add(artnetDatagram);
    }

    try {
      unityOutput = new LXDatagramOutput(lx);
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }

    for (ArtNetDatagram dgram : datagrams) {
      unityOutput.addDatagram(dgram);
    }

      if (unityOutput != null) {
        unityOutput.enabled.setValue(false);
        lx.engine.output.addChild(unityOutput);
      } else {
        logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
      }

  }

  public static void configurePixliteOutput(LX lx) {
    List<ArtNetDatagram> datagrams = new ArrayList<ArtNetDatagram>();
    List<Integer> countsPerOutput = new ArrayList<Integer>();
    // For each output, track the number of points per panel type so we can log the details to help
    // with output verification.

    String artNetIpAddress = Icosahedron.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_1_IP).getString();
    int artNetIpPort = Integer.parseInt(Icosahedron.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_1_PORT).getString());
    logger.log(Level.INFO, "Using Pixlite ArtNet: " + artNetIpAddress + ":" + artNetIpPort);

    // For each non-empty mapping output parameter, collect all points in wire order from each lightbar listed
    // Distribute all points across the necessary number of 170-led sized universes.
    // TODO(tracy): Make number of outputs configurable in UIPixliteConfig
    int curUniverseNum = 0;
    for (int outputNum = 0; outputNum < 16; outputNum++) {
      logger.info("Loading mapping for output " + (outputNum+1));
      String lightBarLeds = Icosahedron.mappingConfig.getStringParameter("output" + (outputNum+1)).getString();
      logger.info("lightbars: " + lightBarLeds);
      if (lightBarLeds.length() > 0) {
        List<LXPoint> pointsWireOrder = new ArrayList<LXPoint>();
        String[] ids = lightBarLeds.split(",");
        for (int i = 0; i < ids.length; i++) {
          int lightBarId = Integer.parseInt(ids[i]);
          LightBar lightBar = IcosahedronModel.lightBars.get(lightBarId);
          pointsWireOrder.addAll(lightBar.pointsInWireOrder());
        }
        
        int numUniversesThisWire = (int) Math.ceil((float) pointsWireOrder.size() / 170f);
        int univStartNum = curUniverseNum;
        int lastUniverseCount = pointsWireOrder.size() - 170 * (numUniversesThisWire - 1);
        int maxLedsPerUniverse = (pointsWireOrder.size()>170)?170:pointsWireOrder.size();
        int[] thisUniverseIndices = new int[maxLedsPerUniverse];
        int curIndex = 0;
        int curUnivOffset = 0;
        for (LXPoint pt : pointsWireOrder) {
          thisUniverseIndices[curIndex] = pt.index;
          curIndex++;
          if (curIndex == 170 || (curUnivOffset == numUniversesThisWire - 1 && curIndex == lastUniverseCount)) {
            logger.log(Level.INFO, "Adding datagram: universe=" + (univStartNum + curUnivOffset) + " points=" + curIndex);
            ArtNetDatagram datagram = new ArtNetDatagram(thisUniverseIndices, curIndex * 3, univStartNum + curUnivOffset);
            try {
              datagram.setAddress(artNetIpAddress).setPort(artNetIpPort);
            } catch (UnknownHostException uhex) {
              logger.log(Level.SEVERE, "Configuring ArtNet: " + artNetIpAddress + ":" + artNetIpPort, uhex);
            }
            datagrams.add(datagram);
            curUnivOffset++;
            curIndex = 0;
            if (curUnivOffset == numUniversesThisWire - 1) {
              thisUniverseIndices = new int[lastUniverseCount];
            } else {
              thisUniverseIndices = new int[maxLedsPerUniverse];
            }
          }
        }
        curUniverseNum += curUnivOffset;
      }
    }

    try {
      datagramOutput = new LXDatagramOutput(lx);
      for (ArtNetDatagram datagram : datagrams) {
        datagramOutput.addDatagram(datagram);
      }
      try {
        datagramOutput.addDatagram(new ArtSyncDatagram().setAddress(artNetIpAddress).setPort(artNetIpPort));
      } catch (UnknownHostException uhex) {
        logger.log(Level.SEVERE, "Unknown host for ArtNet sync.", uhex);
      }
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }
    if (datagramOutput != null) {
      datagramOutput.enabled.setValue(true);
      lx.engine.output.addChild(datagramOutput);
    } else {
      logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
    }
  }


  /**
   * One universe per lightbar.
   * @param lx
   */
  public static void configurePixliteOutputBarPerOutput(LX lx) {
    List<ArtNetDatagram> datagrams = new ArrayList<ArtNetDatagram>();
    List<Integer> countsPerOutput = new ArrayList<Integer>();
    // For each output, track the number of points per panel type so we can log the details to help
    // with output verification.

    String artNetIpAddress = Icosahedron.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_1_IP).getString();
    int artNetIpPort = Integer.parseInt(Icosahedron.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_1_PORT).getString());
    logger.log(Level.INFO, "Using ArtNet: " + artNetIpAddress + ":" + artNetIpPort);

    int lightBarNum = 0;
    for (LightBar lightBar : IcosahedronModel.model.lightBars) {
      List<LBPoint> pointsWireOrder = lightBar.pointsInWireOrder();
      int[] thisUniverseIndices = new int[150];  // 170f
      int numUniversesThisWire = (int)Math.ceil((float)pointsWireOrder.size() / 170f);
      int univStartNum = lightBarNum * numUniversesThisWire;
      int lastUniverseCount = pointsWireOrder.size() - 150 * (numUniversesThisWire - 1);
      int curIndex = 0;
      int curUnivOffset = 0;
      for (LBPoint pt : pointsWireOrder) {
        thisUniverseIndices[curIndex] = pt.index;
        curIndex++;
        if (curIndex == 170 || (curUnivOffset == numUniversesThisWire - 1 && curIndex == lastUniverseCount)) {
          logger.log(Level.INFO, "Adding datagram: universe=" + (univStartNum+curUnivOffset) + " points=" + curIndex);
          ArtNetDatagram datagram = new ArtNetDatagram(thisUniverseIndices, curIndex*3, univStartNum + curUnivOffset);
          try {
            datagram.setAddress(artNetIpAddress).setPort(artNetIpPort);
          } catch (UnknownHostException uhex) {
            logger.log(Level.SEVERE, "Configuring ArtNet: " + artNetIpAddress + ":" + artNetIpPort, uhex);
          }
          datagrams.add(datagram);
          curUnivOffset++;
          curIndex = 0;
          if (curUnivOffset == numUniversesThisWire - 1) {
            thisUniverseIndices = new int[lastUniverseCount];
          } else {
            thisUniverseIndices = new int[150];
          }
        }
      }
      lightBarNum++;
    }
    try {
      datagramOutput = new LXDatagramOutput(lx);
      for (ArtNetDatagram datagram : datagrams) {
        datagramOutput.addDatagram(datagram);
      }
      try {
        datagramOutput.addDatagram(new ArtSyncDatagram().setAddress(artNetIpAddress).setPort(artNetIpPort));
      } catch (UnknownHostException uhex) {
        logger.log(Level.SEVERE, "Unknown host for ArtNet sync.", uhex);
      }
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }
    if (datagramOutput != null) {
      datagramOutput.enabled.setValue(true);
      lx.engine.output.addChild(datagramOutput);
    } else {
      logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
    }

  }
}
