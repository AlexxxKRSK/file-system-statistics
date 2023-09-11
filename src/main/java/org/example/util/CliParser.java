package org.example.util;

import org.apache.commons.cli.*;
import org.example.model.AppOptions;

public class CliParser {

    private static final String RECURSIVE = "recursive";
    private static final String RECURSION_DEPTH = "maxDepth";
    private static final String THREADS_SIZE = "thread";
    private static final String INCLUDE_EXTENSIONS = "includeExt";
    private static final String EXCLUDE_EXTENSIONS = "excludeExt";

    public static AppOptions parseArgs(String[] args) throws ParseException {
        CommandLine commandLine;
        Option recursive = Option.builder(RECURSIVE)
                .desc("Enable recursive")
                .longOpt("recursive")
                .build();
        Option maxDepth = Option.builder(RECURSION_DEPTH)
                .desc("Recursion depth")
                .hasArg()
                .longOpt("max-depth")
                .build();
        Option thread = Option.builder(THREADS_SIZE)
                .desc("Number of threads")
                .hasArg()
                .longOpt("thread")
                .build();
        Option includeExt = Option.builder(INCLUDE_EXTENSIONS)
                .desc("Files extension to include")
                .hasArgs()
                .valueSeparator(',')
                .longOpt("include-ext")
                .build();
        Option excludeExt = Option.builder(EXCLUDE_EXTENSIONS)
                .desc("Files extension to exclude")
                .hasArgs()
                .valueSeparator(',')
                .longOpt("exclude-ext")
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(recursive);
        options.addOption(maxDepth);
        options.addOption(thread);
        options.addOption(includeExt);
        options.addOption(excludeExt);

        try {
            commandLine = parser.parse(options, args);

            var appOptions = new AppOptions();
            appOptions.setRecursive(commandLine.hasOption(RECURSIVE));
            if (commandLine.hasOption(RECURSION_DEPTH)) {
                appOptions.setRecMaxDepth(Integer.parseInt(commandLine.getOptionValue(RECURSION_DEPTH)));
            }
            if (commandLine.hasOption(THREADS_SIZE)) {
                appOptions.setThreadsCount(Integer.parseInt(commandLine.getOptionValue(THREADS_SIZE)));
            }
            if (commandLine.hasOption(INCLUDE_EXTENSIONS)) {
                appOptions.setIncludeExt(commandLine.getOptionValues(INCLUDE_EXTENSIONS));
            }
            if (commandLine.hasOption(EXCLUDE_EXTENSIONS)) {
                appOptions.setExcludeExt(commandLine.getOptionValues(EXCLUDE_EXTENSIONS));
            }

            String[] remainder = commandLine.getArgs();
            if (remainder.length != 1) {
                throw new RuntimeException("Unable to get path from args");
            }
            appOptions.setPath(remainder[0]);
            if (appOptions.getPath() == null) {
                throw new RuntimeException("Path not provided. Please provide path to scan");
            }
            if (appOptions.getIncludeExt() != null && appOptions.getExcludeExt() != null) {
                throw new RuntimeException("ERROR Include and exclude lists are present. Leave only one option");
            }

            System.out.println();
            System.out.println(appOptions);
            return appOptions;
        } catch (ParseException exception) {
            System.out.print("Parse error: ");
            System.out.println(exception.getMessage());
            throw exception;
        }
    }
}