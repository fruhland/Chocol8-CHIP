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
            names = {"-o", "--open" },
            description = "The CHIP-8 ROM to open")
    private String romPath;

    @CommandLine.Option(
            names = {"-f", "--frequency" },
            description = "The CPU frequency")
    private int cpuFrequency = 1000;

    @Override
    public Void call() throws Exception {
        var machine = new Machine(SwingGraphics.class);
        var window = new MainWindow(machine);
        window.setVisible(true);

        machine.getCpu().getClock().setFrequency(cpuFrequency);

        if(romPath != null) {
            try {
                machine.loadProgram(romPath);
                window.setTitle(window.getTitle() + " - " + new File(romPath).getName());
                machine.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
