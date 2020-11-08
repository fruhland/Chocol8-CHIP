package one.ruhland.chocol8.chip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class Clock {

    private double frequency;
    private final String threadName;
    private final List<Runnable> onTick = new ArrayList<>();

    private Thread clockThread;

    private boolean isRunning = false;

    Clock(final double frequency, final String threadName) {
        if (frequency < 1) {
            throw new IllegalArgumentException("The frequency must be positive number greater than zero!");
        }

        this.frequency = frequency;
        this.threadName = threadName;
    }

    void addRunnable(Runnable runnable) {
        onTick.add(runnable);
    }

    void start() {
        isRunning = true;

        clockThread = new Thread(() -> {
            while (isRunning) {
                long start = System.nanoTime();

                for (Runnable runnable : onTick) {
                    runnable.run();
                }

                long end = System.nanoTime();
                double sleepTime = (1.0 / frequency) * 1000000000 - (end - start);

                long slept = 0;
                while (sleepTime > slept) {
                    LockSupport.parkNanos((long) (sleepTime - slept));
                    slept = System.nanoTime() - end;
                }
            }
        }, threadName);

        clockThread.start();
    }

    void stop() {
        isRunning = false;

        if (clockThread == null) {
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
