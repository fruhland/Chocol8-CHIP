package one.ruhland.chocol8.chip;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Cpu {

    private static final double DEFAULT_FREQUENCY = 1000;

    private byte[] vRegisters = new byte[0x10];
    private short programCounter = 0x0200;
    private short indexRegister = 0x0000;

    private Keyboard.Key currentKey = null;

    private final Memory memory;
    private final Graphics graphics;
    private final Sound sound;
    private final Keyboard keyboard;
    private final Timer timer;

    private final Clock clock;
    private final Stack stack;

    Cpu(final Memory memory, final Graphics graphics, final Sound sound, final Keyboard keyboard, final Timer timer) {
        this.memory = memory;
        this.graphics = graphics;
        this.sound = sound;
        this.keyboard = keyboard;
        this.timer = timer;

        clock = new Clock(DEFAULT_FREQUENCY, "CpuThread");
        stack = new Stack((byte) 16);

        clock.addRunnable(this::runCycle);
    }

    void reset() {
        stack.reset();
        timer.reset();
        Arrays.fill(vRegisters, (byte) 0);
        programCounter = 0x0200;
        indexRegister = 0x0000;
    }

    void start() {
        timer.start();
        sound.start();
        clock.start();
    }

    void stop() {
        clock.stop();
        sound.stop();
        timer.stop();
    }

    public void runCycle() {
        short opcode = (short) unsignedOperation(memory.getByte(programCounter), memory.getByte(programCounter + 1),
                (operand1, operand2) -> operand1 << 8 | operand2);
        executeOpcode(opcode);
    }

    public Clock getClock() {
        return clock;
    }

    public Stack getStack() {
        return stack;
    }

    public byte[] getVRegisters() {
        return vRegisters;
    }

    public short getProgramCounter() {
        return programCounter;
    }

    public short getIndexRegister() {
        return indexRegister;
    }

    public void setProgramCounter(short programCounter) {
        this.programCounter = programCounter;
    }

    public void setIndexRegister(short indexRegister) {
        this.indexRegister = indexRegister;
    }

    public void setCurrentKey(Keyboard.Key currentKey) {
        this.currentKey = currentKey;
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

    private int getUnsignedByte(final byte value) {
        return value & 0xff;
    }

    private void conditionalJump(final byte operand1, final byte operand2, final BiFunction<Integer, Integer, Boolean> operator) {
        if(operator.apply(getUnsignedByte(operand1), getUnsignedByte(operand2))) {
            incProgramCounter();
        }
    }

    private void conditionalJump(final byte operand1, final Function<Integer, Boolean> operator) {
        if(operator.apply(getUnsignedByte(operand1))) {
            incProgramCounter();
        }
    }

    private int unsignedOperation(final byte operand1, final byte operand2, final BiFunction<Integer, Integer, Integer> operator) {
        return operator.apply(getUnsignedByte(operand1), getUnsignedByte(operand2));
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
                // 0x2NNN: Call subroutine at NNN
                stack.push((programCounter));
                jumpToAddress((short) (opcode & 0x0fff));
                break;
            case 0x3:
                // 0x3XNN: Skip next instruction if V[X] == NN
                conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], (byte) (opcode & 0x00ff), Integer::equals);
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
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8], vRegisters[(opcode & 0x00f0) >> 4], Integer::equals);
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
                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand1 | operand2);
                    incProgramCounter();
                    break;
                }
                // 0x8XY2: V[X] &= V[Y]
                else if((opcode & 0x000f) == 0x2) {
                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand1 & operand2);
                    incProgramCounter();
                    break;
                }
                // 0x8XY3: V[X] ^= V[Y]
                else if((opcode & 0x000f) == 0x3) {
                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand1 ^ operand2);
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
                    vRegisters[0xf] = result < 0 ? (byte) 0 : (byte) 1;

                    incProgramCounter();
                    break;
                }
                // 0x8XY6: V[X] = V[Y] >> 1; V[F] = LSB(V[Y])
                else if((opcode & 0x000f) == 0x6) {
                    vRegisters[0xf] = (byte) (vRegisters[(opcode & 0x00f0) >> 4] & 0x01);
                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) (vRegisters[(opcode & 0x00f0) >> 4] >> 1);
                    incProgramCounter();
                    break;
                }
                // 0x8XY7: V[X] = V[Y] - V[X]; V[F] = (Borrow ? 1 : 0)
                else if((opcode & 0x000f) == 0x7) {
                    int result = unsignedOperation(vRegisters[(opcode & 0x0f00) >> 8],
                            vRegisters[(opcode & 0x00f0) >> 4], (operand1, operand2) -> operand2 - operand1);

                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) result;
                    vRegisters[0xf] = result < 0 ? (byte) 0 : (byte) 1;

                    incProgramCounter();
                    break;
                }
                // 0x8XYE: V[X] <<= 1; V[F] = MSB(V[X])
                else if((opcode & 0x000f) == 0xe) {
                    vRegisters[0xf] = (byte) (vRegisters[(opcode & 0x00f0) >> 4] & 0x80);
                    vRegisters[(opcode & 0x0f00) >> 8] = (byte) (vRegisters[(opcode & 0x00f0) >> 4] << 1);
                    incProgramCounter();
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Unknown opcode: 0x%04x!\n", opcode));
                }
            case 0x9:
                // 0x9XY0: Skip next instruction if V[X] != V[Y]
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
                jumpToAddress((short) (getUnsignedByte(vRegisters[0]) + (opcode & 0x0fff)));
                break;
            }
            case 0xc:
                // 0xCXNN: V[X] = rand() & NN
                vRegisters[(opcode & 0x0f00) >> 8] = (byte) (((int) (Math.random() * 0xff)) & (opcode & 0x00ff));
                incProgramCounter();
                break;
            case 0xd: {
                // 0xDXY: Draw a sprite at (V[X], V[Y]); If any pixel has been flipped from set to unset -> V[F] = 1, else V[F] = 0
                byte x = vRegisters[(opcode & 0x0f00) >> 8];
                byte y = vRegisters[(opcode & 0x00f0) >> 4];
                byte height = (byte) (opcode & 0x000f);

                vRegisters[0xf] = (byte) (graphics.drawSprite(x, y, height, indexRegister) ? 1 : 0);

                incProgramCounter();
                break;
            }
            case 0xe:
                // 0xeX9E: Skip next instruction if key in V[X] is pressed
                if((opcode & 0x00ff) == 0x9e) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8],
                            operand1 -> keyboard.isKeyPressed(Keyboard.Key.fromInt(operand1)));
                    incProgramCounter();
                    break;
                }
                // 0xeXA1: Skip next instruction if key in V[X] is not pressed
                if((opcode & 0x00ff) == 0xa1) {
                    conditionalJump(vRegisters[(opcode & 0x0f00) >> 8],
                            operand1 -> !keyboard.isKeyPressed(Keyboard.Key.fromInt(operand1)));
                    incProgramCounter();
                    break;
                }
            case 0xf:
                // 0xfX07: V[X] = timer
                if((opcode & 0x00ff) == 0x07) {
                    vRegisters[(opcode & 0x0f00) >> 8] = timer.getCounter();
                    incProgramCounter();
                    break;
                }
                // 0xfX0a: Wait until a key is pressed and put the pressed key in V[X]
                if((opcode & 0x00ff) == 0x0a) {
                    if (currentKey != null) {
                        currentKey = keyboard.getPressedKey();
                        break;
                    } else {
                        Keyboard.Key newKey = keyboard.getPressedKey();
                        if(newKey != null) {
                            vRegisters[(opcode & 0x0f00) >> 8] = newKey.getValue();
                            currentKey = newKey;
                            incProgramCounter();
                        }
                    }
                    break;
                }
                // 0xfX15: timer = V[X]
                if((opcode & 0x00ff) == 0x15) {
                    timer.setCounter(vRegisters[(opcode & 0x0f00) >> 8]);
                    incProgramCounter();
                    break;
                }
                // 0xfX18: soundTimer = V[X]
                if((opcode & 0x00ff) == 0x18) {
                    sound.setCounter(vRegisters[(opcode & 0x0f00) >> 8]);
                    incProgramCounter();
                    break;
                }
                // 0xfX1e: index += V[X]
                if((opcode & 0x00ff) == 0x1e) {
                    indexRegister += getUnsignedByte(vRegisters[(opcode & 0x0f00) >> 8]);
                    incProgramCounter();
                    break;
                }
                // 0xfX29: index = address of character sprite of V[X]
                else if((opcode & 0x00ff) == 0x29) {
                    indexRegister = (short) (Memory.FONT_START + getUnsignedByte(vRegisters[(opcode & 0x0f00) >> 8]) * 5);
                    incProgramCounter();
                    break;
                }
                // 0xfX55: Write registers V[0] to V[X] consecutively to memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x55) {
                    for(int i = 0; i <= (opcode & 0x0f00) >> 8; i++) {
                        memory.setByte(indexRegister + i, vRegisters[i]);
                    }

                    incProgramCounter();
                    break;
                }
                // 0xfX33: Write BCD(V[X]) to memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x33) {
                    char[] digits = String.format("%03d", getUnsignedByte(vRegisters[(opcode & 0x0f00) >> 8])).toCharArray();

                    for(int i = 0; i < 3; i++) {
                        memory.setByte(indexRegister + i, (byte) Character.getNumericValue(digits[i]));
                    }

                    incProgramCounter();
                    break;
                }
                // 0xfX65: Load registers V[0] to V[X] from memory at the address pointed to by the index register
                else if((opcode & 0x00ff) == 0x65) {
                    for(int i = 0; i <= (opcode & 0x0f00) >> 8; i++) {
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
