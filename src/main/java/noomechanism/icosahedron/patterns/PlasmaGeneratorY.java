package noomechanism.icosahedron.patterns;

import heronarts.lx.LXUtils;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static processing.core.PApplet.map;

// This is a helper class to generate plasma and many different random patterns. 

public class PlasmaGeneratorY {
  private static final float PI = (float) 3.141592653;
  //NOTE: Geometry is FULL scale for this model. Dont use normalized values. 

  float xmax, ymax, zmax;
  float rmax, emax, amax;
  LXVector circle;
  //sets up table of 255 points that represent a sin wave in radians
  static final LXUtils.LookupTable.Sin sinTable = new LXUtils.LookupTable.Sin(255);
  static final LXUtils.LookupTable.Cos cosTable = new LXUtils.LookupTable.Cos(255);

  //methods
  float SinVertical(LXVector p, float size, float movement)
  {
    return sinTable.sin(   ( p.x / xmax / size) + (movement / 100 ));
  }

  float SinHorizontal(LXVector p, float size, float movement)
  {
    return (float) sin(   ( map(p.y / (ymax),0,1,-1,1)/ size) + (movement / 100 ));
  }

  float MineRotating(LXVector p, float size, float movement)
  {

    return sinTable.sin( PI*((p.y / ymax / size) )+((map(movement,(float) .002,(float).020,-12*PI, 12*PI))));//* (sin(map(movement,.002,.020,0,2*PI)  )))) ;
  }
  float MineRotatingPI(LXVector p, float size, float movement)
  {

    return sinTable.sin(PI+(PI*((p.y / ymax / size) )+((map(movement,(float).002,(float).020,-12*PI, 12*PI)))));//* (sin(map(movement,.002,.020,0,2*PI)  )))) ;
  }
  float MineRotatingDiagon(LXVector p, float size, float movement)
  {

    return sinTable.sin(map(p.x/xmax/size,-1,1,-PI,PI)+(PI*((p.y / ymax / size) )+((map(movement,(float).002,(float).020,-12*PI, 12*PI)))));//* (sin(map(movement,.002,.020,0,2*PI)  )))) ;
  }
  float MineRotatingDiagonSlice(LXVector p, float size, float movement,float offset)
  {

    return sinTable.sin(offset+(map(p.x/xmax/size,-1,1,-PI,PI)+(PI*((p.y / ymax / size) )+((map(movement,(float).002,(float).020,-12*PI, 12*PI))))));//* (sin(map(movement,.002,.020,0,2*PI)  )))) ;
  }

  float MineRotatingDiagonSliceSin(LXVector p, float size, float movement,float offset)
  {

    float pStretched = map(p.x / xmax,-1,1,0, xmax * 2); // normalized coordinates
    float oneCycle = xmax * 2  * size; //size of a single cycle


    if((pStretched > oneCycle) || (pStretched > offset)){
      oneCycle = 0;
      //System.out.println("pStretched: " + pStretched + " oneCycle: " +oneCycle+ "va " +map(oneCycle/size,0, xmax * 2,-1,1));
    } else {
      oneCycle = pStretched + offset;
    }

    //System.out.println("pStretched: " + pStretched + " oneCycle: " +oneCycle);

    float theCycle = map(oneCycle/size,0, xmax * 2,-PI,PI);
    return cosTable.cos(theCycle) ;//* (sin(map(movement,.002,.020,0,2*PI)  )))) ;
  }

  float SinTrim(LXVector p, float size, float movement,float offset,float shapeWindow)
  {
    float posVal = map(p.x/xmax/size,-1,1,1, 1000);
    //float shapeWindow = 750;
    float finalVal;
    float shape;
    float animStart = (posVal + movement +offset) % 1000; //start of the animation

    if ( animStart <= shapeWindow){
      shape = cosTable.cos(map(shapeWindow - animStart, 0, shapeWindow, -PI, PI));
      finalVal = map(shape,-1,1,0,255);

    } else {
      finalVal= 0;
    }

    return finalVal ;
  }

  float SinTrimY(LXVector p, float size, float movement,float offset,float shapeWindow, float diagon)
  {
    float xDist = p.x/xmax/size;
    float yDist = p.y/ymax/size;
    float posVal = map(yDist,0,1,1, 1000);
    float xVal = map(xDist,-1,1,1,1000);
    float finalVal;
    float shape;
    float diagonAdjust = xVal * diagon;
    float animStart = (posVal + movement +offset + diagonAdjust) % 1000; //start of the animation

    if ( animStart <= shapeWindow){
      shape = cosTable.cos(map ((shapeWindow - animStart), 0, shapeWindow, -PI, PI));
      finalVal = map(shape,-1,1,0,255);

    } else {
      finalVal= 0;
    }

    return finalVal ;
  }

