package one.ruhland.chocol8.chip;

public class Timer {

    private static final double FREQUENCY = 60;

    private Clock clock;
    private byte counter = 0;

    Timer() {
        clock = new Clock(FREQUENCY, () -> {
            if(counter != 0) {
                counter--;
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
    }

    public byte getCounter() {
        return counter;
    }
}
