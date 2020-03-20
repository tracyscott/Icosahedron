package noomechanism.icosahedron.ui;

import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

public class UILightBarConfig extends UIConfig {
  public static final String LENGTH = "length";
  public static final String LEDS = "leds";
  public static final String START_MARGIN = "start_margin";
  public static final String END_MARGIN = "end_margin";

  public static final String title = "LightBar";
  public static final String filename = "lightbarconfig.json";
  public LX lx;
  private boolean parameterChanged = false;

  public UILightBarConfig(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    int contentWidth = (int) ui.leftPane.global.getContentWidth();
    this.lx = lx;

    registerStringParameter(LENGTH, "5.0");
    registerStringParameter(LEDS, "150");
    registerStringParameter(START_MARGIN, "0.0");
    registerStringParameter(END_MARGIN, "0.0");

    save();

    buildUI(ui);
  }

  // TODO(tracy): Need some general input validation somewhere to force length to be a number/float and
  // LEDS to be an integer.  Needs to be done in a lower layer so that it can be enforced while loading the
  // configuration file.

  /**
   * getLength returns the length of the lightbar in FEET.
   * @return Length in feet.
   */
  public float getLength() {
    return Float.parseFloat(getStringParameter(LENGTH).getString());
  }

  /**
   * getNumLeds returns the number of LEDs in the lightbar.
   * @return
   */
  public int getNumLeds() {
    return Integer.parseInt(getStringParameter(LEDS).toString());
  }

  /**
   * getStartMargin returns the length of the initial margin of the lightbar in feet.  i.e. the
   * distance from the edge of the lightbar to the first LED.
   * @return Distance from edge of lightbar to first LED in feet.
   */
  public float getStartMagin() {
    return Float.parseFloat(getStringParameter(START_MARGIN).getString());
  }

  /**
   * getEndMargin returns the length of the initial margin of the lightbar in feet.  i.e. the
   * distance from the edge of the lightbar to the first LED.
   * @return distance from the last LED to the end of the lightbar in feet.
   */

  public float getEndMargin() {
    return Float.parseFloat(getStringParameter(END_MARGIN).getString());
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    parameterChanged = true;
  }

  @Override
  public void onSave() {
    // Only reconfigure if a parameter changed.
    if (parameterChanged) {
      // TODO(tracy): Rebuild the model and update LXStudio
    }
  }
}
