package noomechanism.icosahedron;

import java.util.*;

/**
 * Utility class that models a single connector.  It contains 5 DirectionalLightBars.
 */
public class Connector {

  public DirectionalLightBar[] directionalLightBars = new DirectionalLightBar[5];

  static public Connector[] connectors = new Connector[12];

  /**
   * Given an edge and a set of joints, create a Connector out of all attached edges.
   * @param edge
   * @param startJoint
   * @param joints
   * @return
   */
  static public Connector createFromEdge(IcosahedronFixture.Edge edge, boolean startJoint, IcosahedronFixture.Joint[] joints) {
    Connector connector = new Connector();
    DirectionalLightBar dlb = new DirectionalLightBar(edge.lightBar.barNum, startJoint);
    connector.directionalLightBars[0] = dlb;
    int i = 1;
    for (IcosahedronFixture.Joint j : joints) {
      dlb = new DirectionalLightBar(j.edge.lightBar.barNum, j.isAdjacentEdgeAStartPoint);
      connector.directionalLightBars[i] = dlb;
      i++;
    }
    return connector;
  }

  /**
   * Iterate through our edges to build unique connectors.  For each edge joint, we generate a key
   * representing the connected lightbar IDs.
   */
 static public void computeConnectors(IcosahedronFixture.Edge[] edges) {
    Map<String, Connector> connectorMap = new HashMap<String, Connector>();

    int connectorNum = 0;
    for (IcosahedronFixture.Edge e: edges) {
      String startBarNumKey = e.getStartConnectorKey();
      if (!connectorMap.containsKey(startBarNumKey)) {
        Connector startBarConnector = Connector.createFromEdge(e, true, e.myStartPointJoints);
        connectors[connectorNum++]  = startBarConnector;
        connectorMap.put(startBarNumKey, startBarConnector);
      }

      String endBarNumKey = e.getEndConnectorKey();
      if (!connectorMap.containsKey(endBarNumKey)) {
        Connector endBarConnector = Connector.createFromEdge(e, false, e.myEndPointJoints);
        connectors[connectorNum++] = endBarConnector;
        connectorMap.put(endBarNumKey, endBarConnector);
      }
    }
  }
}
