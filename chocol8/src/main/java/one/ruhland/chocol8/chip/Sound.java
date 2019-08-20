package one.ruhland.chocol8.chip;

public abstract class Sound {

    private static final double FREQUENCY = 60;
    private static final double DEFAULT_TONE_FREQUENCY = 440;

    private final Clock clock;
    private byte counter = 0;
    private double toneFrequency = DEFAULT_TONE_FREQUENCY;

    protected Sound() {
        clock = new Clock(FREQUENCY, "SoundThread", () -> {
            if(counter != 0) {
                if(--counter == 0) {
                    stopBeep();
                }
            }
        });
    }

    void reset() {
        counter = 0;
    }

    void start() {
        clock.start();
    }

    void stop() {
        clock.stop();
    }

    public void setCounter(final byte counter) {
        this.counter = counter;

        if(counter == 0) {
            stopBeep();
        } else {
            startBeep();
        }
    }

    public byte getCounter() {
        return counter;
    }

    public void setFrequency(final double toneFrequency) {
        this.toneFrequency = toneFrequency;
    }

    public double getFrequency() {
        return toneFrequency;
    }

    protected abstract void startBeep();

    protected abstract void stopBeep();
}
