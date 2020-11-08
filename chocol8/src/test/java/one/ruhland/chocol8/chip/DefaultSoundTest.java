package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultSoundTest {

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Field counterField = Sound.class.getDeclaredField("counter");
        Field frequencyField = DefaultSound.class.getDeclaredField("toneFrequency");
        counterField.setAccessible(true);
        frequencyField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        DefaultSound sound = new DefaultSound(clock);

        byte counter = (byte) counterField.get(sound);
        double frequency = (double) frequencyField.get(sound);

        assertEquals(0, counter);
        assertEquals(440, frequency);
    }

    @Test
    public void testGetFrequency() throws NoSuchFieldException, IllegalAccessException {
        Field frequencyField = DefaultSound.class.getDeclaredField("toneFrequency");
        frequencyField.setAccessible(true);

        Clock clock = new Clock(100, "TestThread");
        DefaultSound sound = new DefaultSound(clock);

        frequencyField.set(sound, 1000);
        double frequency = sound.getToneFrequency();

        assertEquals(1000, frequency);
    }

    @Test
    public void testSetFrequency() {
        Clock clock = new Clock(100, "TestThread");
        Sound sound = new DefaultSound(clock);

        sound.setToneFrequency(1000);
        double frequency = sound.getToneFrequency();

        assertEquals(1000, frequency);
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testOnTick() {
        Clock clock = new Clock(100, "TestThread");
        DefaultSound sound = new DefaultSound(clock);
        sound.setToneFrequency(0);

        sound.setCounter((byte) 5);
        clock.start();

        while (sound.getCounter() > 0) {
            LockSupport.parkNanos(1000);
        }

        clock.stop();
    }
}
