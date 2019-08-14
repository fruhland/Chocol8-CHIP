package one.ruhland.chocol8.chip;

import java.util.Arrays;

public class Memory {

    static final int MEMORY_SIZE = 0x0fff;
    static final int PROGRAM_START = 0x0200;

    private byte[] memory = new byte[MEMORY_SIZE];

    Memory() {}

    void reset() {
        Arrays.fill(memory, (byte) 0);
    }

    public byte getByte(int address) {
        if(address < 0 || address >= MEMORY_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid address: 0x%x", address));
        }

        return memory[address];
    }

    public void setByte(int address, byte value) {
        if(address < 0 || address >= MEMORY_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid address: 0x%x", address));
        }

        memory[address] = value;
    }

    public void setBytes(int address, byte[] bytes) {
        if(address < 0 || address + bytes.length >= MEMORY_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid address: 0x%x (Size: 0x%x)", address, bytes.length));
        }

        System.arraycopy(bytes, 0, memory, address, bytes.length);
    }
}
