package noomechanism.icosahedron.patterns;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.midi.*;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import noomechanism.icosahedron.Blob;

import java.util.logging.Logger;

public class DrumKit extends ColorPattern {
  private static final Logger logger = Logger.getLogger(DrumKit.class.getName());
  public static final int MAX_INSTRUMENTS = 16;
  public static final int FX_NONE = 0;
  public static final int FX_SPARKLE = 1;
  public static final int FX_COSINE = 2;

  public CompoundParameter slope = new CompoundParameter("slope", 1.0, 0.001, 30.0)
      .setDescription("Determines base size of an instrument blob.");

  public CompoundParameter speed = new CompoundParameter("speed", 1.0, 0.0, 10.0)
      .setDescription("Speed that instruments are moving.");

  public DiscreteParameter numInstruments = new DiscreteParameter("numInst", 1, MAX_INSTRUMENTS, MAX_INSTRUMENTS + 1);

  public DiscreteParameter nextBarKnob = new DiscreteParameter("nxtBar", -1, -1, 4);

  public CompoundParameter widthKnob = new CompoundParameter("width", 0.1f, 0.0f, 10.0f).setDescription("Square wave width");

  public Blob[] blobs = new Blob[MAX_INSTRUMENTS];

  public DrumKit(LX lx) {
    super(lx);
    addParameter(fpsKnob);
    addParameter(fbang);
    addParameter(bangOn);
    addParameter(bangFrames);
    addParameter(bangClear);
    addParameter(bangFade);
    addParameter(paletteKnob);
    addParameter(randomPaletteKnob);
    addParameter(hue);
    addParameter(saturation);
    addParameter(bright);
    addParameter(slope);
    addParameter(speed);
    addParameter(nextBarKnob);
    addParameter(widthKnob);
    resetBlobs();
  }

  public void resetBlobs() {
    for (int i = 0; i < MAX_INSTRUMENTS; i++) {
      blobs[i] = new Blob();
      blobs[i].reset(i%30, 0.0f, 0f, true);
      blobs[i].color = getNewRGB();
      blobs[i].enabled = false;
    }
  }

  @Override
  public void onActive() {
    resetBlobs();
  }

  @Override
  public void renderFrame(double deltaMs) {
    for (LXPoint pt : lx.getModel().points) {
      colors[pt.index] = LXColor.rgba(0,0,0, 255);
    }

    float fadeLevel = 1f;
    if (bangIsRunning())
      fadeLevel = bangFadeLevel();

    for (int i = 0; i < numInstruments.getValuei(); i++) {
      blobs[i].renderBlob(colors, speed.getValuef(), widthKnob.getValuef(), slope.getValuef(), fadeLevel,
          Blob.WAVEFORM_SQUARE, nextBarKnob.getValuei(), false, FX_SPARKLE, 0.5f,
          1f);
    }
  }

  public void noteOnReceived(MidiNoteOn note) {
    int channelNum = note.getChannel();
    // Enable the corresponding blob
    //logger.info("noteOn: " + channelNum);
    if (channelNum == 1) {
      for (Blob blob : blobs) {
        blob.enabled = true;
        float intensity = (float)note.getVelocity()/127f;
        blob.intensity = intensity;
      }
    }
    //blobs[channelNum].enabled = true;
  }

  public void noteOffReceived(MidiNote note) {
    int channelNum = note.getChannel();
    //logger.info("noteOff: " + channelNum);
    //blobs[channelNum].enabled = false;
    if (channelNum == 1) {
      for (Blob blob : blobs) {
        blob.enabled = false;
        blob.intensity = 1f;
      }
    }
  }

  public void afterTouchReceived(MidiAftertouch aftertouch) {
  }

  public void controlChangeReceived(MidiControlChange cc) {
  }

  public void pitchBendReceived(MidiPitchBend pitchBend) {
  }

  public void programChangeReceived(MidiProgramChange pc) {

  }

}
