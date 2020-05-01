package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import noomechanism.icosahedron.IcosahedronModel;
import noomechanism.icosahedron.LightBar;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class GentleSpin extends SpinningPattern {
    private static final float PI = (float) 3.14159265359;

    public String getAuthor() {
        return "Mark C. Slee";
    }

    public GentleSpin(LX lx) {
        super(lx);
    }

    public void run(double deltaMs) {
        float azimuth = this.azimuth.getValuef();
        int pixNum = 0;

        for (LightBar lb : IcosahedronModel.lightBars) {
            for (LXPoint p : lb.points) {

                float az = (float) ((p.azimuth + azimuth + abs(p.yn - .5) * (PI/4)) % (2*PI));

                colors[pixNum]= LXColor.gray(max(0, 100 - 40 * abs(az - PI)));
                ++pixNum;
            }
        }

    }
}
