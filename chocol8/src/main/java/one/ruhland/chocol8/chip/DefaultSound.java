package one.ruhland.chocol8.chip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class DefaultSound extends Sound {

    private static final float SAMPLE_RATE = 8000;
    private static final int SAMPLE_SIZE = 8;

    private final SourceDataLine sourceDataLine;

    private boolean isPlaying = false;

    public DefaultSound() throws LineUnavailableException {
        super();

        final AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
        sourceDataLine = AudioSystem.getSourceDataLine(format);
        sourceDataLine.open(format);
    }

    @Override
    void startBeep() {
        isPlaying = true;

        new Thread(() -> {
            byte[] soundData = new byte[1];

            sourceDataLine.start();

            for(long i = 0; isPlaying; i++) {
                double angle = i / (SAMPLE_RATE / getFrequency()) * 2.0 * Math.PI;
                soundData[0] = (byte) (Math.sin(angle) * 127);
                sourceDataLine.write(soundData, 0, 1);
            }

            sourceDataLine.drain();
            sourceDataLine.stop();
        }).start();
    }

    @Override
    void stopBeep() {
        isPlaying = false;
    }
}
