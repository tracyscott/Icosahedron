package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@LXCategory("Color")
public class ThreeColorPaletteBlend extends LXPattern {

  //global variables
  public double curStepTime = 0.0;
  public int numPix = model.points.length;
  public float[] blend = new float[numPix];
  public float newInc = 0;

  //knobs and such
  public final CompoundParameter hue1 = new CompoundParameter("Hue 1", 1, 1. , 360.);
  public final CompoundParameter sat1 = new CompoundParameter("Sat 1", 100, 0. , 100.);
  public final CompoundParameter bri1 = new CompoundParameter("Bri 1", 100, 0. , 100.);
  public final DiscreteParameter increment = (DiscreteParameter)
          new DiscreteParameter("Size", 50, 1, 1000)
                  .setExponent(2);
  public final CompoundParameter hue2 = new CompoundParameter("Hue 2", 1, 1. , 360.);
  public final CompoundParameter sat2 = new CompoundParameter("Sat 2", 100, 0. , 100.);
  public final CompoundParameter bri2 = new CompoundParameter("Bri 2", 100, 0. , 100.);
  public final CompoundParameter hue3 = new CompoundParameter("Hue 3", 1, 1. , 360.);
  public final CompoundParameter sat3 = new CompoundParameter("Sat 3", 100, 0. , 100.);
  public final CompoundParameter bri3 = new CompoundParameter("Bri 3", 100, 0. , 100.);
  public final BooleanParameter set = new BooleanParameter("Fixed",true);
  public final BooleanParameter direction = new BooleanParameter("Dir",true);
  public final CompoundParameter period = (CompoundParameter)
          new CompoundParameter("Period", 50, 20, 1000)
                  .setExponent(3.0)
                  .setDescription("Speed of the Movement");
  final LXModulator pos = startModulator(new SinLFO(0.9, 1.1, period));

  public ThreeColorPaletteBlend(LX lx) {
    super(lx);
    addParameter(hue1);
    addParameter(sat1);
    addParameter(bri1);
    addParameter(increment);
    addParameter(hue2);
    addParameter(sat2);
    addParameter(bri2);
    addParameter(period);
    addParameter(hue3);
    addParameter(sat3);
    addParameter(bri3);
    addParameter(set);
    addParameter(direction);
  }


  public void run(double deltaMs) {

    curStepTime = curStepTime +deltaMs; //adds time

    int numPixZ = numPix -1 ; // zero based number of pixels
    int cnt = 0; //count value
    int cntReset = 0; //Reset count value
    boolean blendModeUp = true; // if true increment, if false decrement
    boolean s = set.getValueb();
    boolean dir = direction.getValueb();
    float per = period.getValuef();
    float inc = increment.getValuef() /(numPix); //divide by number of pixel for to eliminate a seam
    int cycleReset = Math.round((numPix)/increment.getValuef()) ; //reset accum at set intervals to avoid quantitative error
    int cycleCnt = 0; //represents number of cycles
    float accum ; //accumulator for blend values
    float accumReset = (float) 0.5; //accumulator for blend values
    accum = (float) 0.5; //set inital value of accumulator to ensure there are no seams


    if (s || inc != newInc) {
      for (LXPoint p :  model.points) {
        newInc = inc;

        //increments or decrements
        if (blendModeUp){
          if(cnt != 0){
            accum = accum + inc;
            blend[cnt] = (float) min(1.0,accum); //prevent overflow
          } else {
            blend[cnt] = (float) min(1.0,accum); //don't increment first number
          }
        } else {
          accum = accum - inc;
          blend[cnt] = (float) max(0.0, accum); //prevent underflow
        }


        //evaluate whether cycle is complete
        if (cntReset >= cycleReset){
          accum = accumReset;
          cntReset = 0;
        }

        //sets blend mode
        if (accum >= 1) {
          blendModeUp = false;
          blend[cnt] = 1;
          ++cycleCnt;
          // System.out.println("Going Down: " +cycleCnt);
        } else if (accum <= 0) {
          blendModeUp = true;
          blend[cnt] = 0;
          ++cycleCnt;
          //System.out.println("Going Up: " +cycleCnt);
        }
        ++cnt;


        int c1 = LX.hsb(hue1.getValuef(), sat1.getValuef(),bri1.getValuef());
        int c2 = LX.hsb(hue2.getValuef(), sat2.getValuef(),bri2.getValuef());
        int c3 = LX.hsb(hue3.getValuef(), sat3.getValuef(),bri3.getValuef());

        if (cycleCnt == 0){
          colors[p.index] = LXColor.lerp(c1, c2, blend[p.index]);
        } else if (cycleCnt == 1){
          colors[p.index] = LXColor.lerp(c3, c2, blend[p.index]);
        } else if (cycleCnt == 2){
          colors[p.index] = LXColor.lerp(c3, c1, blend[p.index]);
        } else if (cycleCnt == 3){
          colors[p.index] = LXColor.lerp(c2, c1, blend[p.index]);
        } else if (cycleCnt == 4){
          colors[p.index] = LXColor.lerp(c2, c1, blend[p.index]);
        } else if (cycleCnt == 5){
          colors[p.index] = LXColor.lerp(c2, c3, blend[p.index]);
        }  else {
          colors[p.index] = LXColor.lerp(c1, c2, blend[p.index]);
          cycleCnt = 0;
        }
        ++cntReset;
      }
    } else {

      //controls the period/speed
      if ( curStepTime >= per){
        curStepTime = 0;
        //controls the direction
        if (dir){
          for (LXPoint p :  model.points) {
            if (p.index != numPixZ) {
              blend[p.index] = blend[p.index+1];
            } else {
              blend[p.index] = blend[0];
            }
            int c1 = LX.hsb(hue1.getValuef(), sat1.getValuef(),bri1.getValuef());
            int c2 = LX.hsb(hue2.getValuef(), sat2.getValuef(),bri2.getValuef());

            colors[p.index] = LXColor.lerp(c1, c2, blend[p.index]);
          }
        } else {
          for (LXPoint p :  model.points) {
            if (p.index != numPixZ) {
              blend[numPixZ-p.index] = blend[(numPixZ-p.index)-1];
            } else {
              blend[numPixZ-p.index] =blend[numPixZ];
            }
            int c1 = LX.hsb(hue1.getValuef(), sat1.getValuef(),bri1.getValuef());
            int c2 = LX.hsb(hue2.getValuef(), sat2.getValuef(),bri2.getValuef());

            colors[p.index] = LXColor.lerp(c1, c2, blend[p.index]);
          }
        }
      }
    }
  }
}