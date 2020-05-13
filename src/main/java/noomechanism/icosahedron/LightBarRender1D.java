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
      if (depth < 0)
        depth = 0;
      int randomDepth = ThreadLocalRandom.current().nextInt(depth);
      int value = min + randomDepth;
      if (value > 255) {
        value = 255;
      }
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(value, value, value, 255), blend);
    }
  }

  static public void sine(int colors[], LightBar lightBar, float head, float freq, float phase, float min, float depth, LXColor.Blend blend) {
    for (LBPoint pt : lightBar.points) {
      float ptX = pt.lbx / lightBar.length;
      float value = ((float)Math.sin((double)freq * (head - ptX) + phase) + 1.0f)/2.0f;
      value = min + depth * value;
      int color = (int)(value * 255f);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(color, color, color, 255), blend);
    }
  }

  static public void cosine(int colors[], LightBar lightBar, float head, float freq, float phase, float min, float depth, LXColor.Blend blend) {
      for (LBPoint pt : lightBar.points) {
      float ptX = pt.lbx / lightBar.length;
      float value = ((float)Math.cos((double)freq * (head - ptX) + phase) + 1.0f)/2.0f;
      value = min + depth * value;
      int color = (int)(value * 255f);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(color, color, color, 255), blend);
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
    minMax[0] = (float)zeroCrossingTriangleWave(t, slope);
    minMax[1] = (float)zeroCrossingTriangleWave(t, -slope);
    for (LBPoint pt : lightBar.points) {
      int gray = (int)(triangleWave(peakPos, slope, pt.lbx)*255.0*maxValue);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(gray, gray, gray, 255), blend);
    }
    return minMax;
  }

  static public float[] renderSquare(int colors[], LightBar lightBar, float t, float width, float maxValue, LXColor.Blend blend) {
    double barPos = t * lightBar.length;
    float[] minMax = new float[2];
    minMax[0] = t - width/2.0f;
    minMax[1] = t + width/2.0f;
    for (LBPoint pt: lightBar.points) {
      int gray = (int) ((((pt.lbx > minMax[0]*lightBar.length) && (pt.lbx < minMax[1]*lightBar.length))?maxValue:0f)*255.0f);
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
  static public float[] renderStepDecay(int colors[], LightBar lightBar, float t, float width, float slope,
                                     float maxValue, boolean forward, LXColor.Blend blend) {
    float stepPos = t * lightBar.length;
    float[] minMax = stepDecayZeroCrossing(t, width, slope, forward);
    for (LBPoint pt : lightBar.points) {
      int gray = (int) (stepDecayWave(t, width, slope, pt.lbx/lightBar.length, forward)*255.0*maxValue);
      colors[pt.index] = LXColor.blend(colors[pt.index], LXColor.rgba(gray, gray, gray, 255), blend);
    }

    return minMax;
  }

  static public float triWave(float t, float p)  {
      return 2.0f * (float)Math.abs(t / p - Math.floor(t / p + 0.5f));
  }

  static public float[] stepDecayZeroCrossing(float stepPos, float width, float slope, boolean forward) {
    float[] minMax = new float[2];
    float max = stepPos + width/2.0f;
    float min = stepPos - width/2.0f - 1.0f/slope;
    // If our orientation traveling along the bar is backwards, swap our min/max computations.

    float tail = 0f;
    if (forward) {
      tail  = - 1.0f/slope + stepPos - width/2.0f;
    } else {
      tail = 1.0f/slope + stepPos + width/2.0f;
    }

    float head = 0;
    if (forward) {
      head = stepPos + width/2.0f;
    } else {
      head = stepPos - width/2.0f;
    }

    if (forward) {
      minMax[0] = tail;
      minMax[1] = head;
    } else {
      minMax[1] = tail;
      minMax[0] = head;
    }
    /*
    if (forward) {
      minMax[0] = min;
      minMax[1] = max;
    } else {
      minMax[0] = max;
      minMax[1] = min;
    }
    */
    return minMax;
  }

  /**
   * Step wave with attack slope.
   * Returns value from 0.0f to 1.0f
   */
  static public float stepDecayWave(float stepPos, float width, float slope, float x, boolean forward) {
    float value;
    if ((x > stepPos - width/2.0f) && (x < stepPos + width/2.0f))
      return 1.0f;

    if ((x > stepPos + width/2.0f) && forward)
      return 0f;
    else if ((x < stepPos - width/2.0f && !forward))
      return 0f;

    if (forward) {
        value = 1.0f + slope * (x - (stepPos - width/2.0f));
        if (value < 0f) value = 0f;
    } else {
        value = 1.0f - slope * (x - (stepPos + width/2.0f));
        if (value < 0f) value = 0f;
    }
    return value;
  }

  static public double zeroCrossingTriangleWave(double peakX, double slope) {
    return peakX - 1.0/slope;
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
