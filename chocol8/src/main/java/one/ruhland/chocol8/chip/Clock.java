package one.ruhland.chocol8.chip;

public class Clock {

    private final Runnable onTick;

    private double frequency;
    private Thread clockThread;

    private boolean isRunning = false;

    Clock(final double frequency, final Runnable onTick) {
        this.frequency = frequency;
        this.onTick = onTick;
    }

    void start() {
        isRunning = true;

        clockThread = new Thread(() -> {
            while(isRunning) {
                long start = System.nanoTime();

                onTick.run();

                long end = System.nanoTime();
                double sleepTime = (1.0 / frequency) * 1000000000 - (end - start);

                long slept = 0;
                while (sleepTime > slept) {
                    slept = System.nanoTime() - end;
                }
            }
        });

        clockThread.start();
    }

    void stop() {
        isRunning = false;

        if(clockThread == null) {
            return;
        }

        try {
            clockThread.join();
        } catch (InterruptedException ignored) {}
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
