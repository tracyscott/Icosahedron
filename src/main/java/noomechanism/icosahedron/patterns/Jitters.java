package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.DampedParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static heronarts.lx.LXUtils.constrain;
import static heronarts.lx.LXUtils.random;
import static java.lang.Math.max;

@LXCategory("Texture")
public class Jitters extends LXPattern {

  public final CompoundParameter period = (CompoundParameter)
          new CompoundParameter("Period", 200, 2000, 50)
                  .setExponent(.5)
                  .setDescription("Speed of the motion");

  public final CompoundParameter size = (CompoundParameter)
          new CompoundParameter("Size", 20, 3, 300)
                  .setExponent(2.0)
                  .setDescription("Size of the movers");

  public final CompoundParameter contrast =
          new CompoundParameter("Contrast", 100, 50, 300)
                  .setDescription("Amount of contrast");

  final LXModulator pos = startModulator(new SawLFO(0, 1, period));

  final LXModulator sizeDamped = startModulator(new DampedParameter(size, 30));

  public Jitters(LX lx) {
    super(lx);
    addParameter("period", this.period);
    addParameter("size", this.size);
    addParameter("contrast", this.contrast);
  }

  public void run(double deltaMs) {
    float size = this.sizeDamped.getValuef();
    float pos = this.pos.getValuef();
    float sizeInv = 1 / size;
    float contrast = this.contrast.getValuef();
    boolean inv = false;
    for (LightBar lb : IcosahedronModel.lightBars) {
      inv = !inv;
      float pv = inv ? pos : (1-pos);
      int i = 0;
      for (LXPoint p : lb.points) {
        float pd = (i % size) * sizeInv;
        colors[p.index] = LXColor.gray(max(0, 100 - contrast * LXUtils.wrapdistf(pd, pv, 1)));
        ++i;
      }
    }
  }
}