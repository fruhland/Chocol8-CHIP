package one.ruhland.chocol8.chip;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

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

    private void incProgramCounter() {
        jumpToAddress((short) (programCounter + 2));
    }

    private void jumpToAddress(short address) {
        if (address < Memory.PROGRAM_START || address >= Memory.MEMORY_SIZE) {
            throw new IllegalStateException(String.format("Illegal instructions address 0x%04x!" +
                            "Instruction addresses must be between 0x%04x and 0x%04x", address,
                    Memory.PROGRAM_START, Memory.MEMORY_SIZE - 1));
        }

        programCounter = address;
    }

    private void conditionalJump(short operand1, short operand2, BiFunction<Short, Short, Boolean> operator) {
        if(operator.apply(operand1, operand2)) {
            incProgramCounter();
        }
    }

    private void executeOpcode(final short opcode) {
        byte instruction = (byte) ((opcode & 0xf000) >> 12);

        switch (instruction) {
            case 0x0: {
                // 0x00e0: Clear screen
                if ((opcode & 0x0fff) == 0x00e0) {
                    graphics.reset();
                    incProgramCounter();
                    break;
                }
            }
            case 0x1:
                // 0x1NNN: Jump to NNN
                jumpToAddress((short) (opcode & 0x0fff));
                break;
            case 0x2:
            case 0x3:
                // 0x3XNN: Skip next instruction if V[X] == NN
                conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], (short) (opcode & 0x00ff), Short::equals);
                incProgramCounter();
                break;
            case 0x4: {
                // 0x4XNN: Skip next instruction if V[X] != NN
                conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], (short) (opcode & 0x00ff),
                        (operand1, operand2) -> !operand1.equals(operand2));
                incProgramCounter();
                break;
            }
            case 0x5: {
                // 0x5XY0: Skip next instruction if V[X] == V[Y]
                if((opcode & 0x000f) == 0) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], vRegisters[(opcode & 0x00f0) >> 4], Short::equals);
                    incProgramCounter();
                    break;
                }
            }
            case 0x6:
            case 0x7:
            case 0x8:
            case 0x9:
                // 0x9XY0: Skip next instruction if V[X] == V[Y]
                if((opcode & 0x000f) == 0) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], vRegisters[(opcode & 0x00f0) >> 4],
                            (operand1, operand2) -> !operand1.equals(operand2));
                    incProgramCounter();
                    break;
                }
            case 0xa:
            case 0xb: {
                // 0xBNNN: Jump to V[0] + NNN
                jumpToAddress((short) ((opcode & 0x0fff) + vRegisters[0]));
                break;
            }
            case 0xc:
            case 0xd: {
                short x = vRegisters[(opcode & 0x0f00) >> 8];
                short y = vRegisters[(opcode & 0x00f0) >> 4];
                short height = vRegisters[opcode & 0x000f];

                if(graphics.drawSprite(x, y, height, indexRegister)) {
                    vRegisters[0xf] = 1;
                }
                incProgramCounter();
                break;
            }
            case 0xe:
            case 0xf:
            default:
                throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
        }
    }
}
