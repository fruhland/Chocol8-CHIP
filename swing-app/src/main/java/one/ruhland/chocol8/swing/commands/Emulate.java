package one.ruhland.chocol8.swing.commands;

import java.io.File;
import java.io.IOException;
import one.ruhland.chocol8.chip.Machine;
import one.ruhland.chocol8.swing.MainWindow;
import one.ruhland.chocol8.swing.SwingGraphics;
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
            try {
                machine.loadProgram(romPath);
                window.setTitle(window.getTitle() + " - " + new File(romPath).getName());
                machine.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
