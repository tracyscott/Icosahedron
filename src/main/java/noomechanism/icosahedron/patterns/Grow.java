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

public class Grow extends LXPattern {
  static public final float MAX_INTENSITY = 1.0f;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 5.0);
  public CompoundParameter paramT = new CompoundParameter("t", 0.0, 0.0, 1.0);
  public DiscreteParameter waveKnob = new DiscreteParameter("wave", 0, 0, 3).setDescription("Waveform type");
  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");
  public CompoundParameter startMargin = new CompoundParameter("strtMgn", 0.5, 0.0f, 10.0f).setDescription("Start margin");
  public CompoundParameter endMargin = new CompoundParameter("endMgn", 0.5, 0.0f, 10.0f).setDescription("End margin");

  public Blob stemOneBlobOne = new Blob();
  public Blob stemOneBlobTwo = new Blob();

  public Blob stemTwoBlobOne = new Blob();
  public Blob stemTwoBlobTwo = new Blob();

  public Blob stemThreeBlobOne = new Blob();
  public Blob stemThreeBlobTwo = new Blob();

  List<DirectionalLightBar> stemOneBarsOne = new ArrayList<DirectionalLightBar>(2);
  List<DirectionalLightBar> stemOneBarsTwo = new ArrayList<DirectionalLightBar>(2);

  List<DirectionalLightBar> stemTwoBarsOne = new ArrayList<DirectionalLightBar>(2);
  List<DirectionalLightBar> stemTwoBarsTwo = new ArrayList<DirectionalLightBar>(2);

  List<DirectionalLightBar> stemThreeBarsOne = new ArrayList<DirectionalLightBar>(2);
  List<DirectionalLightBar> stemThreeBarsTwo = new ArrayList<DirectionalLightBar>(2);


  public Grow(LX lx) {
    super(lx);
    addParameter(slope);
    addParameter(paramT);
    addParameter(waveKnob);
    addParameter(widthKnob);
    addParameter(startMargin);
    addParameter(endMargin);
    // 27 forwards, split, 25 backwards, 29 backwards
    DirectionalLightBar stemOneDlb = new DirectionalLightBar(27, true);
    stemOneBarsOne.add(stemOneDlb);
    stemOneBarsOne.add(new DirectionalLightBar(25, false));
    stemOneBarsTwo.add(stemOneDlb);
    stemOneBarsTwo.add(new DirectionalLightBar(29, false));
    stemOneBlobOne.pathBars = stemOneBarsOne;
    stemOneBlobTwo.pathBars = stemOneBarsTwo;

    // 6 forward, 5 forward, 11 backwards
    DirectionalLightBar stemTwoDlb = new DirectionalLightBar(6, true);
    stemTwoBarsOne.add(stemTwoDlb);
    stemTwoBarsOne.add(new DirectionalLightBar(5, true));
    stemTwoBarsTwo.add(stemTwoDlb);
    stemTwoBarsTwo.add(new DirectionalLightBar(11, false));
    stemTwoBlobOne.pathBars = stemTwoBarsOne;
    stemTwoBlobTwo.pathBars = stemTwoBarsTwo;

    // 8 backwards, 9 forwards, 18 backwards
    DirectionalLightBar stemThreeDlb = new DirectionalLightBar(8, false);
    stemThreeBarsOne.add(stemThreeDlb);
    stemThreeBarsOne.add(new DirectionalLightBar(9, true));
    stemThreeBarsTwo.add(stemThreeDlb);
    stemThreeBarsTwo.add(new DirectionalLightBar(18, false));
    stemThreeBlobOne.pathBars = stemThreeBarsOne;
    stemThreeBlobTwo.pathBars = stemThreeBarsTwo;
  }

  public void run(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }
    // TODO(tracy): We need some one to mark 27 as dirty so don't render it twice.
    stemOneBlobOne.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemOneBlobOne.pathBars.get(0).disableRender = true;
    stemOneBlobTwo.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemOneBlobOne.pathBars.get(0).disableRender = false;

    stemTwoBlobOne.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemTwoBlobOne.pathBars.get(0).disableRender = true;
    stemTwoBlobTwo.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemTwoBlobOne.pathBars.get(0).disableRender = false;

    stemThreeBlobOne.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemThreeBlobOne.pathBars.get(0).disableRender = true;
    stemThreeBlobTwo.renderBlobAtT(colors, paramT.getValuef(), widthKnob.getValuef(), slope.getValuef(),
        MAX_INTENSITY, waveKnob.getValuei(), startMargin.getValuef(), 2 + endMargin.getValuef());
    stemThreeBlobOne.pathBars.get(0).disableRender = false;
  }
}
