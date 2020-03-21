package noomechanism.icosahedron.ui;

import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;
import noomechanism.icosahedron.Output;

/**
 * UIMappingConfig provides mapping configuration for lightbars.  For each output, specify
 * a list of lightbar IDs separated by commas in the order that the lightbars are
 * wired.
 */
public class UIMappingConfig extends UIConfig {
  public static final String OUTPUT1 = "output1";
  public static final String OUTPUT2 = "output2";
  public static final String OUTPUT3 = "output3";
  public static final String OUTPUT4 = "output4";
  public static final String OUTPUT5 = "output5";
  public static final String OUTPUT6 = "output6";
  public static final String OUTPUT7 = "output7";
  public static final String OUTPUT8 = "output8";
  public static final String OUTPUT9 = "output9";
  public static final String OUTPUT10 = "output10";
  public static final String OUTPUT11 = "output11";
  public static final String OUTPUT12 = "output12";
  public static final String OUTPUT13 = "output13";
  public static final String OUTPUT14 = "output14";
  public static final String OUTPUT15 = "output15";
  public static final String OUTPUT16 = "output16";

  public static final String title = "mapping";
  public static final String filename = "mappingconfig.json";
  public LX lx;
  private boolean parameterChanged = false;

  public UIMappingConfig(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    int contentWidth = (int)ui.leftPane.global.getContentWidth();
    this.lx = lx;

    registerStringParameter(OUTPUT1, "0,16,17");
    registerStringParameter(OUTPUT2, "1,18");
    registerStringParameter(OUTPUT3, "2,19,20,21");
    registerStringParameter(OUTPUT4, "3,22,23,24");
    registerStringParameter(OUTPUT5, "4");
    registerStringParameter(OUTPUT6, "5,25,26,27,28,29");
    registerStringParameter(OUTPUT7, "6");
    registerStringParameter(OUTPUT8, "7");
    registerStringParameter(OUTPUT9, "8");
    registerStringParameter(OUTPUT10, "9");
    registerStringParameter(OUTPUT11, "10");
    registerStringParameter(OUTPUT12, "11");
    registerStringParameter(OUTPUT13, "12");
    registerStringParameter(OUTPUT14, "13");
    registerStringParameter(OUTPUT15, "14");
    registerStringParameter(OUTPUT16, "15");

    save();

    buildUI(ui);
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    parameterChanged = true;
  }

  @Override
  public void onSave() {
    // Only reconfigure if a parameter changed.
    if (parameterChanged) {
      boolean originalEnabled = lx.engine.output.enabled.getValueb();
      lx.engine.output.enabled.setValue(false);
      lx.engine.output.removeChild(Output.datagramOutput);
      Output.configurePixliteOutput(lx);
      parameterChanged = false;
      lx.engine.output.enabled.setValue(originalEnabled);
    }
  }
}
