package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimerTest {

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Field counterField = Timer.class.getDeclaredField("counter");
        counterField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        Timer timer = new Timer(clock);

        byte counter = (byte) counterField.get(timer);
        assertEquals(0, counter);
    }

    @Test
    public void testGetCounter() throws NoSuchFieldException, IllegalAccessException {
        Field counterField = Timer.class.getDeclaredField("counter");
        counterField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        Timer timer = new Timer(clock);

        counterField.set(timer, (byte) 10);
        byte counter = timer.getCounter();

        assertEquals(10, counter);
    }

    @Test
    public void testSetCounter() {
        Clock clock = new Clock(100, "TestThread");
        Timer timer = new Timer(clock);

        timer.setCounter((byte) 10);
        byte counter = timer.getCounter();

        assertEquals(10, counter);
    }

    @Test
    public void testReset() {
        Clock clock = new Clock(100, "TestThread");
        Timer timer = new Timer(clock);

        timer.setCounter((byte) 10);
        timer.reset();

        byte counter = timer.getCounter();
        assertEquals(0, counter);
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testOnTick() {
        Clock clock = new Clock(100, "TestThread");
        Timer timer = new Timer(clock);

        timer.setCounter((byte) 5);
        clock.start();

        while (timer.getCounter() > 0) {
            LockSupport.parkNanos(1000);
        }

        clock.stop();
    }
}
