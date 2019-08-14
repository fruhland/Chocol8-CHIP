package one.ruhland.chocol8.chip;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class Cpu {

    private byte[] vRegisters = new byte[0x10];
    private short programCounter = 0x0200;
    private short indexRegister = 0x0000;
    private short stackPointer = 0x0000;

    private final Memory memory;
    private final Graphics graphics;

    Cpu(final Memory memory, final Graphics graphics) {
        this.memory = memory;
        this.graphics = graphics;
    }

    void reset() {
        Arrays.fill(vRegisters, (byte) 0);
        programCounter = 0x0200;
    }

    public void runCycle() {
        // Some test instructions, that should draw the fontset
        for(byte i = 0; i <= 0xf; i++) {
            executeOpcode((short) (0x6000 | ((i % 4) * 5)));
            executeOpcode((short) (0x6100 | ((i / 4) * 6)));
            executeOpcode((short) (0x6200 | i));
            executeOpcode((short) 0xf229);
            executeOpcode((short) 0xd015);
        }

        short opcode = (short) (memory.getByte(programCounter) << 8 | memory.getByte(programCounter + 1));
        executeOpcode(opcode);
    }

    private void incProgramCounter() {
        jumpToAddress((short) (programCounter + 2));
    }

    private void jumpToAddress(short address) {
        if (address < Memory.PROGRAM_START || address >= Memory.MEMORY_SIZE) {
            throw new IllegalStateException(String.format("Illegal instructions address 0x%04x! " +
                            "Instruction addresses must be between 0x%04x and 0x%04x", address,
                    Memory.PROGRAM_START, Memory.MEMORY_SIZE - 1));
        }

        programCounter = address;
    }

    private void conditionalJump(byte operand1, byte operand2, BiFunction<Byte, Byte, Boolean> operator) {
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
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            }
            case 0x1:
                // 0x1NNN: Jump to NNN
                jumpToAddress((short) (opcode & 0x0fff));
                break;
            case 0x2:
            case 0x3:
                // 0x3XNN: Skip next instruction if V[X] == NN
                conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], (byte) (opcode & 0x00ff), Byte::equals);
                incProgramCounter();
                break;
            case 0x4: {
                // 0x4XNN: Skip next instruction if V[X] != NN
                conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], (byte) (opcode & 0x00ff),
                        (operand1, operand2) -> !operand1.equals(operand2));
                incProgramCounter();
                break;
            }
            case 0x5: {
                // 0x5XY0: Skip next instruction if V[X] == V[Y]
                if((opcode & 0x000f) == 0) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], vRegisters[(opcode & 0x00f0) >> 4], Byte::equals);
                    incProgramCounter();
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            }
            case 0x6:
                // 0x6XNN: V[X] = NN
                vRegisters[(opcode & 0x0f00) >> 8] = (byte) (opcode & 0x00ff);
                incProgramCounter();
                break;
            case 0x7:
                // 0x7XNN: V[X] += NN
                vRegisters[(opcode & 0x0f00) >> 8] += (byte) (opcode & 0x00ff);
                incProgramCounter();
                break;
            case 0x8:
            case 0x9:
                // 0x9XY0: Skip next instruction if V[X] == V[Y]
                if((opcode & 0x000f) == 0) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], vRegisters[(opcode & 0x00f0) >> 4],
                            (operand1, operand2) -> !operand1.equals(operand2));
                    incProgramCounter();
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            case 0xa:
                // 0xaNNN: index = NNN
                indexRegister = (short) (opcode & 0x0fff);
                incProgramCounter();
            case 0xb: {
                // 0xBNNN: Jump to V[0] + NNN
                jumpToAddress((short) ((opcode & 0x0fff) + vRegisters[0]));
                break;
            }
            case 0xc:
            case 0xd: {
                byte x = vRegisters[(opcode & 0x0f00) >> 8];
                byte y = vRegisters[(opcode & 0x00f0) >> 4];
                byte height = (byte) (opcode & 0x000f);

                if(graphics.drawSprite(x, y, height, indexRegister)) {
                    vRegisters[0xf] = 1;
                }

                incProgramCounter();
                break;
            }
            case 0xe:
            case 0xf:
                // 0xfX1e: index += V[X]
                if((opcode & 0x00ff) == 0x001e) {
                    indexRegister += vRegisters[(opcode & 0x0f00) >> 8];
                    incProgramCounter();
                    break;
                }
                // 0xfX29: index = address of character sprite of V[X]
                else if((opcode & 0x00ff) == 0x0029) {
                    indexRegister = (short) (Memory.FONT_START + vRegisters[(opcode & 0x0f00) >> 8] * 5);
                    incProgramCounter();
                    break;
                }
                // 0xfX55: Write registers V[0] to V[X] consecutively to memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x0055) {
                    for(int i = 0; i < (opcode & 0x0f00) >> 8; i++) {
                        memory.setByte(indexRegister + i, vRegisters[i]);
                    }

                    incProgramCounter();
                    break;
                }
                // 0xfX65: Load registers V[0] to V[X] from memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x0065) {
                    for(int i = 0; i < (opcode & 0x0f00) >> 8; i++) {
                        vRegisters[i] = memory.getByte(indexRegister + i);
                    }

                    incProgramCounter();
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            default:
                throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
        }
    }
}
