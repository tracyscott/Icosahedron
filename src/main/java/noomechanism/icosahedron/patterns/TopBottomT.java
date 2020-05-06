package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.Blob;
import noomechanism.icosahedron.DirectionalLightBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses blob rendering code to render a triangle tracer on the top and bottom
 * triangles.
 */
public class TopBottomT extends LXPattern {
  static public final float MAX_INTENSITY = 1.0f;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter paramT = new CompoundParameter("t", 0.0, 0.0, 1.0);
  public DiscreteParameter waveKnob = new DiscreteParameter("wave", 0, 0, 3).setDescription("Waveform type");
  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");
  public CompoundParameter extraLength = new CompoundParameter("extra", 0.5, 0.0f, 10.0f);

  public Blob topBlob = new Blob();
  public Blob bottomBlob = new Blob();

  List<DirectionalLightBar> topBars = new ArrayList<DirectionalLightBar>(3);
  List<DirectionalLightBar> bottomBars = new ArrayList<DirectionalLightBar>(3);

  static final int TOP_BAR_NUM = 24; // 24, 10, 19
  static final int BOTTOM_BAR_NUM = 15; // 14, 7

  public TopBottomT(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(paramT);
    addParameter(waveKnob);
    addParameter(widthKnob);
    addParameter(extraLength);
    topBars.add(new DirectionalLightBar(TOP_BAR_NUM, false));
    bottomBars.add(new DirectionalLightBar(BOTTOM_BAR_NUM, true));

    DirectionalLightBar nextTopBar = topBars.get(0).chooseNextBar(3);
    topBars.add(nextTopBar);
    nextTopBar = nextTopBar.chooseNextBar(3);
    topBars.add(nextTopBar);
    DirectionalLightBar nextBottomBar = bottomBars.get(0).chooseNextBar(0);
    bottomBars.add(nextBottomBar);
    nextBottomBar = nextBottomBar.chooseNextBar(0);
    bottomBars.add(nextBottomBar);

    topBlob.pathBars = topBars;
    bottomBlob.pathBars = bottomBars;
  }

  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    topBlob.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), 3 + extraLength.getValuef());
    bottomBlob.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), 3 + extraLength.getValuef());
  }
}
