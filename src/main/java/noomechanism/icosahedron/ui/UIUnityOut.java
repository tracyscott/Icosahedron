package noomechanism.icosahedron.ui;

import heronarts.lx.LX;
import heronarts.lx.output.LXDatagramOutput;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;
import noomechanism.icosahedron.Output;

import java.util.logging.Logger;

public class UIUnityOut extends UIConfig {

  private static final Logger logger = Logger.getLogger(UIUnityOut.class.getName());

  public static final String ENABLED = "enabled";
  public static final String OUT_1_IP = "out_1_ip";

  public static final String title = "UnityOut";
  public static final String filename = "unityout.json";
  public LX lx;
  private boolean parameterChanged = false;

  public UIUnityOut(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    int contentWidth = (int)ui.leftPane.global.getContentWidth();
    this.lx = lx;

    registerBooleanParameter(ENABLED, false);
    registerStringParameter(OUT_1_IP, "192.168.2.123");

    // TODO(tracy): We force this to button to bee disabled.  Much be enabled each time by the user.
    // We force the output to be disabled when creating the configuration during startup in
    // Output.configureUnityArtNet().  We do that since this is a secondary/optional output
    // and we don't want to accidentally spray the network with packets.
    getBooleanParameter(ENABLED).setValue(false);
    save();

    buildUI(ui);
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    if (p instanceof BooleanParameter) {
      BooleanParameter bp = (BooleanParameter) p;
      logger.info("Setting Unity output: " + bp.getValueb());
      if (Output.unityOutput != null)
        Output.unityOutput.enabled.setValue(bp.getValueb());
    } else {
      parameterChanged = true;
    }
  }

  @Override
  public void onSave() {
    // Only reconfigure if a parameter changed.
    // If we save new parameters, pause our output, rebuild the datagrams
    // and then put our output enabled back to it's original value.
    // NOTE(tracy): outputGalacticJungle always adds the output in a disabled state.
    if (parameterChanged) {
      boolean originalEnabled = lx.engine.output.enabled.getValueb();
      boolean originalUnityOutput = false;
      if (Output.unityOutput != null)
        originalUnityOutput = Output.unityOutput.enabled.getValueb();
      lx.engine.output.enabled.setValue(false);
      if (Output.unityOutput != null)
        Output.unityOutput.enabled.setValue(false);
      Output.configureUnityArtNet(lx);
      if (Output.unityOutput != null)
        Output.unityOutput.enabled.setValue(originalUnityOutput);
      parameterChanged = false;
      lx.engine.output.enabled.setValue(originalEnabled);
    }
  }
}