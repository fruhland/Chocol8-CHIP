package one.ruhland.chocol8.chip;

public class Clock {

    private double frequency;
    private final String threadName;
    private final Runnable onTick;

    private Thread clockThread;

    private boolean isRunning = false;

    Clock(final double frequency, final String threadName, final Runnable onTick) {
        this.frequency = frequency;
        this.threadName = threadName;
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
        }, threadName);

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

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }
}
