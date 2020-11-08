package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Field frequencyField = Clock.class.getDeclaredField("frequency");
        Field threadNameField = Clock.class.getDeclaredField("threadName");
        Field isRunningField = Clock.class.getDeclaredField("isRunning");
        Field onTickField = Clock.class.getDeclaredField("onTick");
        frequencyField.setAccessible(true);
        threadNameField.setAccessible(true);
        isRunningField.setAccessible(true);
        onTickField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");

        double frequency = (double) frequencyField.get(clock);
        String threadName = (String) threadNameField.get(clock);
        boolean isRunning = (boolean) isRunningField.get(clock);
        List<Runnable> onTick = (List<Runnable>) onTickField.get(clock);

        assertEquals(100, frequency);
        assertEquals("TestThread", threadName);
        assertFalse(isRunning);
        assertTrue(onTick.isEmpty());
    }

    @Test
    public void testConstructorZeroFrequency() {
        assertThrows(IllegalArgumentException.class, () -> new Clock(0, "TestThread"));
    }

    @Test
    public void testConstructorNegativeFrequency() {
        assertThrows(IllegalArgumentException.class, () -> new Clock(-1, "TestThread"));
    }

    @Test
    public void testGetFrequency() {
        Clock clock = new Clock(100, "TestThread");

        assertEquals(100, clock.getFrequency());
    }

    @Test
    public void testSetFrequency() {
        Clock clock = new Clock(100, "TestThread");
        clock.setFrequency(200);

        assertEquals(200, clock.getFrequency());
    }

    @Test
    public void testAddRunnable() throws NoSuchFieldException, IllegalAccessException {
        Field onTickField = Clock.class.getDeclaredField("onTick");
        onTickField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        clock.addRunnable(() -> {});
        clock.addRunnable(() -> {});
        clock.addRunnable(() -> {});

        List<Runnable> onTick = (List<Runnable>) onTickField.get(clock);

        assertEquals(3, onTick.size());
    }

    @Test
    public void testStart() throws NoSuchFieldException, IllegalAccessException {
        Field isRunningField = Clock.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        clock.start();

        boolean isRunning = (boolean) isRunningField.get(clock);
        assertTrue(isRunning);
    }

    @Test
    public void testStop() throws NoSuchFieldException, IllegalAccessException {
        Field isRunningField = Clock.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        clock.start();

        boolean isRunning = (boolean) isRunningField.get(clock);
        assertTrue(isRunning);

        clock.stop();

        isRunning = (boolean) isRunningField.get(clock);
        assertFalse(isRunning);
    }

    @Test
    public void testStopNotRunning() throws NoSuchFieldException, IllegalAccessException {
        Field isRunningField = Clock.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        assertDoesNotThrow(clock::stop);

        boolean isRunning = (boolean) isRunningField.get(clock);
        assertFalse(isRunning);
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testOnTick() {
        Clock clock = new Clock(100, "TestThread");

        AtomicBoolean hasRun = new AtomicBoolean(false);
        clock.addRunnable(() -> hasRun.set(true));

        clock.start();

        while (!hasRun.get()) {
            LockSupport.parkNanos(1000);
        }

        clock.stop();
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testMultipleTicks() {
        Clock clock = new Clock(100, "TestThread");

        AtomicInteger counter = new AtomicInteger(0);
        clock.addRunnable(counter::incrementAndGet);

        clock.start();

        while (counter.get() < 5) {
            LockSupport.parkNanos(1000);
        }

        clock.stop();
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testTickTime() {
        Clock clock = new Clock(100, "TestThread");

        AtomicInteger counter = new AtomicInteger(0);
        clock.addRunnable(counter::incrementAndGet);

        long start = System.nanoTime();
        clock.start();

        while (counter.get() < 2) {
            LockSupport.parkNanos(1000);
        }

        long end = System.nanoTime();
        clock.stop();

        assertEquals(10000000, end - start, 10000000);
    }
}
