package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.concurrent.ThreadLocalRandom;

public class Traveler extends LXPattern {
  public static final int MAX_BLOBS = 100;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter maxValue = new CompoundParameter("maxv", 1.0, 0.0, 1.0);
  public CompoundParameter speed = new CompoundParameter("speed", 1.0, 0.0, 10.0);
  public CompoundParameter randSpeed = new CompoundParameter("randspd", 1.0, 0.0, 5.0);
  public DiscreteParameter numBlobs = new DiscreteParameter("blobs", 1, 1, MAX_BLOBS);

  public static class Blob {
    public int currentBarNum = 0;
    public boolean forward = true;
    public float pos = 0f;
    public float speed = 1f;
  }

  public Blob[] blobs = new Blob[MAX_BLOBS];

  public Traveler(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(maxValue);
    addParameter(speed);
    addParameter(numBlobs);
    addParameter(randSpeed);
    for (int i = 0; i < MAX_BLOBS; i++) {
      blobs[i] = new Blob();
      blobs[i].pos = (float)Math.random();
      float randSpeedOffset = randSpeed.getValuef() * (float)Math.random();
      blobs[i].speed = speed.getValuef() + randSpeedOffset;
    }
  }

  /**
   * onActive is called when the pattern starts playing and becomes the active pattern.  Here we re-assigning
   * our speeds to generate some randomness in the speeds.
   */
  @Override
  public void onActive() {
    for (int i = 0; i < MAX_BLOBS; i++) {
      blobs[i].speed = speed.getValuef() + randSpeed.getValuef() * (float)Math.random();
    }
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }

    for (int i = 0; i < numBlobs.getValuei(); i++) {
      Blob blob = blobs[i];
      int newCurrentLightBarNum = -1;
      for (LightBar lb : IcosahedronModel.lightBars) {
        if (blob.currentBarNum == lb.barNum) {
          LightBarRender1D.renderTriangle(colors, lb, blob.pos, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
          LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
          float p = blob.pos;
          if (blob.forward) {
            p += blob.speed / 100f;
          } else {
            p -= blob.speed / 100f;
          }
          // If we are going off the end of our start point, look for a new lightbar
          if (p <= 0.0) {
            int jointNum = ThreadLocalRandom.current().nextInt(4);
            IcosahedronModel.Edge nextEdge = IcosahedronModel.edges[blob.currentBarNum].myStartPointJoints[jointNum].edge;
            blob.forward = IcosahedronModel.edges[blob.currentBarNum].myStartPointJoints[jointNum].isAdjacentEdgeAStartPoint;
            newCurrentLightBarNum = nextEdge.lightBar.barNum;
            if (blob.forward) {
              p = 0.0f;
            } else {
              p = 1.0f;
            }
          } else if (p >= 1.0f) {
            // We are going off the end of our end point, look for a new lightbar.
            int jointNum = ThreadLocalRandom.current().nextInt(4);
            IcosahedronModel.Edge nextEdge = IcosahedronModel.edges[blob.currentBarNum].myEndPointJoints[jointNum].edge;
            blob.forward = IcosahedronModel.edges[blob.currentBarNum].myEndPointJoints[jointNum].isAdjacentEdgeAStartPoint;
            newCurrentLightBarNum = nextEdge.lightBar.barNum;
            if (blob.forward) {
              p = 0.0f;
            } else {
              p = 1.0f;
            }
          }
          blob.pos = p;
        }
      }
      if (newCurrentLightBarNum != -1) {
        blob.currentBarNum = newCurrentLightBarNum;
      }
    }
  }
}
