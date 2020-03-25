package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

public class Collision extends AnimPattern {

  CompoundParameter slope = new CompoundParameter("slope", 0.5f, 0.01f, 5.0f);
  public Collision(LX lx) {
    super(lx);
    registerPhase("Move", 3.0f, 60.0f, "Start duration");
    registerPhase("Explode", 1.0f, 60.0f, "Explosion duration");
    addParameter(slope);
  }

  protected float blobPos1 = 0.0f;
  protected float blobPos2 = 1.0f;

  public void runPhase(int phaseNum, double deltaMs) {
    // Start at t = 0 and t = 1.  Move towards t=0.5.  At t=0.5, change animation phase to
    // explosion.
    for (LXPoint pt : model.points)
      colors[pt.index] = LXColor.rgba(0, 0, 0, 255);

    for (LightBar lightBar : IcosahedronModel.lightBars) {
      if (phaseNum == 0) {
        blobPos1 = (time / curPhaseDuration) / 2f - 0.01f;
        blobPos2 = 1f - (time / curPhaseDuration) / 2f + 0.01f;
        LightBarRender1D.renderTriangle(colors, lightBar, blobPos1, slope.getValuef(), LXColor.Blend.ADD);
        LightBarRender1D.renderTriangle(colors, lightBar, blobPos2, slope.getValuef(), LXColor.Blend.ADD);
      } else if (phaseNum == 1) {
        float explosionSlope = slope.getValuef() - (time/curPhaseDuration) * slope.getValuef();
        LightBarRender1D.renderTriangle(colors, lightBar, blobPos1, explosionSlope, LXColor.Blend.ADD);
        LightBarRender1D.renderTriangle(colors, lightBar, blobPos2, explosionSlope, LXColor.Blend.ADD);
      }
    }
  }
}
