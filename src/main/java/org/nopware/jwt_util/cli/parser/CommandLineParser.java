package org.nopware.jwt_util.cli.parser;

import org.nopware.jwt_util.cli.commands.*;
import org.nopware.jwt_util.cli.common.HelpOption;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import static picocli.CommandLine.Option;

@Command(
        subcommands = {DecodeCommand.class, EncodeCommand.class, VerifyCommand.class},
        versionProvider = VersionProvider.class
)
public class CommandLineParser {

    @Mixin
    private HelpOption helpOption;
    @Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionHelpRequested;

    @Option(names = {"--auto-completion-script"}, description = "Output auto-completion script for the specified shell.")
    private boolean autoCompletionScriptRequested;
}
