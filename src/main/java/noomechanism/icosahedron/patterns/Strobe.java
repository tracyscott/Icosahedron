package noomechanism.icosahedron.patterns;

import heronarts.lx.*;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.DampedParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.LXWaveshape;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import java.awt.*;
import java.util.Arrays;

import static java.lang.Math.max;
import static processing.core.PApplet.lerp;

@LXCategory("Form")
public class Strobe extends LXEffect {

  public enum Waveshape {
    TRI,
    SIN,
    SQUARE,
    UP,
    DOWN
  }

  public final EnumParameter<Waveshape> mode = new EnumParameter<>("Shape", Waveshape.TRI);

  public final CompoundParameter frequency = (CompoundParameter)
          new CompoundParameter("Freq", 1, .05, 10).setUnits(LXParameter.Units.HERTZ);

  public final CompoundParameter depth =
          new CompoundParameter("Depth", 0.5)
                  .setDescription("Depth of the strobe effect");

  private final SawLFO basis = new SawLFO(1, 0, new FunctionalParameter() {
    public double getValue() {
      return 1000 / frequency.getValue();
    }});

  public Strobe(LX lx) {
    super(lx);
    addParameter("mode", this.mode);
    addParameter("frequency", this.frequency);
    addParameter("depth", this.depth);
    startModulator(basis);
  }

  @Override
  protected void onEnable() {
    basis.setBasis(0).start();
  }

  private LXWaveshape getWaveshape() {
    switch (this.mode.getEnum()) {
      case SIN: return LXWaveshape.SIN;
      case TRI: return LXWaveshape.TRI;
      case UP: return LXWaveshape.UP;
      case DOWN: return LXWaveshape.DOWN;
      case SQUARE: return LXWaveshape.SQUARE;
    }
    return LXWaveshape.SIN;
  }

  private final float[] hsb = new float[3];

  @Override
  public void run(double deltaMs, double amount) {
    float amt = this.enabledDamped.getValuef() * this.depth.getValuef();
    if (amt > 0) {
      float strobef = basis.getValuef();
      strobef = (float) getWaveshape().compute(strobef);
      strobef = lerp(1, strobef, amt);
      if (strobef < 1) {
        if (strobef == 0) {
          Arrays.fill(colors, LXColor.BLACK);
        } else {
          for (int i = 0; i < colors.length; ++i) {
            hsb[0] = LXColor.h(colors[i]);
            hsb[1] = LXColor.s(colors[i]);
            hsb[2] = LXColor.b(colors[i]);
            hsb[2] *= strobef;
            colors[i] = LXColor.hsb(hsb[0], hsb[1], hsb[2]);
          }
        }
      }
    }
  }
}