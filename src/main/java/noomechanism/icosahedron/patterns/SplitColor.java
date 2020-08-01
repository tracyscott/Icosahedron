package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.StrictMath.random;

@LXCategory("Color")
public class SplitColor extends LXPattern {

  final CompoundParameter hue1 = new CompoundParameter("Hue 1", 0, 0, 360);
  final CompoundParameter saturation1 = new CompoundParameter("Sat 1", 100, 0, 100);
  final CompoundParameter brightness1 = new CompoundParameter("Bri 1", 100, 0, 100);
  final DiscreteParameter splitMode = new DiscreteParameter("Mode", 1, 1, 6);
  final CompoundParameter hue2 = new CompoundParameter("Hue 2", 0, 0, 360);
  final CompoundParameter saturation2 = new CompoundParameter("Sat 2", 100, 0, 100);
  final CompoundParameter brightness2 = new CompoundParameter("Bri 2", 100, 0, 100);

  public SplitColor(LX lx) {
    super(lx);
    addParameter(hue1);
    addParameter(saturation1);
    addParameter(brightness1);
    addParameter(splitMode);
    addParameter(hue2);
    addParameter(saturation2);
    addParameter(brightness2);
  }

  public void run(double deltaMs) {

    int mode= (int) splitMode.getValue();
    int pixCnt;
    int numPixels=150;
    int cntVal1= (numPixels/2);
    int cntVal2= (numPixels/4);
    int cntVal3= (numPixels/8);
    int cntVal4= (numPixels/16);
    int cntVal5= (numPixels/32);


    if (mode == 1) {
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        pixCnt = 0;
        for (LXPoint p : lb.points) {
          if (pixCnt < cntVal1) {
            colors[p.index] = LXColor.hsb( hue1.getValuef() , saturation1.getValuef(),brightness1.getValuef());
          } else {
            colors[p.index] = LXColor.hsb( hue2.getValuef() , saturation2.getValuef(),brightness2.getValuef());
          }
          ++pixCnt;
        }
      }
    } else if (mode == 2){
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        pixCnt = 0;
        for (LXPoint p : lb.points) {
          if (pixCnt % cntVal1 >= cntVal2) {
            colors[p.index] = LXColor.hsb( hue1.getValuef() , saturation1.getValuef(),brightness1.getValuef());
          } else {
            colors[p.index] = LXColor.hsb( hue2.getValuef() , saturation2.getValuef(),brightness2.getValuef());
          }
          ++pixCnt;
        }
      }
    }  else if (mode == 3){
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        pixCnt = 0;
        for (LXPoint p : lb.points) {
          if (pixCnt % cntVal2 >= cntVal3) {
            colors[p.index] = LXColor.hsb( hue1.getValuef() , saturation1.getValuef(),brightness1.getValuef());
          } else {
            colors[p.index] = LXColor.hsb( hue2.getValuef() , saturation2.getValuef(),brightness2.getValuef());
          }
          ++pixCnt;
        }
      }
    } else if (mode == 4){
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        pixCnt = 0;
        for (LXPoint p : lb.points) {
          if (pixCnt % cntVal3 >= cntVal4) {
            colors[p.index] = LXColor.hsb( hue1.getValuef() , saturation1.getValuef(),brightness1.getValuef());
          } else {
            colors[p.index] = LXColor.hsb( hue2.getValuef() , saturation2.getValuef(),brightness2.getValuef());
          }
          ++pixCnt;
        }
      }
    } else if (mode == 5){
      for (LightBar lb : IcosahedronModel.getAllLightBars()) {
        pixCnt = 0;
        for (LXPoint p : lb.points) {
          if (pixCnt % cntVal4 >= cntVal5) {
            colors[p.index] = LXColor.hsb( hue1.getValuef() , saturation1.getValuef(),brightness1.getValuef());
          } else {
            colors[p.index] = LXColor.hsb( hue2.getValuef() , saturation2.getValuef(),brightness2.getValuef());
          }
          ++pixCnt;
        }
      }
    }
  }
}