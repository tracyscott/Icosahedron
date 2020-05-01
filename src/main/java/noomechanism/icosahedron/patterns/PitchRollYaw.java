package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXLayer;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.transform.LXProjection;
import heronarts.lx.transform.LXVector;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;
import noomechanism.icosahedron.LightBarRender1D;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class PitchRollYaw extends LXPattern {

  private static final double TWO_PI = 6.28318530718;
  //fields or variables 
  private LXProjection proj = new LXProjection(model);
  final CompoundParameter pRot = new CompoundParameter("Pitch", 0, -TWO_PI, TWO_PI);
  final CompoundParameter rRot = new CompoundParameter("Roll", 0, -TWO_PI, TWO_PI);
  final CompoundParameter yRot = new CompoundParameter("Yaw", 0, 0, TWO_PI);
  final CompoundParameter yOffset  = new CompoundParameter("Y off", 0, -3* model.yMax, 3* model.yMax);
  final CompoundParameter intensity = new CompoundParameter("Int", 100, 0, 100);
  final CompoundParameter spread = (CompoundParameter) new CompoundParameter("Spread", 50 , 0, 500).setExponent(3);
  final CompoundParameter pAmp = (CompoundParameter) new CompoundParameter("pAmp", 0.1, 0 , 1.0)  .setExponent(3);
  final CompoundParameter rAmp = (CompoundParameter) new CompoundParameter("rAmp", 0.1, 0 , 1.0) .setExponent(3);
  final CompoundParameter yAmp = new CompoundParameter("yAmp", 1.0, 0 , 1.0);

  //Contructor Method
  public PitchRollYaw(LX lx) {
    super(lx);
    for (int i = 0; i < 1; ++i) {
      addLayer(new PitRoll(lx));
    }
    addParameter(pRot);
    addParameter(rRot);
    addParameter(yRot);
    addParameter(yOffset);
    addParameter(pAmp);
    addParameter(rAmp);
    addParameter(yAmp);
    addParameter(intensity);
    addParameter(spread);
  }

  //sets background color to be black
  public void run(double deltaMs) {
    setColors(0x000000);
  }
  //New class called
  class PitRoll extends LXLayer {

    //Contructor method  
    public PitRoll(LX lx) {
      super(lx);
    }

    //main/run method
    public void run(double deltaMs) {
      proj.reset().center().rotateY(yAmp.getValuef() *yRot.getValuef()).rotateX(pAmp.getValuef() * pRot.getValuef()).rotateZ(rAmp.getValuef() * rRot.getValuef());//rotateZ(amplitude.getValuef() * zAmp.getValuef() * roll.getValuef());
      //float yOffset = yOffset.getValuef();
      //float falloff = 100 / (2*FEET);
      for (LXVector v : proj) {
        float b = intensity.getValuef() - spread.getValuef() * abs(v.y - yOffset.getValuef());
        if (b > 0) {
          addColor(v.index, LXColor.gray(b));
        }
      }
    }
  }
}
