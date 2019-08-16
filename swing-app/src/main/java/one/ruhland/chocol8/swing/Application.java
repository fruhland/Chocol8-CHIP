package one.ruhland.chocol8.swing;

import one.ruhland.chocol8.swing.commands.Root;
import picocli.CommandLine;

public class Application {


    public static void main(String... args) {
        CommandLine cli = new CommandLine(new Root());
        cli.setCaseInsensitiveEnumValuesAllowed(true);
        cli.parseWithHandlers(
                new CommandLine.RunAll().useOut(System.out),
                CommandLine.defaultExceptionHandler().useErr(System.err),
                args);
    }
}
