package one.ruhland.chocol8.chip;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Machine {

    private final Memory memory;
    private final Graphics graphics;
    private final Cpu cpu;

    private boolean isRunning = false;

    public Machine(final Class<? extends Graphics> graphicsClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.memory = new Memory();
        this.graphics = graphicsClass.getConstructor(int.class, int.class, Memory.class).newInstance(64, 32, memory);
        this.cpu = new Cpu(memory, graphics);
    }

    public void reset() {
        stop();
        cpu.reset();
        memory.reset();
        graphics.reset();
    }

    public void loadProgram(final String fileName) throws IOException {
        reset();
        memory.setBytes(Memory.PROGRAM_START, Files.readAllBytes(Paths.get(fileName)));
    }

    public void run() {
        if(isRunning) {
            return;
        }

        isRunning = true;

        new Thread(() -> {
            while(isRunning) {
                cpu.runCycle();
            }
        }, "Emulator-Thread").start();
    }

    public void stop() {
        isRunning = false;
    }

    public Memory getMemory() {
        return memory;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Cpu getCpu() {
        return cpu;
    }
}
