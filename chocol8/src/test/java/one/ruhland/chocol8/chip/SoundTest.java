package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoundTest {

    @Test
    public void testGetCounter() throws NoSuchFieldException, IllegalAccessException {
        Field counterField = Sound.class.getDeclaredField("counter");
        counterField.setAccessible(true);

        Sound sound = Mockito.mock(Sound.class);
        Mockito.doCallRealMethod().when(sound).getCounter();

        counterField.set(sound, (byte) 10);
        byte counter = sound.getCounter();

        assertEquals(10, counter);
    }

    @Test
    public void testSetCounterStartBeep() {
        Sound sound = Mockito.mock(Sound.class);
        Mockito.doCallRealMethod().when(sound).setCounter(Mockito.anyByte());
        Mockito.doCallRealMethod().when(sound).getCounter();

        AtomicBoolean startBeepCalled = new AtomicBoolean(false);
        Mockito.doAnswer(invocation -> {
            startBeepCalled.set(true);
            return null;
        }).when(sound).startBeep();

        sound.setCounter((byte) 10);
        byte counter = sound.getCounter();

        assertEquals(10, counter);
        assertTrue(startBeepCalled.get());
    }

    @Test
    public void testSetCounterStopBeep() {
        Sound sound = Mockito.mock(Sound.class);
        Mockito.doCallRealMethod().when(sound).setCounter(Mockito.anyByte());
        Mockito.doCallRealMethod().when(sound).getCounter();

        AtomicBoolean stopBeepCalled = new AtomicBoolean(false);
        Mockito.doAnswer(invocation -> {
            stopBeepCalled.set(true);
            return null;
        }).when(sound).stopBeep();

        sound.setCounter((byte) 0);
        byte counter = sound.getCounter();

        assertEquals(0, counter);
        assertTrue(stopBeepCalled.get());
    }

    @Test
    public void testReset() {
        Sound sound = Mockito.mock(Sound.class);
        Mockito.doCallRealMethod().when(sound).setCounter(Mockito.anyByte());
        Mockito.doCallRealMethod().when(sound).getCounter();
        Mockito.doCallRealMethod().when(sound).reset();

        sound.setCounter((byte) 10);
        sound.reset();

        byte counter = sound.getCounter();
        assertEquals(0, counter);
    }
}
