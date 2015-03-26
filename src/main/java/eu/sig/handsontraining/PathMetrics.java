package eu.sig.handsontraining;

public class PathMetrics {

    private long mcCabe = 0;
    private long linesOfCode = 0;

    public PathMetrics() {}

    public PathMetrics(long mcCabe, long linesOfCode) {
        this.mcCabe = mcCabe;
        this.linesOfCode = linesOfCode;
    }

    public long getLinesOfCode() {
        return linesOfCode;
    }

    public long getMcCabe() {
        return mcCabe;
    }

    public PathMetrics aggregate(PathMetrics other) {
        return new PathMetrics(mcCabe + other.mcCabe, linesOfCode + other.linesOfCode);
    }

    @Override
    public String toString() {
        return linesOfCode + ", " + mcCabe;
    }

}
