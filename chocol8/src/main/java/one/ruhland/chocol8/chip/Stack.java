package one.ruhland.chocol8.chip;

public class Stack {

    static final int STACK_SIZE = 0x10;

    private short[] stack = new short[STACK_SIZE];
    private byte stackPointer = 0x00;

    Stack() {}

    void push(final short value) {
        if (stackPointer >= stack.length) {
            throw new IllegalStateException("Trying to push to full stack (Stack size: " + stack.length + ")!");
        }

        stack[stackPointer++] = value;
    }

    short pop() {
        if (stackPointer <= 0) {
            throw new IllegalStateException("Trying to pop from empty stack!");
        }

        return stack[--stackPointer];
    }

    void reset() {
        stack = new short[stack.length];
        stackPointer = 0x00;
    }

    public int getSize() {
        return stack.length;
    }

    public short peekAt(int index) {
        if (index < 0 || index >= stack.length) {
            throw new IndexOutOfBoundsException("Trying to peek at index " + index + " (Stack size: " + stack.length + ")!");
        }

        return stack[index];
    }

    public void setAt(int index, short value) {
        if (index < 0 || index >= stack.length) {
            throw new IndexOutOfBoundsException("Trying to set value at index " + index + " (Stack size: " + stack.length + ")!");
        }

        stack[index] = value;
    }
}
