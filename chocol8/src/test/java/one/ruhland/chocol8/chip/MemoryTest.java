package one.ruhland.chocol8.chip;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemoryTest {

    @Test
    public void testSize() {
        Memory memory = new Memory();

        assertEquals(Memory.MEMORY_SIZE, memory.getSize());
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        Field arrayField = Memory.class.getDeclaredField("memory");
        arrayField.setAccessible(true);

        Memory memory = new Memory();
        byte[] array = (byte[]) arrayField.get(memory);

        memory.setByte(0x0000, (byte) 0xab);
        memory.setByte(0x0b0b, (byte) 0xcd);
        memory.setByte(0x0fff, (byte) 0xef);

        assertEquals((byte) 0xab, array[0x0000]);
        assertEquals((byte) 0xcd, array[0x0b0b]);
        assertEquals((byte) 0xef, array[0x0fff]);
    }

    @Test
    public void testSetOutOfBounds() {
        Memory memory = new Memory();

        assertThrows(IndexOutOfBoundsException.class, () -> memory.setByte(Memory.MEMORY_SIZE + 1, (byte) 0xff));
    }

    @Test
    public void testMultiSet() throws NoSuchFieldException, IllegalAccessException {
        Field arrayField = Memory.class.getDeclaredField("memory");
        arrayField.setAccessible(true);

        Memory memory = new Memory();
        byte[] array = (byte[]) arrayField.get(memory);

        memory.setBytes(0x0b0b, new byte[]{
            (byte) 0xab,
            (byte) 0xcd,
            (byte) 0xef
        });

        assertEquals((byte) 0xab, array[0x0b0b]);
        assertEquals((byte) 0xcd, array[0x0b0c]);
        assertEquals((byte) 0xef, array[0x0b0d]);
    }

    @Test
    public void testMultiSetOutOfBounds() {
        Memory memory = new Memory();

        assertThrows(IndexOutOfBoundsException.class, () -> memory.setBytes(Memory.MEMORY_SIZE - 1, new byte[]{
            (byte) 0xab,
            (byte) 0xcd,
            (byte) 0xef
        }));
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        Field arrayField = Memory.class.getDeclaredField("memory");
        arrayField.setAccessible(true);

        Memory memory = new Memory();
        byte[] array = (byte[]) arrayField.get(memory);

        array[0x0000] = (byte) 0xab;
        array[0x0b0b] = (byte) 0xcd;
        array[0x0fff] = (byte) 0xef;

        assertEquals((byte) 0xab, memory.getByte(0x0000));
        assertEquals((byte) 0xcd, memory.getByte(0x0b0b));
        assertEquals((byte) 0xef, memory.getByte(0x0fff));
    }

    @Test
    public void testGetOutOfBounds() {
        Memory memory = new Memory();

        assertThrows(IndexOutOfBoundsException.class, () -> memory.getByte(Memory.MEMORY_SIZE + 1));
    }

    @Test
    public void testSetGet() {
        Memory memory = new Memory();

        for (int i = 0; i < memory.getSize(); i++) {
            memory.setByte(i, (byte) i);
        }

        for (int i = 0; i < memory.getSize(); i++) {
            assertEquals((byte) i, memory.getByte(i));
        }
    }

    @Test
    public void testConstructor() {
        Memory memory = new Memory();

        for (int i = 0; i < memory.getSize(); i++) {
            assertEquals(0, memory.getByte(i));
        }
    }

    @Test
    public void testReset() {
        Memory memory = new Memory();

        for (int i = 0; i < memory.getSize(); i++) {
            memory.setByte(i, (byte) i);
        }

        memory.reset();

        for (int i = Memory.PROGRAM_START; i < memory.getSize(); i++) {
            assertEquals(0, memory.getByte(i));
        }
    }
}
