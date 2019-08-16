package one.ruhland.chocol8.chip;

public class Stack {

    private short[] stack;
    private byte stackPointer = 0x00;

    Stack(byte size) {
        stack = new short[size];
    }

    void push(short address) {
        if(stackPointer >= stack.length) {
            throw new IllegalStateException("Trying to push to full stack (Stack size: " + stack.length + ")!");
        }

        stack[stackPointer++] = address;
    }

    short pop() {
        if(stackPointer <= 0) {
            throw new IllegalStateException("Tryring to pop from empty stack!");
        }

        return stack[--stackPointer];
    }

    void reset() {
        stack = new short[stack.length];
        stackPointer = 0x00;
    }
}
