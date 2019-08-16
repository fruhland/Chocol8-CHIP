package one.ruhland.chocol8.swing.commands;

import one.ruhland.chocol8.chip.Machine;
import picocli.CommandLine;

@CommandLine.Command(
        name = "chocol8",
        description = "",
        subcommands = { Emulate.class }
)
public class Root implements Runnable {

    @Override
    public void run() {
        Machine.printBanner();
    }
}
