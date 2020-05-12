package noomechanism.icosahedron;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BarSets {

  public static final int SET_TOP_TRIANGLE = 0;
  public static final int SET_TOP_ATTACHED_TRIANGLES = 1;
  public static final int SET_TOP_HALF = 2;
  public static final int SET_TOP_HALF_NO_TRI = 3;
  public static final int SET_BOTTOM_HALF_NO_TRI = 4;
  public static final int SET_BOTTOM_HALF = 5;
  public static final int SET_BOTTOM_ATTACHED_TRIANGLES = 6;
  public static final int SET_BOTTOM_TRIANGLE = 7;

  public static int[] topTriangleSetIds = {24, 10, 19};

  public static int[] topHalfSetIds = {24, 10, 19, 29, 25, 26, 20, 12, 11, 5, 1, 0, 4, 9,
      18, 17, 23, 28};

  public static int[] topAttachedTrisIds = {24, 10, 19, 29, 25, 11, 5, 9, 18};

  public static int[] topHalfNoTriIds = {29, 25, 26, 20, 12, 11, 5, 1, 0, 4, 9,
      18, 17, 23, 28};

  public static int[] bottomHalfNoTriIds = {16, 22, 28, 27, 26, 21, 13, 12, 6, 2,
      3, 1, 4, 8, 17};

  public static int[] bottomHalfSetIds = {15, 14, 7, 16, 22, 28, 27, 26, 21, 13, 12, 6, 2,
      3, 1, 4, 8, 17};

  public static int[] bottomAttachedTrisIds = {15, 14, 7, 16, 22, 21, 13, 2, 3};

  public static int[] bottomTriangleSetIds = { 15, 14, 7};

  public static int[][] allSets = {
      topTriangleSetIds,
      topAttachedTrisIds,
      topHalfSetIds,
      topHalfNoTriIds,
      bottomHalfNoTriIds,
      bottomHalfSetIds,
      bottomAttachedTrisIds,
      bottomTriangleSetIds,
  };

  public static List<LightBar> getSet(int whichSet) {
    return getSetFromIds(allSets[whichSet]);
  }

  public static List<LightBar> getSetFromIds(int[] ids) {
    List<LightBar> lightBars = new ArrayList();
    Set<Integer> set = new HashSet<Integer>();
    for (int i = 0; i < ids.length; i++)
      set.add(new Integer(ids[i]));
    for (LightBar lb : IcosahedronModel.lightBars) {
      if (set.contains(lb.barNum))
        lightBars.add(lb);
    }
    return lightBars;
  }
}
