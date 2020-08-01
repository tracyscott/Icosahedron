package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SawLFO;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static java.lang.Math.max;

public class Tumbler extends LXPattern {
    private static final float PI = (float) 3.14159265359;

    public String getAuthor() {
        return "Mark C. Slee";
    }

    private LXModulator azimuthRotation = startModulator(new SawLFO(0, 1, 15000).randomBasis());
    private LXModulator thetaRotation = startModulator(new SawLFO(0, 1, 13000).randomBasis());

    public Tumbler(LX lx) {
        super(lx);
    }


    public void run(double deltaMs) {
        float azimuthRotation = this.azimuthRotation.getValuef();
        float thetaRotation = this.thetaRotation.getValuef();
        int pixnum = 0;
        int strandnum = 0;
        int final_num = 0;
        for (LightBar lb : IcosahedronModel.getAllLightBars()) {
            for (LXPoint p : lb.points) {
                float tri1 = LXUtils.trif(azimuthRotation + p.azimuth / PI);
                float tri2 = LXUtils.trif(thetaRotation + (PI + p.theta) / PI);
                float tri = max(tri1, tri2);
                final_num = (strandnum *64) + pixnum;
                colors[final_num]= LXColor.gray(100 * tri * tri);
                ++pixnum;
            }
        }
        
    }
}
