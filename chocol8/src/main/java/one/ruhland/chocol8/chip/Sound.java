package one.ruhland.chocol8.chip;

public abstract class Sound {

    private static final double FREQUENCY = 60;
    private static final double DEFAULT_TONE_FREQUENCY = 440;

    private final Clock clock;
    private byte counter = 0;
    private double toneFrequency = DEFAULT_TONE_FREQUENCY;

    protected Sound() {
        clock = new Clock(FREQUENCY, "SoundThread");
        clock.addRunnable(() -> {
            if(counter != 0) {
                setCounter((byte) (counter - 1));
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
        byte oldCounter = this.counter;
        this.counter = counter;

        if(counter == 0) {
            stopBeep();
        } else if(oldCounter == 0) {
            startBeep();
        }
    }

    public byte getCounter() {
        return counter;
    }

    public abstract void setToneFrequency(final double toneFrequency);

    public abstract double getToneFrequency();

    protected abstract void startBeep();

    protected abstract void stopBeep();
}
