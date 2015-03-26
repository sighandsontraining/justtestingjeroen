package eu.sig.handsontraining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JCMC {

    public static void main(String[] args) throws IOException {
        CodeQualityVisitor visitor = new CodeQualityVisitor();
        Path rootPath = Paths.get(args[0]);
        Files.walkFileTree(rootPath, visitor);
        new PathMetricPrinter(visitor.getMetrics(), Paths.get(args[1]));
    }
}