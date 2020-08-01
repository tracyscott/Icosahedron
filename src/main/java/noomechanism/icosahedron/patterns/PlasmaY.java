package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static processing.core.PApplet.map;

@LXCategory("Color")
public class PlasmaY extends LXPattern {
  private static final float PI = (float) 3.141592653;
  public String getAuthor() {
    return "Fin McCarthy";
  }

  //by Fin McCarthy
  // finchronicity@gmail.com

  //variables
  int brightness = 255;//set brightness to max
  float red, green, blue;
  float shade;
  float movement = (float) 0.1;

  //variable calling the helper class
  PlasmaGenerator plasmaGenerator;

  //adjust the size of the plasma
  public final CompoundParameter size =
          new CompoundParameter("Size", 0.8, 0.1, 1)
                  .setDescription("Size");

  //variable speed of the plasma.
  public final SinLFO RateLfo = new SinLFO(
          2,
          20,
          45000
  );

  //moves the circle object around in space
  public final SinLFO CircleMoveX = new SinLFO(
          model.xMax*-1,
          model.xMax*2,
          40000
  );

  public final SinLFO CircleMoveZ = new SinLFO(
          model.xMax*-1,
          model.zMax*2,
          22000
  );

  private final LXUtils.LookupTable.Sin sinTable = new LXUtils.LookupTable.Sin(255);
  private final LXUtils.LookupTable.Cos cosTable = new LXUtils.LookupTable.Cos(255);

  //constructor
  public PlasmaY(LX lx) {
    super(lx);

    addParameter(size);

    startModulator(CircleMoveX);
    startModulator(CircleMoveZ);
    startModulator(RateLfo);

    plasmaGenerator =  new PlasmaGenerator(model.xMax, model.yMax, model.zMax);
    UpdateCirclePosition();
  }

  //main method
  public void run(double deltaMs) {
    //System.out.println("frame rate: " + Math.round(1000/deltaMs));
    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      //GET A UNIQUE SHADE FOR THIS PIXEL

      //convert this point to vector so we can use the dist method in the plasma generator
      float _size = size.getValuef();
      for (LXPoint p : lb.points) {
        //combine the individual plasma patterns
        shade = plasmaGenerator.GetThreeTierPlasma(p, _size, movement );

        //separate out a red, green and blue shade from the plasma wave
        red = map(sinTable.sin(shade*PI), -1, 1, 0, brightness);
        green =  map(sinTable.sin(shade*PI+(2*cosTable.cos(movement*490))), -1, 1, 0, brightness); //*cos(movement*490) makes the colors morph over the top of each other
        blue = map(sinTable.sin(shade*PI+(4*sinTable.sin(movement*300))), -1, 1, 0, brightness);

        //ready to populate this color!
        colors[p.index]= LXColor.rgba( (int) red,(int)green, (int)blue,254);
        //setColor(p, LXColor.rgb((int)red,(int)green, (int)blue));
      }
    }

    movement =+ ((float)RateLfo.getValue() / 1000); //advance the animation through time.

    UpdateCirclePosition();

  }

  //method to update circle position
  void UpdateCirclePosition()
  {
    plasmaGenerator.UpdateCirclePosition(
            (float)CircleMoveX.getValue(),
            (float)CircleMoveZ.getValue(),
            0
    );
  }


}