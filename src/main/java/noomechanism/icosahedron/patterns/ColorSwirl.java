package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

import static java.lang.StrictMath.*;

@LXCategory("Color")
public class ColorSwirl extends LXPattern {
  private static final float TWO_PI = (float) 6.28318;

  public String getAuthor() {
    return "Mark C. Slee";
  }

  private float basis = 0;

  public final CompoundParameter speed =
          new CompoundParameter("Speed", .5, 0, 2);

  public final CompoundParameter slope =
          new CompoundParameter("Slope", 1, .2, 3);

  public final DiscreteParameter amount =
          new DiscreteParameter("Amount", 3, 1, 5)
                  .setDescription("Amount of swirling around the center");

  public ColorSwirl(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
    addParameter("slope", this.slope);
    addParameter("amount", this.amount);
  }

  public void run(double deltaMs) {
    this.basis = (float) (this.basis + .001 * speed.getValuef() * deltaMs) % TWO_PI;
    float slope = this.slope.getValuef();
    float sat = palette.getSaturationf();
    int amount = this.amount.getValuei();
    for (LXPoint p : model.points) {
      float hb1 = (this.basis + p.azimuth - slope * (1 - p.yn)) / TWO_PI;
      colors[p.index]= LXColor.hsb(
              hb1 * 360 * amount,
              sat,
              100
      );
    }
  }
}