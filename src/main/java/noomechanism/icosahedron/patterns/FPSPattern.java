package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

abstract public class FPSPattern extends LXPattern {

  public final CompoundParameter fpsKnob =
      new CompoundParameter("Fps", 61, -1.0, 61 + 10)
          .setDescription("Controls the frames per second.");
  public final BooleanParameter fbang = new BooleanParameter("fbang", false);

  protected double currentFrame = 0.0;
  protected int previousFrame = -1;
  protected double deltaDrawMs = 0.0;

  public FPSPattern(LX lx) {
    super(lx);
  }

  @Override
  public void run(double deltaMs) {

    double fps = fpsKnob.getValue();
    currentFrame += (deltaMs / 1000.0) * fps;
    // We don't call draw() every frame so track the accumulated deltaMs for them.
    deltaDrawMs += deltaMs;

    // If FPS is less than zero, we will wait until fbang is true.  We will render a frame and reset
    // fbang.
    if (fps < 0f) {
      if (fbang.getValueb()) {
        fbang.setValue(false);
        renderFrame(deltaDrawMs);
        previousFrame = (int) currentFrame;
        deltaDrawMs = 0.0;
      }
    } else {
      if ((int) currentFrame > previousFrame) {
        // Time for new frame.  Draw
        renderFrame(deltaDrawMs);
        previousFrame = (int) currentFrame;
        deltaDrawMs = 0.0;
      }
    }

    // Don't let current frame increment forever.  Otherwise float will
    // begin to lose precision and things get wonky.
    if (currentFrame > 10000.0) {
      currentFrame = 0.0;
      previousFrame = -1;
    }
  }

  public void clearLightBarsToBlack() {
    for (LightBar lb : IcosahedronModel.lightBars) {
      LightBarRender1D.renderColor(colors, lb, LXColor.BLACK);
    }
  }

  protected abstract void renderFrame(double deltaDrawMs);
}
