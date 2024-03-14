package org.nopware.jwt_util;

import org.nopware.jwt_util.cli.parser.CommandLineParser;
import picocli.CommandLine;

public class Application {

    static int execute(String[] args) {
        CommandLine commandLine = new CommandLine(new CommandLineParser());

        if (args.length == 0) {
            commandLine.usage(System.out);
            return 1;
        }

        return commandLine.execute(args);
    }

    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }
}
