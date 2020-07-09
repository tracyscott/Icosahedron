package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXEffect;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.Colors;

public class HueMultiplyEfx extends LXEffect {
  CompoundParameter hue = new CompoundParameter("hue", 0f, 0f, 1f);
  CompoundParameter saturation = new CompoundParameter("sat", 1f, 0f, 1f);
  CompoundParameter bright = new CompoundParameter("bright", 1f, 0f, 1f);

  float hsb[] = new float[3];

  public HueMultiplyEfx(LX lx) {
    super(lx);
    addParameter(hue);
    addParameter(saturation);
    addParameter(bright);
  }

  @Override
  public void run(double deltaMs, double amount) {
    int color =  LXColor.hsb(360f * hue.getValuef(), 100f * saturation.getValuef(), 100f * bright.getValuef());
    for (LXPoint point : lx.getModel().points) {
      int originalColor = this.colors[point.index];
      Colors.RGBtoHSB(originalColor, hsb);
      hsb[1] = 0f;  // desaturate original
      // effectively only the brightness is left.  We could also just take the HSB from our knobs and multiply the
      // brightness values.
      int newDesaturatedColor = Colors.HSBtoRGB(hsb);
      this.colors[point.index] = LXColor.blend(newDesaturatedColor, color, LXColor.Blend.MULTIPLY);
    }
  }
}