  float SinRadius(LXVector p, float size, float movement,float offset,float shapeWindow)
  {
    LXPoint lxp = p.point;
    float posVal = map(lxp.rn/size,0,1,1, 1000);
    //float shapeWindow = 750;
    float finalVal ;
    float shape;
    float animStart = (posVal + movement +offset) % 1000; //start of the animation

    if ( animStart <= shapeWindow){
      shape = cosTable.cos(map(shapeWindow - animStart, 0, shapeWindow, -PI, PI));
      finalVal = map(shape,-1,1,0,255);

    } else {
      finalVal= 0;
    }

    return finalVal ;
  }

  float SinRadiusXY(LXVector p, float size, float movement,float offset,float shapeWindow)
  {
    LXPoint lxp = p.point;
    float posVal = map(lxp.rxy/xmax/size,0,1,1, 1000);
    float finalVal;
    float shape;
    float animStart = (posVal + offset) % 1000;//(posVal + movement +offset) % 1000; //start of the animation

    if ( animStart <= shapeWindow){
      shape = cosTable.cos(map(shapeWindow - animStart, 0, shapeWindow, -PI, PI));
      finalVal = map(shape,-1,1,0,255);

    } else {
      finalVal= 0;
    }

    return finalVal ;
  }

  float CylinderXY(LXVector p, float size, float xOff,float zOff)
  {
    LXPoint lxp = p.point;
    float finalVal;
    float xCoord,zCoord;
    xCoord = (float) (xOff + (size * sin (lxp.theta)));
    zCoord = (float) (zOff + (size * cos (lxp.theta)));

    if ( Math.abs(lxp.x) < Math.abs(xCoord) && Math.abs(lxp.z) < Math.abs(zCoord)) {
      finalVal = 255; //map(shape,-1,1,0,255);

    } else {
      finalVal= 0;
    }

    return finalVal ;
  }

  float SinEllipsoid(LXVector p, float size, float movement,float offset, boolean animateSwitch)//oval shaped sphere
  {
    //this is hacky AF
    LXPoint lxp = p.point;
    float finalVal;
    float radius1 = 250;//offset;
    float radius2 = 350;//shapeWindow;
    float shape;
    float animate; // determine whether animation will be controlled by offset or movement

    if (animateSwitch) {
      animate = movement;
    } else {
      animate = offset;
    }

    float ring1Min = map(animate,1,1000, 6-size,36-size);
    float ring1Max = map(animate,1,1000, 6+size,36+size);
    float ring2Min = map(animate,1,1000,15-size,26-size);
    float ring2Max = map(animate,1,1000,15+size,26+size);
    float ring3Min = map(animate,1,1000,36-size, 6-size);
    float ring3Max = map(animate,1,1000,36+size, 6+size);

    //if statement to decide where ring values will be
    if ( (lxp.r < radius1) && (p.y > ring1Min) && (p.y <= ring1Max)) {
      shape = sinTable.sin(map(p.y, ring1Min, ring1Max, -PI/2, (3*PI/2)));
      finalVal = map(shape,-1,1,0,255);
    } else if ( (lxp.r > radius1) && (lxp.r < radius2) && (p.y > ring2Min) && (p.y <= ring2Max)){
      shape = sinTable.sin(map(p.y, ring2Min, ring2Max, -PI/2, (3*PI/2)));
      finalVal = map(shape,-1,1,0,255);
    } else if ( (lxp.r > radius2) && (p.y >= ring3Min) && (p.y <= ring3Max)){
      shape = sinTable.sin(map(p.y, ring3Min, ring3Max, -PI/2, (3*PI/2)));
      finalVal = map(shape,-1,1,0,255);
    } else {
      finalVal = 0;
    }

    //this code is pretty dope, right?  
    return finalVal ;
  }
  float SinRotating(LXVector p, float size, float movement)
  {

    return sinTable.sin((float) (( ( p.z / zmax / size) * sin( movement /66 )) + (p.y / ymax / size) * (cos(movement / 100)))) ;
  }
  float SinCircle(LXVector p, float size, float movement)
  {
    float distance =  p.dist(circle);
    return sinTable.sin( (( distance + movement + (p.y/ymax) ) / xmax / size) * 2 );
  }

  float GetThreeTierPlasma(LXPoint p, float size, float movement)
  {
    LXVector pointAsVector = new LXVector(p);
    return  SinVertical(  pointAsVector, size, movement) +
            SinRotating(  pointAsVector, size, movement) +
            SinCircle( pointAsVector, size, movement);
  }

  //contructor
  public PlasmaGeneratorY(float _xmax, float _ymax, float _zmax)
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