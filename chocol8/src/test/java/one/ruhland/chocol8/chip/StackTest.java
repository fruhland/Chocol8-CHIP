package one.ruhland.chocol8.chip;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StackTest {

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Field arrayField = Stack.class.getDeclaredField("stack");
        arrayField.setAccessible(true);

        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();
        short[] array = (short[]) arrayField.get(stack);
        byte pointer = (byte) pointerField.get(stack);

        assertEquals(Stack.STACK_SIZE, array.length);
        assertEquals(0, pointer);

        for (short s : array) {
            assertEquals(0, s);
        }
    }

    @Test
    public void testSize() {
        Stack stack = new Stack();

        assertEquals(Stack.STACK_SIZE, stack.getSize());
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        Field arrayField = Stack.class.getDeclaredField("stack");
        arrayField.setAccessible(true);

        Stack stack = new Stack();
        short[] array = (short[]) arrayField.get(stack);

        stack.setAt(0x08, (short) 0xab);

        assertEquals(0xab, array[0x08]);
    }

    @Test
    public void testSetException() {
        Stack stack = new Stack();

        assertThrows(IndexOutOfBoundsException.class, () -> stack.setAt(-1, (short) 0xab));
        assertThrows(IndexOutOfBoundsException.class, () -> stack.setAt(Stack.STACK_SIZE + 1, (short) 0xab));
    }

    @Test
    public void testPeek() {
        Stack stack = new Stack();

        stack.setAt(0x08, (short) 0xab);

        assertEquals(0xab, stack.peekAt(0x08));
    }

    @Test
    public void testPeekException() {
        Stack stack = new Stack();

        assertThrows(IndexOutOfBoundsException.class, () -> stack.peekAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stack.peekAt(Stack.STACK_SIZE + 1));
    }

    @Test
    public void testSinglePush() throws NoSuchFieldException, IllegalAccessException {
        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();

        stack.push((short) 0xab);

        byte pointer = (byte) pointerField.get(stack);

        assertEquals(1, pointer);
        assertEquals(0xab, stack.peekAt(0));
    }

    @Test
    public void testMultiplePush() throws NoSuchFieldException, IllegalAccessException {
        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();

        for (int i = 0; i < stack.getSize(); i++) {
            stack.push((short) i);
        }

        byte pointer = (byte) pointerField.get(stack);

        assertEquals(Stack.STACK_SIZE, pointer);

        for (int i = 0; i < stack.getSize(); i++) {
            assertEquals(i, stack.peekAt(i));
        }
    }

    @Test
    public void testPushOverflow() {
        Stack stack = new Stack();

        for (int i = 0; i < stack.getSize(); i++) {
            stack.push((short) i);
        }

        assertThrows(IllegalStateException.class, () -> stack.push((short) 0xab));
    }

    @Test
    public void testSinglePop() throws NoSuchFieldException, IllegalAccessException {
        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();

        stack.push((short) 0xab);

        short value = stack.pop();
        byte pointer = (byte) pointerField.get(stack);

        assertEquals(0xab, value);

        assertEquals(0, pointer);
    }

    @Test
    public void testMultiplePop() throws NoSuchFieldException, IllegalAccessException {
        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();

        for (int i = 0; i < stack.getSize(); i++) {
            stack.push((short) i);
        }

        for (int i = 0; i < stack.getSize(); i++) {
            assertEquals(stack.getSize() - 1 - i, stack.pop());
        }

        byte pointer = (byte) pointerField.get(stack);

        assertEquals(0, pointer);
    }

    @Test
    public void testPopUnderflow() {
        Stack stack = new Stack();

        assertThrows(IllegalStateException.class, stack::pop);
    }

    @Test
    public void testReset() throws NoSuchFieldException, IllegalAccessException {
        Field pointerField = Stack.class.getDeclaredField("stackPointer");
        pointerField.setAccessible(true);

        Stack stack = new Stack();

        for (int i = 0; i < stack.getSize(); i++) {
            stack.push((short) i);
        }

        stack.reset();

        byte pointer = (byte) pointerField.get(stack);

        assertEquals(0, pointer);

        for (int i = 0; i < stack.getSize(); i++) {
            assertEquals(0, stack.peekAt(i));
        }
    }
}
