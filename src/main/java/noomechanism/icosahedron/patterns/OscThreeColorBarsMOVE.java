package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.transform.LXVector;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

@LXCategory("Color")
public class OscThreeColorBarsMOVE extends LXPattern {
  private static final float PI = (float) 3.141592653;
  public String getAuthor() {
    return "Fin McCarthy";
  }

  //by Fin McCarthy
  // finchronicity@gmail.com

  //variables
  int brightness = 255;//set brightness to max
  float red, green, blue;
  float shade,shade1, shade2, shade3;
  float movement = (float) 0.1;
  float slice1 =0;
  float slice2 =(2*PI)/3;
  float slice3 =(4*PI)/3;

  //variable calling the helper class
  PlasmaGeneratorY plasmaGenerator;

  long framecount = 0;

  //adjust the size of the plasma
  public final CompoundParameter size =
          new CompoundParameter("Size", 1.0, 0.1, 2.0)
                  .setDescription("Size");

  public final CompoundParameter r1 = new CompoundParameter("Hue1", 0.0, 0.0, 360.0);
  public final CompoundParameter g1 = new CompoundParameter("Sat1", 100.0, 0.0, 100.0);
  public final CompoundParameter b1 = new CompoundParameter("Bri1", 100.0, 0.0, 100.0);
  public final CompoundParameter rate = new CompoundParameter("Rate", 22000.0, 1000.0, 60000.0);
  public final CompoundParameter r2 = new CompoundParameter("Hue2", 0.0, 0.0, 360.0);
  public final CompoundParameter g2 = new CompoundParameter("Sat2", 100.0, 0.0, 100.0);
  public final CompoundParameter b2 = new CompoundParameter("Bri2", 100.0, 0.0, 100.0);
  public final CompoundParameter r3 = new CompoundParameter("Hue3", 0.0, 0.0, 360.0);
  public final CompoundParameter g3 = new CompoundParameter("Sat3", 100.0, 0.0, 100.0);
  public final CompoundParameter b3 = new CompoundParameter("Bri3", 100.0, 0.0, 100.0);
  //public final CompoundParameter slice1 = new CompoundParameter("Slice1", 0.0, 0, PI);
  //public final CompoundParameter slice2 = new CompoundParameter("Slice2", (2*PI)/3, 0, PI);
  //public final CompoundParameter slice3 = new CompoundParameter("Slice3", (4*PI)/3, 0, PI);

  //public final CompoundParameter min = new CompoundParameter("Min", 2.0, 2.0, 20.0);
  //public final CompoundParameter max = new CompoundParameter("Max", 20.0, 2.0, 20.0);*/
  //variable speed of the plasma.
  public final CompoundParameter move = new CompoundParameter("Move", 2.0, 2.0, 20.0);
  public final SinLFO RateLfo = new SinLFO(
          2, //start
          20, //stop
          new FunctionalParameter() {
            public double getValue() {
              return rate.getValue();
            }
          });

  //moves the circle object around in space
  public final SinLFO CircleMoveX = new SinLFO(
          model.xMax*-1,
          model.xMax*2,
          22000//40000
  );

  public final SinLFO CircleMoveY = new SinLFO(
          model.zMax*-1,
          model.zMax*2,
          22000
  );

  private final LXUtils.LookupTable.Sin sinTable = new LXUtils.LookupTable.Sin(255);
  private final LXUtils.LookupTable.Cos cosTable = new LXUtils.LookupTable.Cos(255);


  //constructor
  public OscThreeColorBarsMOVE(LX lx) {
    super(lx);

    addParameter(size);
    addParameter(r1);
    addParameter(g1);
    addParameter(b1);

    startModulator(CircleMoveX);
    startModulator(CircleMoveY);
    startModulator(RateLfo);
    addParameter("rate", this.rate);
    addParameter(r2);
    addParameter(g2);
    addParameter(b2);
    addParameter(r3);
    addParameter(g3);
    addParameter(b3);
    addParameter(move);
    //addParameter(slice1);
    //addParameter(slice2);
    //addParameter(slice3);


    plasmaGenerator =  new PlasmaGeneratorY(model.xMax, model.yMax, model.zMax);
    UpdateCirclePosition();

    //PrintModelGeometory();
  }

  //main method
  public void run(double deltaMs) {
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      //GET A UNIQUE SHADE FOR THIS PIXEL
      //convert this point to vector so we can use the dist method in the plasma generator
      float _size = size.getValuef();
      for (LXPoint p : lb.points) {
        //combine the individual plasma patterns
        LXVector pointAsVector = new LXVector(p);
        shade = plasmaGenerator.GetThreeTierPlasma(p, _size, movement );
        shade1 = plasmaGenerator.MineRotatingDiagonSlice(pointAsVector, _size, movement, slice1 );
        shade2 = plasmaGenerator.MineRotatingDiagonSlice(pointAsVector, _size, movement, slice2 );
        shade3 = plasmaGenerator.MineRotatingDiagonSlice(pointAsVector, _size, movement, slice3 );

        //separate out a red, green and blue shade from the plasma wave
        if (shade1 > 0.5) {
          red = r1.getValuef();
          green = g1.getValuef();
          blue = b1.getValuef();
        } else if (shade2 > 0.5){
          red = r2.getValuef();
          green = g2.getValuef();
          blue = b2.getValuef();
        }  else if(shade3 > 0.5) {
          red = r3.getValuef();
          green = g3.getValuef();
          blue = b3.getValuef();
        }
        colors[p.index]= LXColor.hsb( (int) red,(int)green, (int)blue);
      }
    }

    movement =+ ((float)move.getValue() / 1000); //advance the animation through time. =+ notation means it takes the positive value so this will range from 0.002 to 0.020 over 45s
    UpdateCirclePosition();
  }

  //method to update circle position
  void UpdateCirclePosition()
  {
    plasmaGenerator.UpdateCirclePosition(
            (float)CircleMoveX.getValue(),
            (float)CircleMoveY.getValue(),
            0
    );
  }
}