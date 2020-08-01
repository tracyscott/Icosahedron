package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

/**
 * Parametric version of Collision.
 */
public class CollT extends AnimT {

  CompoundParameter slope = new CompoundParameter("slope", 0.5f, 0.01f, 20.0f);
  CompoundParameter maxValue = new CompoundParameter("maxv", 1.0, 0.0, 1.0);
  DiscreteParameter waveform = new DiscreteParameter("wave", 0, 0, 2).setDescription("0=triangle 1=square");

  public CollT(LX lx) {
    super(lx);
    registerPhase("Move", 1.0f, 10.0f, "Start duration");
    registerPhase("Explode", 1.0f, 10.0f, "Explosion duration");
    addParameter(maxValue);
    addParameter(slope);
    addParameter(waveform);
  }

  protected float blobPos1 = 0.0f;
  protected float blobPos2 = 1.0f;

  public void renderPhase(int phaseNum, float phaseLocalT) {
    for (LXPoint pt : model.points)
      colors[pt.index] = LXColor.rgba(0, 0, 0, 255);

    for (LightBar lightBar : IcosahedronModel.getAllLightBars()) {
      if (phaseNum == 0) {
        blobPos1 = phaseLocalT / 2f;
        blobPos2 = 1f - phaseLocalT / 2f;
        if (waveform.getValuei() == 0) {
          LightBarRender1D.renderTriangle(colors, lightBar, blobPos1, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
          LightBarRender1D.renderTriangle(colors, lightBar, blobPos2, slope.getValuef(), maxValue.getValuef(), LXColor.Blend.ADD);
        } else if (waveform.getValuei() == 1) {
          LightBarRender1D.renderSquare(colors, lightBar, blobPos1, 4f / 150f, maxValue.getValuef(), LXColor.Blend.ADD);
          LightBarRender1D.renderSquare(colors, lightBar, blobPos2, 4f / 150f, maxValue.getValuef(), LXColor.Blend.ADD);
        }
      } else if (phaseNum == 1) {
        if (waveform.getValuei() == 0) {
          float explosionSlope = slope.getValuef() - phaseLocalT * slope.getValuef();
          LightBarRender1D.renderTriangle(colors, lightBar, blobPos1, explosionSlope, maxValue.getValuef(), LXColor.Blend.ADD);
          LightBarRender1D.renderTriangle(colors, lightBar, blobPos2, explosionSlope, maxValue.getValuef(), LXColor.Blend.ADD);
        } else if (waveform.getValuei() == 1) {
          float explosionWidth = 4f / 150f + (150f * phaseLocalT) / 150f;
          LightBarRender1D.renderSquare(colors, lightBar, blobPos1, explosionWidth, maxValue.getValuef(), LXColor.Blend.ADD);
          LightBarRender1D.renderSquare(colors, lightBar, blobPos2, explosionWidth, maxValue.getValuef(), LXColor.Blend.ADD);
        }
      }
    }
  }
}
