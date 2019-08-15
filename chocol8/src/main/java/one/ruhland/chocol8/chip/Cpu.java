package one.ruhland.chocol8.chip;

import java.util.Arrays;

public class Cpu {

    private short[] vRegisters = new short[0xf];
    private int programCounter = 0x0200;
    private int indexRegister = 0x0000;
    private int stackPointer = 0x0000;

    private final Memory memory;
    private final Graphics graphics;

    Cpu(final Memory memory, final Graphics graphics) {
        this.memory = memory;
        this.graphics = graphics;
    }

    void reset() {
        Arrays.fill(vRegisters, (short) 0);
        programCounter = 0x0200;
    }

    public void runCycle() {
        short opcode = (short) (memory.getByte(programCounter) << 8 | memory.getByte(programCounter + 1));
        executeOpcode(opcode);
    }

    private void executeOpcode(final short opcode) {
        byte operation = (byte) (opcode & 0xf000);

        switch (operation) {
            case 0x0:
            case 0x1:
            case 0x2:
            case 0x3:
            case 0x4:
            case 0x5:
            case 0x6:
            case 0x7:
            case 0x8:
            case 0x9:
            case 0xa:
            case 0xb:
            case 0xc:
            case 0xd:
            case 0xe:
            case 0xf:
            default:
                throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
        }
    }
}
