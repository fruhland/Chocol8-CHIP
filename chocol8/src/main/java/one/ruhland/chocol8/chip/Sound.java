package one.ruhland.chocol8.chip;

public abstract class Sound {

    private byte counter = 0;

    protected Sound(Clock clock) {
        clock.addRunnable(() -> {
            if (counter != 0) {
                setCounter((byte) (counter - 1));
            }
        });
    }

    void reset() {
        counter = 0;
    }

    public void setCounter(final byte counter) {
        byte oldCounter = this.counter;
        this.counter = counter;

        if (counter == 0) {
            stopBeep();
        } else if (oldCounter == 0) {
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
