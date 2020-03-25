package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.CompoundParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility base class for patterns that are based on animation phases.
 */
public abstract class AnimPattern extends LXPattern {

  protected float time = 0.0f;
  protected float curPhaseDuration = 0.0f;

  int curAnimPhase;
  List<String> phaseNames = new ArrayList<String>();
  List<CompoundParameter> phaseDurations = new ArrayList<CompoundParameter>();

  public AnimPattern(LX lx) {
    super(lx);
  }

  /**
   * Registers an animation phase.
   * @param phaseName The name of the phase.
   * @param phaseTime The default time length of the phase.  A knob will be created for tuning.
   * @param maxPhaseTime The maximum phase duration.
   * @param description Description to be shown for the time knob.
   */
  protected void registerPhase(String phaseName, float phaseTime, float maxPhaseTime, String description) {
    phaseNames.add(phaseName);
    CompoundParameter p = new CompoundParameter(phaseName, phaseTime, 0.1f, maxPhaseTime).setDescription(description);
    phaseDurations.add(p);
    addParameter(p);
  }

  public String getCurrentPhaseName() {
    if (curAnimPhase < phaseNames.size())
      return phaseNames.get(curAnimPhase);
    else
      return "";
  }

  /**
   *  Allow manual specification of R, G, B.  Useful for testing lights for color correction purposes.
   * @param deltaMs
   */
  public void run(double deltaMs) {
    boolean resetTime = false;
    curPhaseDuration = phaseDurations.get(curAnimPhase).getValuef();
    float percentDone = time / curPhaseDuration;

    // First we iterate over all the points and potentially update intensity and kelvins.  Note, that
    // some calls to assignLightColor don't result in an update.  For example, if we are just in an
    // animation phase that is holding a value.
    runPhase(curAnimPhase, deltaMs);
    resetTime = updateAnimPhase();
    if (resetTime) {
      time = 0.0f;
      System.out.println("Changing anim phase to: " + phaseNames.get(curAnimPhase));
    } else {
      time += deltaMs / 1000f;
    }
  }

  abstract public void runPhase(int curAnimPhase, double deltaMs);

  public boolean updateAnimPhase() {
    float curPhaseLength = phaseDurations.get(curAnimPhase).getValuef();
    if (time >= curPhaseLength) {
      curAnimPhase++;
      if (curAnimPhase >= phaseNames.size())
        curAnimPhase = 0;
      return true;
    }
    return false;
  }
}
