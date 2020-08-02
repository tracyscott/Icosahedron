package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronFixture;
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
  public DiscreteParameter nextBarKnob = new DiscreteParameter("nxtBar", -1, -1, 4);
  public CompoundParameter sparkleMin = new CompoundParameter("spklMin", 0.0f, 0.0f, 255.0f);
  public CompoundParameter sparkleDepth = new CompoundParameter("spklDepth", 255.0f, 0.0f, 255.0f);

  public static class Blob {
    public int currentBarNum = 0;
    public boolean forward = true;
    public float pos = 0f;
    public float speed = 1f;
    public int nextBarNum = -1;
    public boolean nextBarForward = true;
    public int prevBarNum = -1;
    public boolean prevBarForward = true;

    public void updateCurrentBar(int nextBarSelector) {
      if (nextBarNum == -1)
        Traveler.chooseNextBar(this, nextBarSelector);

      // Now we need to make nextBarNum the currentBarNum.
      prevBarNum = currentBarNum;
      prevBarForward = forward;
      currentBarNum = nextBarNum;
      forward = nextBarForward;
      nextBarNum = -1;
      if (forward) {
        pos = 0.0f;
      } else {
        pos = 1.0f;
      }
    }
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
    addParameter(nextBarKnob);
    addParameter(sparkleMin);
    addParameter(sparkleDepth);
    resetBars();
  }

  public void resetBars() {
    for (int i = 0; i < MAX_BLOBS; i++) {
      blobs[i] = new Blob();
      blobs[i].pos = (float)Math.random();
      blobs[i].currentBarNum = (i * 4) % IcosahedronModel.getAllLightBars().size();
      float randSpeedOffset = randSpeed.getValuef() * (float)Math.random();
      blobs[i].speed = randSpeedOffset;
    }
  }

  /**
   * onActive is called when the pattern starts playing and becomes the active pattern.  Here we re-assigning
   * our speeds to generate some randomness in the speeds.
   */
  @Override
  public void onActive() {
    resetBars();
  }

  @Override
  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }

    for (int i = 0; i < numBlobs.getValuei(); i++) {
      Blob blob = blobs[i];
      int newCurrentLightBarNum = -1;
      boolean needsCurrentBarUpdate = false;
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        if (blob.currentBarNum == lb.barNum) {
          float minMax[] = LightBarRender1D.renderTriangle(colors, lb, blob.pos, slope.getValuef(),
              maxValue.getValuef(), LXColor.Blend.ADD);
          // If the trailing edge of the triangle pulse is fully on the current lightbar, then we
          // can reset the prevBarNum so we don't render on it unnecessarily.
          if ((minMax[0] > 0f && blob.forward) || (minMax[1] < 1.0f && !blob.forward)) {
            blob.prevBarNum = -1;
          }
          if ((minMax[0] < 0.0f && !blob.forward) || (minMax[1] > 1.0f && blob.forward)) {
            if (blob.nextBarNum == -1) {
              chooseNextBar(blob, nextBarKnob.getValuei());
            }
            float nextBarPos = computeNextBarPos(blob);
            LightBar nextBar = IcosahedronModel.getAllLightBars().get(blob.nextBarNum);
            LightBarRender1D.renderTriangle(colors, nextBar, nextBarPos, slope.getValuef(), maxValue.getValuef(),
                LXColor.Blend.ADD);
          }
          // Render on the previous bar if necessary
          if (blob.prevBarNum != -1) {
            LightBar prevBar = IcosahedronModel.getAllLightBars().get(blob.prevBarNum);
            float prevBarPos = computePrevBarPos(blob);
            LightBarRender1D.renderTriangle(colors, prevBar, prevBarPos, slope.getValuef(), maxValue.getValuef(),
                LXColor.Blend.ADD);
          }

          if (blob.forward) {
            blob.pos += speed.getValuef()/100f + blob.speed / 100f;
          } else {
            blob.pos -= speed.getValuef()/100f + blob.speed / 100f;
          }

          // We are going off the end of the lightbar.  Note that we need to update the current lightbar for this
          // blob.  We will update the blob lightbar parameters after we are done iterating through all of the
          // lightbars for this blob rendering pass.
          if (blob.pos <= 0.0 || blob.pos >= 1.0f) {
            // We don't want to update the currentBarNum until we have processed all lightbars for this blob.
            // Otherwise, there is some chance that we might double-render.  For example, if the new lightbar is later
            // in our list of lightbars that we are iterating over then it will also render on the new lightbar,
            // causing a double render that looks like a "flash" with our current color ADD mode.
            needsCurrentBarUpdate = true;
          }
        }
      }
      if (newCurrentLightBarNum != -1) {
        blob.currentBarNum = newCurrentLightBarNum;
      }
      if (needsCurrentBarUpdate) {
        blob.updateCurrentBar(nextBarKnob.getValuei());
      }
    }
    if (sparkle.getValueb()) {
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        LightBarRender1D.randomGrayBaseDepth(colors, lb, LXColor.Blend.MULTIPLY, (int)sparkleMin.getValuef(),
            (int)sparkleDepth.getValuef());
      }
    }
  }

  /**
   * Utility function for choosing the next lightbar.  This method will account for our direction of travel.  If moving
   * forward, we look at our end joints.  If not moving forward, we look at our start joints for choosing the next
   * lightbar.  We store the choice in blob.nextBarNum.  We also track the directionality of the next bar in
   * blob.nextBarForward.
   */
  static public void chooseNextBar(Blob blob, int nextBarSelector) {
    if (blob.forward) {
      chooseRandomBarFromJoints(blob, IcosahedronModel.smallIcosahedron.edges[blob.currentBarNum].myEndPointJoints, nextBarSelector);
    } else {
      chooseRandomBarFromJoints(blob, IcosahedronModel.smallIcosahedron.edges[blob.currentBarNum].myStartPointJoints, nextBarSelector);
    }
  }

  /**
   * Given a blob and a set of joints, randomly select the next light bar.  Sets blob.nextBarNum and
   * blob.nextBarNum appropriately.
   * @param blob
   * @param joints
   */
  static public void chooseRandomBarFromJoints(Blob blob, IcosahedronFixture.Joint[] joints, int nextBarSelector) {
    int jointNum = nextBarSelector;
    if (jointNum == -1)
      jointNum = ThreadLocalRandom.current().nextInt(4);
    IcosahedronFixture.Edge nextEdge = joints[jointNum].edge;
    blob.nextBarForward = joints[jointNum].isAdjacentEdgeAStartPoint;
    blob.nextBarNum = nextEdge.lightBar.barNum;
  }

  static public float computeNextBarPos(Blob blob) {
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

  static public float computePrevBarPos(Blob blob) {
    // If we are forward, and our pos is 0.2, then if prev bar was forward then position should be 1.2
    // else position is -0.2
    if (blob.forward) {
      if (blob.prevBarForward) {
        return 1.0f + blob.pos;
      } else {
        return 0.0f - blob.pos;
      }
    } else {
      // If not forward, and we are at 0.2 then if prev bar was forward, we are at -0.2.
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
