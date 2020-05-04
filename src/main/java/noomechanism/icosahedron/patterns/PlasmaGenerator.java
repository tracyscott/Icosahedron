package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.transform.LXVector;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

// This is a helper class to generate plasma.

public class PlasmaGenerator {

  //NOTE: Geometry is FULL scale for this model. Dont use normalized values.

  float xmax, ymax, zmax;
  LXVector circle;

  //sets up table of 255 points that represent a sin wave in radians
  static final LXUtils.LookupTable.Sin sinTable = new LXUtils.LookupTable.Sin(255);

  //methods
  float SinVertical(LXVector p, float size, float movement)
  {
    return sinTable.sin(   ( p.x / xmax / size) + (movement / 100 ));
  }

  float SinRotating(LXVector p, float size, float movement)
  {

    return sinTable.sin((float) (( ( p.y / ymax / size) * sin( movement /66 )) + (p.z / zmax / size) * (cos(movement / 100)))) ;
  }

  float SinCircle(LXVector p, float size, float movement)
  {
    float distance =  p.dist(circle);
    return sinTable.sin( (( distance + movement + (p.z/zmax) ) / xmax / size) * 2 );
  }

  float GetThreeTierPlasma(LXPoint p, float size, float movement)
  {
    LXVector pointAsVector = new LXVector(p);
    return  SinVertical(  pointAsVector, size, movement) +
            SinRotating(  pointAsVector, size, movement) +
            SinCircle( pointAsVector, size, movement);
  }

  //contructor
  public PlasmaGenerator(float _xmax, float _ymax, float _zmax)
  {
    xmax = _xmax;
    ymax = _ymax;
    zmax = _zmax;
    circle = new LXVector(0,0,0);
  }

  //main method
  void UpdateCirclePosition(float x, float y, float z)
  {
    circle.x = x;
    circle.y = y;
    circle.z = z;
  }

}//end plasma generator