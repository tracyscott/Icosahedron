package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

import static java.lang.StrictMath.*;
import static org.joml.SimplexNoise.noise;

@LXCategory("Color")
public class RGBThreeColorBlend extends LXPattern {

  final CompoundParameter R1 = new CompoundParameter("R1", 1, 1, 254.);
  final CompoundParameter G1 = new CompoundParameter("G1", 128, 1, 254);
  final CompoundParameter B1 = new CompoundParameter("B1", 1, 1, 254);
  final CompoundParameter R2 = new CompoundParameter("R2", 1, 1, 254);
  final CompoundParameter G2 = new CompoundParameter("G2", 1, 1, 254);
  final CompoundParameter B2 = new CompoundParameter("B2", 128, 1, 254);
  final CompoundParameter R3 = new CompoundParameter("R3", 1, 1, 254);
  final CompoundParameter G3 = new CompoundParameter("G3", 1, 1, 254);
  final CompoundParameter B3 = new CompoundParameter("B3", 128, 1, 254);
  final CompoundParameter A = new CompoundParameter("A", 254, 1, 254);
  final DiscreteParameter Div = new DiscreteParameter("Div", 2, 128, 128);

  public RGBThreeColorBlend(LX lx) {
    super(lx);
    addParameter(R1);
    addParameter(G1);
    addParameter(B1);
    addParameter(R2);
    addParameter(G2);
    addParameter(B2);
    addParameter(R3);
    addParameter(G3);
    addParameter(B3);
    addParameter(A);
    addParameter(Div);
  }

  public void run(double deltaMs) {
    float div =  Div.getValuef();
    float xRange =  model.xMax - model.xMin;
    float xDiv= 1/div;
    float rDiv= 0;
    float gDiv= 0;
    float bDiv= 0;
    float xFactor = xRange/div;
    float midpoint1 = xRange/4;
    float midpoint2 = 3 * xRange/4;
    float rBlend;
    float gBlend;
    float bBlend;
    float aBlend;
    float r1 = R1.getValuef();
    float g1 = G1.getValuef();
    float b1 = B1.getValuef();
    float r2 = R2.getValuef();
    float g2 = G2.getValuef();
    float b2 = B2.getValuef();
    float r3 = R3.getValuef();
    float g3 = G3.getValuef();
    float b3 = B3.getValuef();
    float pxOffset;

    for (LXPoint p : model.points) {
      pxOffset = (xRange/2)- p.x;
      //println("pxOffset: " + pxOffset);
      if (p.x >= 0) {
        if(r1>=r2){
          rDiv = 0;
        } else if (r1 < r2) {
          rDiv = div;
        }
        if(g1>=g2){
          gDiv = 0;
        } else if (g1 < g2){
          gDiv = div;
        }
        if(b1>=b2){
          bDiv = 0;
        } else if (b1 < b2){
          bDiv = div;
        }
        if ( pxOffset <= midpoint1 && pxOffset >= 0) {
          rBlend= (float) (((ceil(abs(rDiv - (p.x+midpoint1)/xFactor)) * xDiv * max(1,abs(r1 - r2))))+ min( r1, r2));
          gBlend= (float) (((ceil(abs(gDiv - (p.x+midpoint1)/xFactor)) * xDiv * max(1,abs(g1 - g2))))+ min( g1, g2));
          bBlend= (float) (((ceil(abs(bDiv - (p.x+midpoint1)/xFactor) )* xDiv * max(1,abs(b1 - b2))))+ min( b1, b2));
          aBlend= A.getValuef();
          colors[p.index]= LXColor.rgba( (int) rBlend,(int)gBlend, (int)bBlend,(int) aBlend);
          // println("1: " + p.x);
        } else if (pxOffset >= midpoint1 && pxOffset <= (xRange/2))  {
          rBlend= (float) ((abs((ceil(-rDiv + (p.x+midpoint1)/xFactor)) * xDiv * max(1,abs(r1 - r2))))+ min( r1, r2));
          gBlend= (float) ((abs((ceil(-gDiv + (p.x+midpoint1)/xFactor)) * xDiv * max(1,abs(g1 - g2))))+ min( g1, g2));
          bBlend= (float) ((abs((ceil(-bDiv + (p.x+midpoint1)/xFactor)) * xDiv * max(1,abs(b1 - b2))))+ min( b1, b2));
          aBlend= A.getValuef();//((ceil((p.x-midpoint)/div) * abs(A1.getValuef() - A2.getValuef()))/xRange)+ min( R1.getValuef(), R2.getValuef());
          colors[p.index]= LXColor.rgba( (int) rBlend,(int)gBlend, (int)bBlend,(int) aBlend);
          // println("2: " + p.x);
        }
      } else if (p.x < 0)  {
        if(r2>=r3){
          rDiv = 0;
        } else if (r2 < r3) {
          rDiv = div;
        }
        if(g2>=g3){
          gDiv = 0;
        } else if (g2 < g3){
          gDiv = div;
        }
        if(b2>=b3){
          bDiv = 0;
        } else if (b2 < b3){
          bDiv = div;
        }
        if (pxOffset <= midpoint2 && pxOffset > (xRange/2)) {
          rBlend= (float) (((ceil(abs(rDiv - (p.x+midpoint2)/xFactor)) * xDiv * max(1,abs(r3 - r2))))+ min( r3, r2));
          gBlend= (float) (((ceil(abs(gDiv - (p.x+midpoint2)/xFactor)) * xDiv * max(1,abs(g3 - g2))))+ min( g3, g2));
          bBlend= (float) (((ceil(abs(bDiv - (p.x+midpoint2)/xFactor) )* xDiv * max(1,abs(b3 - b2))))+ min( b3, b2));
          aBlend= A.getValuef();
          colors[p.index]= LXColor.rgba( (int) rBlend,(int)gBlend, (int)bBlend,(int) aBlend);
          // println("3: " + p.x);
        } else if  (pxOffset >= midpoint2 && pxOffset <= (model.xMax*2)) {
          rBlend= (float) ((abs((ceil(-rDiv + (p.x+midpoint2)/xFactor)) * xDiv * max(1,abs(r3 - r2))))+ min( r3, r2));
          gBlend= (float) ((abs((ceil(-gDiv + (p.x+midpoint2)/xFactor)) * xDiv * max(1,abs(g3 - g2))))+ min( g3, g2));
          bBlend= (float) ((abs((ceil(-bDiv + (p.x+midpoint2)/xFactor)) * xDiv * max(1,abs(b3 - b2))))+ min( b3, b2));
          aBlend= A.getValuef();//((ceil((p.x-midpoint)/div) * abs(A1.getValuef() - A2.getValuef()))/xRange)+ min( R1.getValuef(), R2.getValuef());
          colors[p.index]= LXColor.rgba( (int) rBlend,(int)gBlend, (int)bBlend,(int) aBlend);
          // println("4: " + p.x);
        }
      }
    }
  }
}