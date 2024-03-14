package org.nopware.jwt_util;

import com.google.common.io.Resources;
import org.nopware.jwt_util.cli.parser.CommandLineParser;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Application {
    private static String loadImageName() {
        URL configUrl = Resources.getResource("config.properties");
        try {
            Properties config = new Properties();
            config.load(configUrl.openStream());
            return config.getProperty("imageName");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class ExecutionStrategy implements CommandLine.IExecutionStrategy {
        @Override
        public int execute(CommandLine.ParseResult parseResult) {
            boolean autoCompletionScriptRequied = parseResult.hasMatchedOption("--auto-completion-script");
            if (autoCompletionScriptRequied) {
                String autoCompletionScript = AutoComplete.bash(loadImageName(), commandLine);
                System.out.print(autoCompletionScript);
                return CommandLine.ExitCode.OK;
            }

            return new CommandLine.RunLast().execute(parseResult);
        }
    }

    private static final CommandLine commandLine = new CommandLine(new CommandLineParser())
            .setCommandName(loadImageName())
            .setExecutionStrategy(new ExecutionStrategy());

    static int execute(String[] args) {
        if (args.length == 0) {
            commandLine.usage(System.out);
            return CommandLine.ExitCode.USAGE;
        }

        return commandLine.execute(args);
    }

    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }
}
