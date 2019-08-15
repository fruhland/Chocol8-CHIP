package one.ruhland.chocol8.application.commands;

import one.ruhland.chocol8.swing.SwingGraphics;
import one.ruhland.chocol8.chip.Machine;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "emulate",
        description = "Starts the emulator.%n",
        showDefaultValues = true,
        separator = " ")
public class Emulate implements Callable<Void> {

    @Override
    public Void call() throws Exception {
        Machine machine = new Machine(SwingGraphics.class);
        machine.run();

        return null;
    }
}
