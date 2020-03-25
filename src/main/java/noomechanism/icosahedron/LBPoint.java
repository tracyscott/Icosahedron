package noomechanism.icosahedron;

import heronarts.lx.model.LXPoint;

/**
 * LBPoint is a wrapper class for an LXPoint that adds some additional functionality
 * for tracking the LightBar-local x position of a point for simplifying 1D lightbar-local
 * animations.
 */
public class LBPoint extends LXPoint {

  public LightBar lightBar;
  public float lbx;

  public LBPoint(LightBar lightBar, double x, double y, double z, double lbx) {
    super(x, y, z);
    this.lightBar = lightBar;
    this.lbx = (float)lbx;
  }
}
