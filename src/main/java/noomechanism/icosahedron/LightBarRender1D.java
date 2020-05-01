package noomechanism.icosahedron;

import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * LightBarRender1D implements a variety of 1D rendering functions that
 * are local to the specified LightBar.
 */
public class LightBarRender1D {
  private static final Logger logger = Logger.getLogger(LightBarRender1D.class.getName());

  static public void randomGray(int colors[], LightBar lightBar, LXColor.Blend blend) {
    Random r = new Random();
    for (LBPoint pt : lightBar.points) {
      int randomValue = r.nextInt(256);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(randomValue, randomValue, randomValue, 255), blend);
    }
  }

  static public void randomGrayBaseDepth(int colors[], LightBar lightBar, LXColor.Blend blend, int min, int depth) {
    for (LBPoint pt : lightBar.points) {
      int randomDepth = ThreadLocalRandom.current().nextInt(depth);
      int value = min + randomDepth;
      if (value > 255) {
        value = 255;
      }
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(value, value, value, 255), blend);
    }
  }

  /**
   * Render a triangle gradient in gray.  t is the 0 to 1 normalized x position.  Slope
   * is the slope of the gradient.
   * TODO(tracy): This should probably be additive on the color so that they can be combined.
   * TODO(tracy): Slope normalization needs to account for led density? i.e. Max slope should include
   * only one led.  Minimum slope should include all leds.
   * @param colors LED colors array.
   * @param lightBar The lightbar to render on.
   * @param t Normalized (0.0-1.0) x position.
   * @param slope The slope of the gradient.  Not normalized currently.
   * @param maxValue Maximum value of the step function (0.0 - 1.0)
   * @param blend Blend mode for writing into the colors array.
   * @return A float array containing the minimum x intercept and maximum x intercept in that order.
   */
  static public float[] renderTriangle(int colors[], LightBar lightBar, float t, float slope, float maxValue, LXColor.Blend blend) {
    double peakPos = t * lightBar.length;
    float[] minMax = new float[2];
    for (LBPoint pt : lightBar.points) {
      int gray = (int)(triangleWave(peakPos, slope, pt.lbx)*255.0*maxValue);
      minMax[0] = (float)zeroCrossingTriangleWave(peakPos, slope);
      minMax[1] = (float)zeroCrossingTriangleWave(peakPos, -slope);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(gray, gray, gray, 255), blend);
    }
    return minMax;
  }

  /**
   * Render a step function at the given position with the given slope.
   * @param colors Points color array to write into.
   * @param lightBar The lightbar to render on.
   * @param t Normalized (0.0-1.0) x position of the step function on the lightbar.
   * @param slope The slope of edge of the step function.
   * @param maxValue Maximum value of the step function (0.0 - 1.0)
   * @param forward Direction of the step function.
   * @param blend Blend mode for writing into the colors array.
   */
  static public void renderStep(int colors[], LightBar lightBar, float t, float slope, float maxValue, boolean forward, LXColor.Blend blend) {
    float stepPos = t * lightBar.length;
    for (LBPoint pt : lightBar.points) {
      int gray = (int) (stepWave(stepPos, slope, pt.lbx, forward)*255.0*maxValue);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(gray, gray, gray, 255), blend);
    }
  }

  static public float triWave(float t, float p)  {
      return 2.0f * (float)Math.abs(t / p - Math.floor(t / p + 0.5f));
  }

  /**
   * Step wave with attack slope.
   * Returns value from 0.0f to 1.0f
   */
  static public float stepWave(float stepPos, float slope, float x, boolean forward) {
    float value;
    if (forward) {
      if (x < stepPos)
        value = 1.0f;
      else {
        value = -slope * (x - stepPos) + 1.0f;
        if (value < 0f) value = 0f;
      }
    } else {
      if (x > stepPos)
        value = 1.0f;
      else {
        value = slope * (x - stepPos) + 1.0f;
        if (value < 0f) value = 0f;
      }
    }
    return value;
  }

  static public double zeroCrossingTriangleWave(double peakX, double slope) {
    return -1.0/slope;
  }

  /**
   * Normalized triangle wave function.  Given position of triangle peak and the
   * slope, return value of function at evalAtX.  If less than 0, clip to zero.
   */
  static public double triangleWave(double peakX, double slope, double evalAtX)
  {
    // If we are to the right of the triangle, the slope is negative
    if (evalAtX > peakX) slope = -slope;
    double y = slope * (evalAtX - peakX) + 1.0f;
    if (y < 0f) y = 0f;
    return y;
  }

  static public void renderColor(int[] colors, LightBar lb, int red, int green, int blue, int alpha) {

    renderColor(colors, lb, LXColor.rgba(red, green, blue, alpha));

  }
  static public void renderColor(int[] colors, LightBar lb, int color) {
    for (LXPoint point: lb.points) {
      colors[point.index] = color;
    }
  }
}
