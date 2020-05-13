package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.Blob;

/**
 * Uses blob rendering code to render a triangle tracer on the top and bottom
 * triangles.
 */
public class TopBottom extends LXPattern {
  static public final float MAX_INTENSITY = 1.0f;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter speed = new CompoundParameter("speed", 1.0, 0.0, 10.0);
  public DiscreteParameter waveKnob = new DiscreteParameter("wave", 0, 0, 3).setDescription("Waveform type");
  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");

  public Blob topBlob = new Blob();
  public Blob bottomBlob = new Blob();

  static final int TOP_BAR_NUM = 24; // 24, 10, 19
  static final int BOTTOM_BAR_NUM = 15; // 14, 7

  public TopBottom(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(speed);
    addParameter(waveKnob);
    addParameter(widthKnob);
    resetBlobs();
  }

  public void resetBlobs() {
    topBlob.reset(TOP_BAR_NUM, 1f,0f, false);
    bottomBlob.reset(BOTTOM_BAR_NUM, 0f, 0f, true);
  }

  /**
   * onActive is called when the pattern starts playing and becomes the active pattern.  Here we re-assigning
   * our speeds to generate some randomness in the speeds.
   */
  @Override
  public void onActive() {
    resetBlobs();
  }

  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    topBlob.renderBlob(colors, speed.getValuef(), widthKnob.getValuef(), slope.getValuef(), MAX_INTENSITY,
          waveKnob.getValuei(), 3, false, 0, 0f, 0f);
    bottomBlob.renderBlob(colors, speed.getValuef(), widthKnob.getValuef(), slope.getValuef(), MAX_INTENSITY,
        waveKnob.getValuei(), 0, false, 0, 0f, 0f);
  }
}
