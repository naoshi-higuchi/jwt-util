package org.nopware.jwt_util.cli.parser;

import org.nopware.jwt_util.cli.commands.CreateCommand;
import org.nopware.jwt_util.cli.commands.DeleteCommand;
import org.nopware.jwt_util.cli.commands.ListCommand;
import org.nopware.jwt_util.cli.commands.UpdateCommand;
import org.nopware.jwt_util.cli.common.HelpOption;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import static picocli.CommandLine.Option;

@Command(
        subcommands = {CreateCommand.class, ListCommand.class, UpdateCommand.class, DeleteCommand.class},
        versionProvider = VersionProvider.class
)
public class CommandLineParser {

    @Mixin
    private HelpOption helpOption;
    @Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionHelpRequested;
}
