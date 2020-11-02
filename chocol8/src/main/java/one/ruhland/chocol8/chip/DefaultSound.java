package one.ruhland.chocol8.chip;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultSound extends Sound {

    private static final int DEFAULT_FREQUENCY = 440;
    private static final int SAMPLE_RATE = 8000;
    private static final int SAMPLE_SIZE = 8;

    private Clip clip;
    private double toneFrequency;
    private boolean isPlaying = false;

    public DefaultSound(Clock clock) {
        super(clock);
        setToneFrequency(DEFAULT_FREQUENCY);
    }

    @Override
    public void setToneFrequency(final double toneFrequency) {
        this.toneFrequency = toneFrequency;
        List<Byte> audioData = new ArrayList<>();

        for (double i = 0; i < 0.1; i += (1.0 / SAMPLE_RATE)) {
            audioData.add((byte) (Math.sin(2 * Math.PI * toneFrequency * i) * 127));
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);

        try {
            byte[] audioArray = new byte[audioData.size()];

            for (int i = 0; i < audioArray.length; i++) {
                audioArray[i] = audioData.get(i);
            }

            clip = AudioSystem.getClip();
            clip.open(format, audioArray, 0, audioArray.length);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getToneFrequency() {
        return toneFrequency;
    }

    @Override
    protected void startBeep() {
        if (isPlaying) {
            return;
        }

        isPlaying = true;

        new Thread(() -> {
            while (isPlaying) {
                clip.loop(1);
            }

            clip.stop();
        }, "DefaultSoundThread").start();
    }

    @Override
    protected void stopBeep() {
        isPlaying = false;
    }
}
