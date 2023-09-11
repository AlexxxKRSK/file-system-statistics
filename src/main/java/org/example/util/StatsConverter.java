package org.example.util;

import org.example.model.ExtensionStats;

import java.util.Map;
import java.util.stream.Collectors;

public class StatsConverter {

    public static String statsConverter(Map<String, ExtensionStats> stat) {
        String format = "\n%1$-20s|%2$-20s|%3$-20s|%4$-15s|%5$-15s|%6$-15s|";
        var header = String.format(
                format, "extension", "files", "size, bytes", "lines", "non-empty lines", "comment lines");
        var result = stat.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        String.format(format,
                                "'" + entry.getKey() + "'",
                                entry.getValue().getQuantity(),
                                String.format("%,d", entry.getValue().getBytes().get()),
                                entry.getValue().getLines(),
                                entry.getValue().getNonEmptyLines(),
                                entry.getValue().getCommentLines()
                        )
                )
                .collect(Collectors.joining());
        String totals = String.format(format,
                "TOTAL",
                stat.values().stream().mapToLong(extensionStats -> extensionStats.getQuantity().get()).sum(),
                String.format(
                        "%,d",
                        stat.values().stream().mapToLong(extensionStats -> extensionStats.getBytes().get()).sum()
                ),
                stat.values().stream().mapToLong(extensionStats -> extensionStats.getLines().get()).sum(),
                stat.values().stream().mapToLong(extensionStats -> extensionStats.getNonEmptyLines().get()).sum(),
                stat.values().stream().mapToLong(extensionStats -> extensionStats.getCommentLines().get()).sum()
        );
        return header + result + totals;
    }
}

