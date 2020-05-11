package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.CompoundParameter;

abstract public class FPSPattern extends LXPattern {

  public final CompoundParameter fpsKnob =
      new CompoundParameter("Fps", 61, 0.0, 61 + 10)
          .setDescription("Controls the frames per second.");
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
    if ((int) currentFrame > previousFrame) {
      // Time for new frame.  Draw
      renderFrame(deltaDrawMs);
      previousFrame = (int) currentFrame;
      deltaDrawMs = 0.0;
    }
    // Don't let current frame increment forever.  Otherwise float will
    // begin to lose precision and things get wonky.
    if (currentFrame > 10000.0) {
      currentFrame = 0.0;
      previousFrame = -1;
    }
  }


  protected abstract void renderFrame(double deltaDrawMs);
}
