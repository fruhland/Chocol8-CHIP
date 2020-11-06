package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GraphicsTest {

    @Test
    public void testSetResolution() throws NoSuchFieldException, IllegalAccessException {
        Field resolutionXField = Graphics.class.getDeclaredField("resolutionX");
        Field resolutionYField = Graphics.class.getDeclaredField("resolutionY");
        resolutionXField.setAccessible(true);
        resolutionYField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());

        graphics.setResolution(64, 32);

        int resolutionX = (int) resolutionXField.get(graphics);
        int resolutionY = (int) resolutionYField.get(graphics);

        assertEquals(64, resolutionX);
        assertEquals(32, resolutionY);
    }

    @Test
    public void testSetResolutionZero() throws NoSuchFieldException, IllegalAccessException {
        Field resolutionXField = Graphics.class.getDeclaredField("resolutionX");
        Field resolutionYField = Graphics.class.getDeclaredField("resolutionY");
        resolutionXField.setAccessible(true);
        resolutionYField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());

        assertThrows(IllegalArgumentException.class, () -> graphics.setResolution(0, 0));
    }

    @Test
    public void testReset() throws NoSuchFieldException, IllegalAccessException {
        Field resolutionXField = Graphics.class.getDeclaredField("resolutionX");
        Field resolutionYField = Graphics.class.getDeclaredField("resolutionY");
        Field frameBufferField = Graphics.class.getDeclaredField("frameBuffer");
        resolutionXField.setAccessible(true);
        resolutionYField.setAccessible(true);
        frameBufferField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doCallRealMethod().when(graphics).reset();

        graphics.setResolution(64, 32);

        boolean[] frameBuffer = (boolean[]) frameBufferField.get(graphics);
        Arrays.fill(frameBuffer, true);

        graphics.reset();

        for (boolean b : frameBuffer) {
            assertFalse(b);
        }
    }

    @Test
    public void testSetResolutionNegative() throws NoSuchFieldException, IllegalAccessException {
        Field resolutionXField = Graphics.class.getDeclaredField("resolutionX");
        Field resolutionYField = Graphics.class.getDeclaredField("resolutionY");
        resolutionXField.setAccessible(true);
        resolutionYField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());

        assertThrows(IllegalArgumentException.class, () -> graphics.setResolution(-1, -1));
    }

    @Test
    public void testDrawSprite() throws NoSuchFieldException, IllegalAccessException {
        Field memoryField = Graphics.class.getDeclaredField("memory");
        Field frameBufferField = Graphics.class.getDeclaredField("frameBuffer");
        memoryField.setAccessible(true);
        frameBufferField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doCallRealMethod().when(graphics).drawSprite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

        Memory memory = new Memory();
        memoryField.set(graphics, memory);

        memory.setBytes(0x500, new byte[]{ (byte) 0xf0, (byte) 0x80, (byte) 0xf0, (byte) 0x80, (byte) 0x80 });
        graphics.setResolution(64, 32);

        graphics.drawSprite(0, 0, 5,0x500);

        boolean[] frameBuffer = (boolean[]) frameBufferField.get(graphics);
        boolean[][] expectedBuffer = new boolean[][]{
                new boolean[] { true, true, true, true, false, false, false, false },
                new boolean[] { true, false, false, false, false, false, false, false },
                new boolean[] { true, true, true, true, false, false, false, false },
                new boolean[] { true, false, false, false, false, false, false, false },
                new boolean[] { true, false, false, false, false, false, false, false },
        };

        for (int i = 0; i < 5; i++) {
            boolean[] partBuffer = Arrays.copyOfRange(frameBuffer, i * 64, i * 64 + 8);

            assertArrayEquals(expectedBuffer[i], partBuffer);
        }
    }

    @Test
    public void testDrawSpriteFlip() throws NoSuchFieldException, IllegalAccessException {
        Field memoryField = Graphics.class.getDeclaredField("memory");
        Field frameBufferField = Graphics.class.getDeclaredField("frameBuffer");
        memoryField.setAccessible(true);
        frameBufferField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doCallRealMethod().when(graphics).drawSprite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

        Memory memory = new Memory();
        memoryField.set(graphics, memory);

        memory.setByte(0x500, (byte) 0xff);
        graphics.setResolution(64, 32);

        boolean flipped = graphics.drawSprite(0, 0, 1,0x500);
        assertFalse(flipped);

        flipped = graphics.drawSprite(0, 0, 1,0x500);
        assertTrue(flipped);
    }

    @Test
    public void testDrawSpriteHorizontalWrapAround() throws NoSuchFieldException, IllegalAccessException {
        Field memoryField = Graphics.class.getDeclaredField("memory");
        Field frameBufferField = Graphics.class.getDeclaredField("frameBuffer");
        memoryField.setAccessible(true);
        frameBufferField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doCallRealMethod().when(graphics).drawSprite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

        Memory memory = new Memory();
        memoryField.set(graphics, memory);

        memory.setByte(0x500, (byte) 0xff);
        graphics.setResolution(64, 32);

        graphics.drawSprite(62, 0, 1,0x500);

        boolean[] frameBuffer = (boolean[]) frameBufferField.get(graphics);

        assertTrue(frameBuffer[0]);
        assertTrue(frameBuffer[1]);
        assertTrue(frameBuffer[2]);
        assertTrue(frameBuffer[3]);
        assertTrue(frameBuffer[4]);
        assertTrue(frameBuffer[5]);
        assertTrue(frameBuffer[62]);
        assertTrue(frameBuffer[63]);

        for (int i = 6; i < 62; i++) {
            assertFalse(frameBuffer[i]);
        }
    }

    @Test
    public void testDrawSpriteVerticalWrapAround() throws NoSuchFieldException, IllegalAccessException {
        Field memoryField = Graphics.class.getDeclaredField("memory");
        Field frameBufferField = Graphics.class.getDeclaredField("frameBuffer");
        memoryField.setAccessible(true);
        frameBufferField.setAccessible(true);

        Graphics graphics = Mockito.mock(Graphics.class);
        Mockito.doCallRealMethod().when(graphics).setResolution(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doCallRealMethod().when(graphics).drawSprite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

        Memory memory = new Memory();
        memoryField.set(graphics, memory);

        memory.setBytes(0x500, new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80 });
        graphics.setResolution(64, 32);

        graphics.drawSprite(0, 30, 8,0x500);

        boolean[] frameBuffer = (boolean[]) frameBufferField.get(graphics);

        assertTrue(frameBuffer[0]);
        assertTrue(frameBuffer[64]);
        assertTrue(frameBuffer[128]);
        assertTrue(frameBuffer[192]);
        assertTrue(frameBuffer[256]);
        assertTrue(frameBuffer[320]);
        assertTrue(frameBuffer[1920]);
        assertTrue(frameBuffer[1984]);

        for (int i = 6; i < 30; i++) {
            assertFalse(frameBuffer[i * 64]);
        }
    }
}
