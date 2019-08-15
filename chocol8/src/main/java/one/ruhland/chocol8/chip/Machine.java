package one.ruhland.chocol8.chip;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Machine {

    private final Memory memory;
    private final Graphics graphics;
    private final Cpu cpu;

    public Machine(final Class<? extends Graphics> graphicsClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.memory = new Memory();
        this.graphics = graphicsClass.getConstructor(int.class, int.class, Memory.class).newInstance(64, 32, memory);
        this.cpu = new Cpu(memory, graphics);

        reset();
    }

    public void reset() {
        cpu.reset();
        memory.reset();
        graphics.reset();
    }

    public void loadProgram(final String fileName) throws IOException {
        memory.setBytes(Memory.PROGRAM_START, Files.readAllBytes(Paths.get(fileName)));
    }

    public void run() {
        while(true) {
            cpu.runCycle();
        }
    }
}
