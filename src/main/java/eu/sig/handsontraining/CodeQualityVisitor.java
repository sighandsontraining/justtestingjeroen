package eu.sig.handsontraining;

import static com.puppycrawl.tools.checkstyle.api.TokenTypes.BLOCK_COMMENT_BEGIN;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.BLOCK_COMMENT_END;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.COMMENT_CONTENT;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LAND;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LITERAL_CASE;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LITERAL_CATCH;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LITERAL_FOR;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LITERAL_IF;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LITERAL_WHILE;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.LOR;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.QUESTION;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.SINGLE_LINE_COMMENT;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import antlr.Token;
import antlr.TokenStreamException;

import com.puppycrawl.tools.checkstyle.grammars.CommentListener;
import com.puppycrawl.tools.checkstyle.grammars.GeneratedJavaLexer;

public class CodeQualityVisitor extends SimpleFileVisitor<Path> {

    private TreeMap<Path, PathMetrics> metrics = new TreeMap<>();

    private Stack<Map<Path, PathMetrics>> metricStack = new Stack<>();

    private static final List<Integer> MCCABE_TOKENS = Arrays.asList(LITERAL_IF, LITERAL_FOR, LITERAL_WHILE,
        LITERAL_CASE, LITERAL_CATCH, QUESTION, LAND, LOR);
    private static final List<Integer> COMMENT_TOKENS = Arrays.asList(SINGLE_LINE_COMMENT, BLOCK_COMMENT_BEGIN,
        BLOCK_COMMENT_END, COMMENT_CONTENT);

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        metricStack.push(new HashMap<>());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (isJavaFile(file)) {
            List<Token> tokens = parseFile(new String(Files.readAllBytes(file)));
            PathMetrics pathMetrics = new PathMetrics(countMcCabe(tokens), countLinesOfCode(tokens));
            metrics.put(file, pathMetrics);
            metricStack.peek().put(file, pathMetrics);
        }
        return FileVisitResult.CONTINUE;
    }

    protected long countMcCabe(List<Token> tokens) {
        return 1 + tokens.stream().filter(t -> MCCABE_TOKENS.contains(t.getType())).count();
    }

    protected long countLinesOfCode(List<Token> tokens) {
        return tokens.stream().filter(t -> !COMMENT_TOKENS.contains(t.getType())). //
            map(Token::getLine).distinct().count();
    }

    protected List<Token> parseFile(String code) {
        List<Token> tokens = new ArrayList<>();
        try {
            GeneratedJavaLexer lexer = new GeneratedJavaLexer(new StringReader(code));
            lexer.setCommentListener(new DummyCommentListener());
            Token token = lexer.nextToken();
            while (token.getType() != Token.EOF_TYPE) {
                tokens.add(token);
                token = lexer.nextToken();
            }
        } catch (TokenStreamException e) {
            e.printStackTrace(); // ignore and continue
        }
        return tokens;
    }

    private boolean isJavaFile(Path file) {
        return file.getName(file.getNameCount() - 1).toString().endsWith(".java");
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

        Map<Path, PathMetrics> fileMetrics = metricStack.pop();
        PathMetrics dirMetrics = fileMetrics.values().stream()
            .reduce(new PathMetrics(), (total, item) -> total.aggregate(item));

        metrics.put(dir, dirMetrics);
        if (!metricStack.isEmpty()) {
            metricStack.peek().put(dir, dirMetrics);
        }

        return FileVisitResult.CONTINUE;
    }

    public PathMetrics getMetricsForPath(Path path) {
        return metrics.get(path);
    }

    public TreeMap<Path, PathMetrics> getMetrics() {
        return metrics;
    }
}

class DummyCommentListener implements CommentListener {

    @Override
    public void reportSingleLineComment(String type, int startLineNo, int startColNo) {}

    @Override
    public void reportBlockComment(String type, int startLineNo, int startColNo, int endLineNo, int endColNo) {}
}
