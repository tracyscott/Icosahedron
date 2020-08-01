package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.CompoundParameter;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.StrictMath.random;

@LXCategory("Pattern")
public class BigWillSnow extends LXPattern {
  private static final double INCHES = 12 ;
  private static final int FEET = 1;

  private static class Flake {
    public int c;
    public float r;
    public float x;
    public float z;
    private LXPeriodicModulator yMod;
    private BigWillSnow pat;

    public Flake(BigWillSnow pat) {
      this.pat = pat;
      this.c =0xFFFFFF;
      this.r = (float) (3.0*INCHES);
      this.yMod = new SawLFO(1.0*INCHES + 15 * FEET *2.0, -1.0*INCHES, 0).randomBasis();
      newValues();
      pat.startModulator(this.yMod);
    }

    public void run() {
      if (this.yMod.loop()) {
        newValues();
      }
    }

    private void newValues() {
      newX();
      newModPeriod();
      newZ();
    }

    private void newModPeriod() {
      this.yMod.setPeriod(this.pat.rate.getValuef() * (0.9 + 0.2 * (float) random()));
    }

    private void newX() {
      this.x = (float) (-2.0 * 15*FEET + 15*FEET * 4.0 * (float) random());
    }

    private void newZ() {
      this.z = (float) (-2.0 * 15*FEET + 15*FEET * 4.0 * (float) random());
    }
  }

  private final static int MAX_FLAKES = 1000;

  public final CompoundParameter rate =
          new CompoundParameter("Rate", 3500, 1, 20000)
                  .setDescription("Average rate at which the snow flakes fall");

  public final CompoundParameter numFlakes =
          new CompoundParameter("Flakes", 500, 100, MAX_FLAKES)
                  .setDescription("Number of snow flakes");

  private final List<Flake> flakes = new ArrayList<>();

  private float bucket(float x, float y, float z) {
    return 17 * (float) Math.floor(x * 3*INCHES) + 13 * (float) Math.floor(y * 3*INCHES) + 11 * (float) Math.floor(z * 3*INCHES);
  }

  public BigWillSnow(LX lx) {
    super(lx);
    addParameter(this.rate);
    addParameter(this.numFlakes);

    for (int i = 0; i < MAX_FLAKES; i++) {
      flakes.add(new Flake(this));
    }
  }

  public void run(double deltaMs) {
    HashMap<Float, Flake> flakeMap = new HashMap<>();

    for (int i = 0; i < (int) this.numFlakes.getValuef(); i++) {
      Flake f = flakes.get(i);
      f.run();
      flakeMap.put(bucket(f.x, f.yMod.getValuef(), f.z), f);
    }

    for (LightBar lb : IcosahedronModel.getAllLightBars()) {
      for (LXPoint p : lb.points) {
        int c = 0x000000;

        Flake f = flakeMap.get(bucket(p.x, p.y, p.z));
        if (f != null) {
          // System.out.println("flake hit! " + leaf.toString() + "flake: " + flake.toString());
          c = f.c;
        }
        setColor(p.index, c);
        //setColor(rail, c);
      }
    }
  }
}