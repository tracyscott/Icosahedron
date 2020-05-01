package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;

import static heronarts.lx.LXUtils.constrain;
import static heronarts.lx.LXUtils.random;
@LXCategory("Texture")
public class Starlight extends LXPattern {

  public final CompoundParameter speed = new CompoundParameter("Speed", 1, 2, .5);
  public final CompoundParameter base = new CompoundParameter("Base", -10, -20, 100);

  public final LXModulator[] brt = new LXModulator[50];
  private final int[] map1 = new int[model.size];
  private final int[] map2 = new int[model.size];

  public Starlight(LX lx) {
    super(lx);
    for (int i = 0; i < this.brt.length; ++i) {
      this.brt[i] = startModulator(new SinLFO(this.base, random(50, 120), new FunctionalParameter() {
        private final float rand = (float) random(1000, 5000);
        public double getValue() {
          return rand * speed.getValuef();
        }
      }).randomBasis());
    }
    for (int i = 0; i < model.size; ++i) {
      this.map1[i] = (int) constrain(random(0, this.brt.length), 0, this.brt.length-1);
      this.map2[i] = (int) constrain(random(0, this.brt.length), 0, this.brt.length-1);
    }
    addParameter("speed", this.speed);
    addParameter("base", this.base);
  }

  public void run(double deltaMs) {
    for (LXPoint p : model.points) {
      int i = p.index;
      float brt = this.brt[this.map1[i]].getValuef() + this.brt[this.map2[i]].getValuef();
      colors[i] = LXColor.gray(constrain(.5*brt, 0, 100));
    }
  }
}