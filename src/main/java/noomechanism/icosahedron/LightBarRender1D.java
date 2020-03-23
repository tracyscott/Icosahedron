package noomechanism.icosahedron;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;

import java.util.Random;

/**
 * LightBarRender1D implements a variety of 1D rendering functions that
 * are local to the specified LightBar.
 */
public class LightBarRender1D {

  static public void randomGray(int colors[], LightBar lightBar) {
    Random r = new Random();
    for (LBPoint pt : lightBar.points) {
      int randomValue = r.nextInt(256);
      colors[pt.index] = LXColor.rgba(randomValue, randomValue, randomValue, 255);
    }
  }
}
