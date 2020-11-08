package one.ruhland.chocol8.chip;

public class Timer {

    private byte counter = 0;

    Timer(Clock clock) {
        clock.addRunnable(() -> {
            if (counter != 0) {
                counter--;
            }
        });
    }

    void reset() {
        counter = 0;
    }

    public void setCounter(final byte counter) {
        this.counter = counter;
    }

    public byte getCounter() {
        return counter;
    }
}
