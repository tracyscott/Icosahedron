package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;

public class XScanner extends ColorPattern {
  public CompoundParameter xThickness = new CompoundParameter("xThck", 1.0, 0.0, 5.0);
  public CompoundParameter xPos = new CompoundParameter("xPos", 0.0, -5.0, 5.0);
  public CompoundParameter yThickness = new CompoundParameter("yThck", 1.0, 0.0, 5.0);
  public CompoundParameter yPos = new CompoundParameter("yPos", 0.0, -5.0, 5.0);
  public CompoundParameter zThickness = new CompoundParameter("zThck", 1.0, 0.0, 5.0);
  public CompoundParameter zPos = new CompoundParameter("zPos", 0.0, -6.0, 6.0);

  public BooleanParameter xDim = new BooleanParameter("xDim", true);
  public BooleanParameter yDim = new BooleanParameter("yDim", true);
  public BooleanParameter zDim = new BooleanParameter("zDim", true);

  public BooleanParameter intersect = new BooleanParameter("ntrsct", false);

  public XScanner(LX lx) {
    super(lx);
    addParameter(fpsKnob);
    addParameter(xThickness);
    addParameter(xPos);
    addParameter(xDim);

    addParameter(yThickness);
    addParameter(yPos);
    addParameter(yDim);

    addParameter(zThickness);
    addParameter(zPos);
    addParameter(zDim);

    randomPaletteKnob.setValue(false);
    paletteKnob.setValue(1);
    addParameter(hue);
    addParameter(saturation);
    addParameter(bright);

    addParameter(intersect);
  }

  @Override
  public void renderFrame(double deltaMs) {
    int color = getNewRGB();
    for (LXPoint pt : lx.getModel().points) {
      if (xDim.getValueb() && ((pt.x < xPos.getValuef() + xThickness.getValuef()/2f &&
          pt.x > xPos.getValuef() - xThickness.getValuef()/2f))) {
        colors[pt.index] = color;
      } else if (yDim.getValueb() && ((pt.y < yPos.getValuef() + yThickness.getValuef()/2f &&
                 pt.y > yPos.getValuef() - yThickness.getValuef()/2f))) {
        colors[pt.index] = color;
      } else if (zDim.getValueb() && ((pt.z < zPos.getValuef() + zThickness.getValuef()/2f &&
                 pt.z > zPos.getValuef() - zThickness.getValuef()/2f))) {
        colors[pt.index] = color;
      } else {
        colors[pt.index] = LXColor.BLACK;
      }
    }
  }
}
