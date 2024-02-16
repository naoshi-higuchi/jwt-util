package org.nopware.jwt_util.cli.common;

import picocli.CommandLine.Option;

public class HelpOption {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Print usage help and exit.")
    boolean usageHelpRequested;
}
