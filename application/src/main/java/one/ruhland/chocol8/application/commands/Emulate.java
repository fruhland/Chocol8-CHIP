package one.ruhland.chocol8.application.commands;

import one.ruhland.chocol8.swing.MainWindow;
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

    @CommandLine.Option(
            names = {"-f", "--file" },
            description = "The CHIP-8 ROM to execute")
    private String romPath;

    @Override
    public Void call() throws Exception {
        var machine = new Machine(SwingGraphics.class);
        var window = new MainWindow(machine);
        window.setVisible(true);

        if(romPath != null) {
            machine.loadProgram(romPath);
            machine.run();
        }

        return null;
    }
}
