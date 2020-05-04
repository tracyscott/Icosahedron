package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.transform.LXVector;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static org.joml.SimplexNoise.noise;

@LXCategory("Color")
public class LSDee extends LXPattern {

  public final BoundedParameter scale = new BoundedParameter("Scale", 10, 5, 100);
  public final BoundedParameter speed = new BoundedParameter("Speed", 4, 1, 6);
  public final BoundedParameter range = new BoundedParameter("Range", 1, .05, 2);

  public LSDee(LX lx) {
    super(lx);
    addParameter(scale);
    addParameter(speed);
    addParameter(range);
  }

  final float[] hsb = new float[3];

  private float accum = 0;
  private int equalCount = 0;
  private float sign = 1;

  @Override
  public void run(double deltaMs) {
    float newAccum = (float) (accum + sign * deltaMs * speed.getValuef() / 4000.);
    if (newAccum == accum) {
      if (++equalCount >= 5) {
        equalCount = 0;
        sign = -sign;
        newAccum = (float) (accum + sign*.01);
      }
    }
    accum = newAccum;
    float sf = (float) (scale.getValuef() / 1000.);
    float rf = range.getValuef();
    float amount = 1;
    for (LXPoint p :  model.points) {
      hsb[0] = LXColor.h(colors[p.index]);
      hsb[1] = LXColor.s(colors[p.index]);
      hsb[2] = LXColor.b(colors[p.index]);
      float h = rf * noise(sf*p.x, sf*p.y, sf*p.z + accum);
      int c2 = LX.hsb(h * 360, 100,100);
      //combine two colors
      if (amount < 1) {
        colors[p.index] = LXColor.lerp(colors[p.index], c2, amount);
      } else {
        colors[p.index] = c2;
      }
      amount = (float) 0.18;
    }

  }
}