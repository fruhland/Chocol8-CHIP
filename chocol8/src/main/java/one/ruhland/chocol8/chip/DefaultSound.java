package one.ruhland.chocol8.chip;

import javax.sound.sampled.*;

public class DefaultSound extends Sound {

    private static final int SAMPLE_RATE = 8000;
    private static final int SAMPLE_SIZE = 8;

    private final Clip clip;
    private boolean isPlaying = false;

    public DefaultSound() throws LineUnavailableException {
        super();

        byte[] audioData = new byte[64];

        for(long i = 0; i < audioData.length; i++) {
            double angle = i / ((float) SAMPLE_RATE / getFrequency()) * 2.0 * Math.PI;
            audioData[0] = (byte) (Math.sin(angle) * 127);
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);

        clip = AudioSystem.getClip();
        clip.open(format, audioData, 0, audioData.length);
    }

    @Override
    protected void startBeep() {
        if(isPlaying) {
            return;
        }

        isPlaying = true;

        new Thread(() -> {
            while(isPlaying) {
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
