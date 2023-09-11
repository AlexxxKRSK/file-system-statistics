package org.example;

import org.example.model.AppOptions;
import org.example.model.ExtensionStats;
import org.example.util.CliParser;
import org.example.util.StatsConverter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    private static final Map<String, ExtensionStats> STATISTICS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            AppOptions options = CliParser.parseArgs(args);
            var pathToScan = Paths.get(options.getPath());
            if (Files.notExists(pathToScan)) {
                throw new RuntimeException("Wrong path provided");
            }
            var exec = Executors.newFixedThreadPool(options.getThreadsCount() == null ? 1 : options.getThreadsCount());
            try (var filesStream = Files.walk(pathToScan, options.getRecursionDepth())) {
                var l = filesStream
                        .filter(Files::isRegularFile)
                        .filter(p -> {
                            var extension = getExtensionByStringHandling(p.getFileName().toString());
                            return (options.getExcludeExt() == null && options.getIncludeExt() == null) ||
                                    (options.getExcludeExt() != null && !Arrays.asList(options.getExcludeExt()).contains(extension)) ||
                                    (options.getIncludeExt() != null && Arrays.asList(options.getIncludeExt()).contains(extension));
                        })
                        .map(p -> Executors.callable(() -> analyzeFile(p)))
                        .toList();
                exec.invokeAll(l);
                exec.shutdown();
            }
            System.out.println(StatsConverter.statsConverter(STATISTICS));
        } catch (Exception e) {
            System.out.printf("ERROR %s", e.getMessage());
        }
    }

    private static void analyzeFile(Path path) {
        try (var lines = Files.lines(path, Charset.defaultCharset())) {
            AtomicLong linesCount = new AtomicLong(0L);
            AtomicLong nonEmptyLinesCount = new AtomicLong(0L);
            AtomicLong commentLinesCount = new AtomicLong(0L);
            var extension = getExtensionByStringHandling(path.getFileName().toString());
            var statsByExtension = STATISTICS.get(extension);
            if (statsByExtension == null) {
                statsByExtension = new ExtensionStats();
                STATISTICS.put(extension, statsByExtension);
            }
            statsByExtension.incQuantity();
            statsByExtension.addBytes(Files.size(path));
            lines.forEach(l -> {
                linesCount.incrementAndGet();
                if (!l.isBlank()) {
                    nonEmptyLinesCount.incrementAndGet();
                }
                var trimmedLine = l.trim();
                if (trimmedLine.startsWith("//") || trimmedLine.startsWith("#")) {
                    commentLinesCount.incrementAndGet();
                }
            });

            statsByExtension.addLines(linesCount.get());
            statsByExtension.addNonEmptyLines(nonEmptyLinesCount.get());
            statsByExtension.addCommentLines(commentLinesCount.get());
        } catch (Exception e) {
            System.out.printf("Exception while processing file: %s, message: %s\n", path, e.getMessage());
        }
    }

    private static String getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

}
