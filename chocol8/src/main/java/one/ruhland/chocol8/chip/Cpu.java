package one.ruhland.chocol8.chip;

import java.util.Arrays;
import java.util.function.BiFunction;

public class Cpu {

    private byte[] vRegisters = new byte[0x10];
    private short programCounter = 0x0200;
    private short indexRegister = 0x0000;

    private final Stack stack = new Stack((byte) 16);
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
        short opcode = (short) unsignedOperation(memory.getByte(programCounter), memory.getByte(programCounter + 1),
                (operand1, operand2) -> operand1 << 8 | operand2);
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

    private int unsignedOperation(byte operand1, byte operand2, BiFunction<Integer, Integer, Integer> operator) {
        return operator.apply(operand1 & 0xff, operand2 & 0xff);
    }

    private int byteToUnsignedInt(byte value) {
        return value & 0xff;
    }

    private void executeOpcode(final short opcode) {
        byte instruction = (byte) ((opcode & 0xf000) >> 12);

        switch (instruction) {
            case 0x0: {
                // 0x00e0: Clear screen
                if ((opcode & 0x0fff) == 0x0e0) {
                    graphics.reset();
                    incProgramCounter();
                    break;
                // 0x00ee: Return from subroutine
                } else if((opcode & 0x0fff) == 0x0ee) {
                    jumpToAddress(stack.pop());
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
                // 0x2NNN: Call subroutine at NNN
                stack.push((short) (programCounter + 2));
                jumpToAddress((short) (opcode & 0x0fff));
                break;
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
                if((opcode & 0x000f) == 0x0) {
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
                vRegisters[(opcode & 0x0f00) >> 8] = (byte) unsignedOperation(
                        vRegisters[(opcode & 0x0f00) >> 8], (byte) (opcode & 0x00ff), Integer::sum);
                incProgramCounter();
                break;
            case 0x8:
                // 0x8XY0: V[X] = V[Y]
                if((opcode & 0x000f) == 0x0) {
                    vRegisters[(opcode & 0x0f00) >> 8] = vRegisters[(opcode & 0x00f0) >> 4];
                    incProgramCounter();
                    break;
                }
                // 0x8XY1: V[X] |= V[Y]
                if((opcode & 0x000f) == 0x1) {
                    vRegisters[(opcode & 0x0f00) >> 8] |= vRegisters[(opcode & 0x00f0) >> 4];
                    incProgramCounter();
                    break;
                }
                // 0x8XY2: V[X] &= V[Y]
                else if((opcode & 0x000f) == 0x2) {
                    vRegisters[(opcode & 0x0f00) >> 8] &= vRegisters[(opcode & 0x00f0) >> 4];
                    incProgramCounter();
                    break;
                }
                // 0x8XY3: V[X] ^= V[Y]
                else if((opcode & 0x000f) == 0x3) {
                    vRegisters[(opcode & 0x0f00) >> 8] ^= vRegisters[(opcode & 0x00f0) >> 4];
                    incProgramCounter();
                    break;
                }
                // 0x8XY4: V[X] += V[Y]
                else if((opcode & 0x000f) == 0x4) {
                    int result = unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], Integer::sum);

                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) result;
                    vRegisters[0xf] = result > 0xff ? (byte) 1 : (byte) 0;

                    incProgramCounter();
                    break;
                }
                // 0x8XY5: V[X] -= V[Y]; V[F] = (Borrow ? 1 : 0)
                else if((opcode & 0x000f) == 0x5) {
                    int result = unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand1 - operand2);

                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) result;
                    vRegisters[0xf] = result < 0 ? (byte) 1 : (byte) 0;

                    incProgramCounter();
                    break;
                }
                // 0x8XY6: V[X] >>= 1; V[F] = LSB(V[X])
                else if((opcode & 0x000f) == 0x6) {
                    vRegisters[0xf] = (byte) (vRegisters[(opcode & 0x0f00) >> 8] & 0x01);
                    vRegisters[(opcode & 0x0f00) >> 8] >>= 1;
                    incProgramCounter();
                    break;
                }
                // 0x8XY7: V[X] = V[Y] - V[X]; V[F] = (Borrow ? 1 : 0)
                else if((opcode & 0x000f) == 0x7) {
                    int result = unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand2 - operand1);

                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) result;
                    vRegisters[0xf] = result < 0 ? (byte) 1 : (byte) 0;

                    incProgramCounter();
                    break;
                }
                // 0x8XYE: V[X] <<= 1; V[F] = MSB(V[X])
                else if((opcode & 0x000f) == 0xe) {
                    vRegisters[0xf] = (byte) (vRegisters[(opcode & 0x0f00) >> 8] & 0x80);
                    vRegisters[(opcode & 0x0f00) >> 8] <<= 1;
                    incProgramCounter();
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            case 0x9:
                // 0x9XY0: Skip next instruction if V[X] == V[Y]
                if((opcode & 0x000f) == 0x0) {
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
                break;
            case 0xb: {
                // 0xBNNN: Jump to V[0] + NNN
                jumpToAddress((short) ((opcode & 0x0fff) + vRegisters[0]));
                break;
            }
            case 0xc:
                // 0xCXNN: V[X] &= NN
                vRegisters[(opcode & 0x0f00) >> 8] = (byte) unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                        (byte) (Math.random() * 0xff), (operand1, operand2) -> operand1 & operand2);
                incProgramCounter();
                break;
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
                if((opcode & 0x00ff) == 0x1e) {
                    indexRegister += vRegisters[(opcode & 0x0f00) >> 8];
                    incProgramCounter();
                    break;
                }
                // 0xfX29: index = address of character sprite of V[X]
                else if((opcode & 0x00ff) == 0x29) {
                    indexRegister = (short) (Memory.FONT_START + vRegisters[(opcode & 0x0f00) >> 8] * 5);
                    incProgramCounter();
                    break;
                }
                // 0xfX55: Write registers V[0] to V[X] consecutively to memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x55) {
                    for(int i = 0; i < (opcode & 0x0f00) >> 8; i++) {
                        memory.setByte(indexRegister + i, vRegisters[i]);
                    }

                    incProgramCounter();
                    break;
                }
                // 0xfX33: Write registers BCD(V[X]) to memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x33) {
                    char[] digits = String.valueOf(vRegisters[(opcode & 0x0f00) >> 8] & 0xff).toCharArray();
                    int i = 0;

                    // Write leading zeros
                    while(i < 3 - digits.length) {
                        memory.setByte(indexRegister + i, (byte) 0);
                        i++;
                    }

                    // Write significant digits
                    while(i < digits.length) {
                        memory.setByte(indexRegister + i, (byte) Character.getNumericValue(digits[i]));
                        i++;
                    }

                    incProgramCounter();
                    break;
                }
                // 0xfX65: Load registers V[0] to V[X] from memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x65) {
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
