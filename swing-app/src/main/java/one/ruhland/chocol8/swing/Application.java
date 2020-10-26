package one.ruhland.chocol8.swing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import one.ruhland.chocol8.chip.DefaultSound;
import one.ruhland.chocol8.chip.Machine;
import picocli.CommandLine;

@CommandLine.Command(
    name = "chocol8",
    description = "",
    showDefaultValues = true,
    separator = " ")
public class Application implements Runnable {

    @CommandLine.Option(
        names = {"-o", "--open" },
        description = "The CHIP-8 ROM to open")
    private String romPath;

    @CommandLine.Option(
        names = {"-f", "--frequency" },
        description = "The CPU frequency")
    private int cpuFrequency = 1000;

    @Override
    public void run() {
        Machine machine;

        try {
            machine = new Machine(SwingGraphics.class, DefaultSound.class, SwingKeyboard.class);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return;
        }

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
    }

    public static void main(String... args) {
        Machine.printBanner();

        CommandLine cli = new CommandLine(new Application());
        cli.setCaseInsensitiveEnumValuesAllowed(true);
        cli.execute(args);
    }
}
