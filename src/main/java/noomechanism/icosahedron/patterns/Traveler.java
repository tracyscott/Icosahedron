package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
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
  public BooleanParameter sparkle = new BooleanParameter("sparkle", true);

  public static class Blob {
    public int currentBarNum = 0;
    public boolean forward = true;
    public float pos = 0f;
    public float speed = 1f;
    public int nextBarNum = -1;
    public boolean nextBarForward = true;
    public int prevBarNum = -1;
    public boolean prevBarForward = true;
  }

  public Blob[] blobs = new Blob[MAX_BLOBS];

  public Traveler(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(maxValue);
    addParameter(speed);
    addParameter(numBlobs);
    addParameter(randSpeed);
    addParameter(sparkle);
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
          float minMax[] = LightBarRender1D.renderTriangle(colors, lb, blob.pos, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
          //LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
          if ((minMax[0] < 0.0f && !blob.forward) || (minMax[1] > 1.0f && blob.forward)) {
            // Beginning of triangle wave spans the joint.  But for now we are only handling the leading edge of the wave for the
            // case of supporting spanning a single joint.
            if (blob.nextBarNum == -1) {
              chooseNextBar(blob);
            }
            float nextBarPos = computeNextBarPos(blob);
            LightBar nextBar = IcosahedronModel.lightBars.get(blob.nextBarNum);
            LightBarRender1D.renderTriangle(colors, nextBar, nextBarPos, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
            //LightBarRender1D.randomGray(colors, nextBar, LXColor.Blend.MULTIPLY);
          }
          // Render on the previous bar if necessary
          // TODO(tracy): Ideally, we should reset prevBarNum when our triangle pulse no longer touches it but it is also
          // okay to just render it because it will render black.
          if (blob.prevBarNum != -1) {
            LightBar prevBar = IcosahedronModel.lightBars.get(blob.prevBarNum);
            float prevBarPos = computePrevBarPos(blob);
            LightBarRender1D.renderTriangle(colors, prevBar, prevBarPos, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
            //LightBarRender1D.randomGray(colors, prevBar, LXColor.Blend.MULTIPLY);
          }
          if (minMax[1] > 1.0f && blob.forward) {
            // Ending of triangle wave spans the end joint.  In that scenario, we need to select the next lightbar and re-render
            // on it 0.8 on one light bar is .2 on another so 0 - (1.0 - current position) if the next bar is starting
            // otherwise, if the next bar in the joint is the end of the bar then we need to be at
            // 1.0 + (1.0 - current position)
            // If we currently don't have a nextBarNum selected, choose the next random lightbar to traverse.
            if (blob.nextBarNum == -1) {
              chooseNextBar(blob);
            }
          }

          if (blob.forward) {
            blob.pos += blob.speed / 100f;
          } else {
            blob.pos -= blob.speed / 100f;
          }
          // If we are going off the end of our lightbar, look for a new lightbar if necessary and reset our
          // nextBarNum, nextBarForward parameters if they are set.
          if (blob.pos <= 0.0 || blob.pos >= 1.0f) {
            if (blob.nextBarNum == -1)
              chooseNextBar(blob);

            // Now we need to make nextBarNum the currentBarNum.
            blob.prevBarNum = blob.currentBarNum;
            blob.prevBarForward = blob.forward;
            blob.currentBarNum = blob.nextBarNum;
            blob.forward = blob.nextBarForward;
            blob.nextBarNum = -1;
            if (blob.forward) {
              blob.pos = 0.0f;
            } else {
              blob.pos = 1.0f;
            }
          }
        }
      }
      if (newCurrentLightBarNum != -1) {
        blob.currentBarNum = newCurrentLightBarNum;
      }
    }
    if (sparkle.getValueb()) {
      for (LightBar lb : IcosahedronModel.lightBars) {
        LightBarRender1D.randomGray(colors, lb, LXColor.Blend.MULTIPLY);
      }
    }
  }

  /**
   * Utility function for choosing the next lightbar.  This method will account for our direction of travel.  If moving
   * forward, we look at our end joints.  If not moving forward, we look at our start joints for choosing the next
   * lightbar.  We store the choice in blob.nextBarNum.  We also track the directionality of the next bar in
   * blob.nextBarForward.
   */
  public void chooseNextBar(Blob blob) {
    if (blob.forward) {
      chooseRandomBarFromJoints(blob, IcosahedronModel.edges[blob.currentBarNum].myEndPointJoints);
    } else {
      chooseRandomBarFromJoints(blob, IcosahedronModel.edges[blob.currentBarNum].myStartPointJoints);
    }
  }

  /**
   * Given a blob and a set of joints, randomly select the next light bar.  Sets blob.nextBarNum and
   * blob.nextBarNum appropriately.
   * @param blob
   * @param joints
   */
  public void chooseRandomBarFromJoints(Blob blob, IcosahedronModel.Joint[] joints) {
    int jointNum = ThreadLocalRandom.current().nextInt(4);
    IcosahedronModel.Edge nextEdge = joints[jointNum].edge;
    blob.nextBarForward = joints[jointNum].isAdjacentEdgeAStartPoint;
    blob.nextBarNum = nextEdge.lightBar.barNum;
  }

  public float computeNextBarPos(Blob blob) {
    // Ending of triangle wave spans the end joint.  In that scenario, we need to select the next lightbar and re-render
    // on it 0.8 on one light bar is .2 on another so 0 - (1.0 - current position) if the next bar is starting
    // otherwise, if the next bar in the joint is the end of the bar then we need to be at
    // 1.0 + (1.0 - current position)
    // If we currently don't have a nextBarNum selected, choose the next random lightbar to traverse.
    if (blob.forward) {
      if (blob.nextBarForward) {
        // If our position is 0.8, the next bar value if it is forward should be -0.2
        return 0f - (1.0f - blob.pos);
      } else {
        // Else if our position is 0.8, the next bar position if it is not forward is 1.2
        return 1.0f + (1.0f - blob.pos);
      }
    } else {
      // Ending of the triangle wave spans the start joint.  In that scenario, take pos as 0.1.  If the next bar is
      // forward, the pos for that render should be -0.1.  If the next bar is backwards, the pos for that render
      // should be 1.1
      if (blob.nextBarForward) {
        return -blob.pos;
      } else {
        return 1.0f + blob.pos;
      }
    }
  }

  public float computePrevBarPos(Blob blob) {
    // If we are forward, and our pos is 0.2, then if prev bar was forward then position should be 1.2
    // else position is -0.2
    if (blob.forward) {
      if (blob.prevBarForward) {
        return 1.0f + blob.pos;
      } else {
        return 0.0f - blob.pos;
      }
    } else {
      // If not forward, and we are at 0.8 then if prev bar was forward, we are at -0.2.
      // Of - 1.0f - position
      // If prev bar was not forward, then we are at 1.2 which is 1.0 - pos + 1.0f
      if (blob.prevBarForward) {
        return 1.0f - blob.pos + 1.0f;
      } else {
        return 0f - (1.0f - blob.pos);
      }
    }
  }
}
