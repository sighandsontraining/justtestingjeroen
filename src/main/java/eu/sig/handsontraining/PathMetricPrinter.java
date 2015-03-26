package eu.sig.handsontraining;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

public class PathMetricPrinter {

    public PathMetricPrinter(TreeMap<Path, PathMetrics> metrics, Path output) {
        try {
            StringBuffer result = new StringBuffer();
            metrics.entrySet().forEach(e -> result.append(e.getKey() + " : " + e.getValue() + "\n"));
            Files.write(output, result.toString().getBytes(), TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to print output");
        }
    }
}