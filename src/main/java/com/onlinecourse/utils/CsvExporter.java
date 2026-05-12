package com.onlinecourse.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CsvExporter {
    private CsvExporter() {
    }

    public static void write(Path file, List<String[]> rows) throws AppException {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                for (String[] row : rows) {
                    writer.write(Stream.of(row).map(CsvExporter::escape).collect(Collectors.joining(",")));
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            throw new AppException("Could not export CSV file.", ex);
        }
    }

    private static String escape(String value) {
        String text = value == null ? "" : value;
        boolean mustQuote = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        String escaped = text.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
