package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXModelLayer;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static heronarts.lx.LXUtils.random;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Bugs extends LXPattern {

  public final CompoundParameter speed =
          new CompoundParameter("Speed", 10, 20, 1)
                  .setDescription("Speed of the bugs");

  public final CompoundParameter size =
          new CompoundParameter("Size", .1, .02, .4)
                  .setDescription("Size of the bugs");

  public Bugs(LX lx) {
    super(lx);
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      for (int i = 0; i < 10; ++i) {
        addLayer(new Layer(lx, lb));
      }
    }
    addParameter("speed", this.speed);
    addParameter("size", this.size);
  }

  class RandomSpeed extends FunctionalParameter {

    private final float rand;

    RandomSpeed(float low, float hi) {
      this.rand = (float) random(low, hi);
    }

    public double getValue() {
      return this.rand * speed.getValue();
    }
  }

  class Layer extends LXModelLayer<IcosahedronModel> {

    private final LightBar lb;
    private final LXModulator pos = startModulator(new SinLFO(
            startModulator(new SinLFO(0, .5, new RandomSpeed(500, 1000)).randomBasis()),
            startModulator(new SinLFO(.5, 1, new RandomSpeed(500, 1000)).randomBasis()),
            new RandomSpeed(3000, 8000)
    ).randomBasis());

    private final LXModulator size = startModulator(new SinLFO(
            startModulator(new SinLFO(.1, .3, new RandomSpeed(500, 1000)).randomBasis()),
            startModulator(new SinLFO(.5, 1, new RandomSpeed(500, 1000)).randomBasis()),
            startModulator(new SinLFO(4000, 14000, random(3000, 18000)).randomBasis())
    ).randomBasis());

    Layer(LX lx, LightBar lb) {
      super(lx);
      this.lb = lb;
    }

    public void run(double deltaMs) {
      float size = Bugs.this.size.getValuef() * this.size.getValuef();
      float falloff = (float) (100.0 / size);//max(size, (1.5 / model.yRange)));
      float pos = this.pos.getValuef();
      for (LXPoint p : this.lb.points) {
        float b = 100 - falloff * abs(p.yn - pos);
        if (b > 0) {
          addColor(p.index, LXColor.gray(b));
        }
      }
    }
  }
  public void run(double deltaMs) {
    setColors(0x000000);
  }
}