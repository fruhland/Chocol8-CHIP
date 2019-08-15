package one.ruhland.chocol8.application.commands;

import picocli.CommandLine;

@CommandLine.Command(
        name = "chocol8",
        description = "",
        subcommands = { Emulate.class }
)
public class Root implements Runnable {

    @Override
    public void run() {

    }
}
