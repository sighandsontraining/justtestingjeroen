package eu.sig.handsontraining;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.junit.Test;

import antlr.Token;

public class CodeQualityVisitorTest {

    @Test
    public void testLinesOfCodeEmptyFile() throws Exception {
        String code = "";
        assertThat(new CodeQualityVisitor().countLinesOfCode(toTokens(code)), equalTo(0L));
    }

    @Test
    public void testLinesOfCodeCommentsOnly() throws Exception {
        String code = "";
        code += "/* comment\n";
        code += "more comment */\n";
        assertThat(new CodeQualityVisitor().countLinesOfCode(toTokens(code)), equalTo(0L));
    }

    @Test
    public void testLinesOfCodeSingleLineCommentsOnly() throws Exception {
        String code = "";
        code += "// comment\n";
        code += "// more comment\n";
        assertThat(new CodeQualityVisitor().countLinesOfCode(toTokens(code)), equalTo(0L));
    }

    @Test
    public void testLinesOfCodeSimpleCode() throws Exception {
        String code = "";
        code += "class Aap {}\n";
        assertThat(new CodeQualityVisitor().countLinesOfCode(toTokens(code)), equalTo(1L));
    }

    @Test
    public void testLinesOfCodeMixedWithComments() throws Exception {
        String code = "";
        code += "class Aap {\n";
        code += " int noot; //not initialized\n";
        code += "}";
        assertThat(new CodeQualityVisitor().countLinesOfCode(toTokens(code)), equalTo(3L));
    }

    @Test
    public void testMcCabeNoComplexity() throws Exception {
        String code = "";
        code += "x = 3;";
        assertThat(new CodeQualityVisitor().countMcCabe(toTokens(code)), equalTo(1L));
    }

    @Test
    public void testMcCabeIf() throws Exception {
        String code = "";
        code += "if (x > 3)";
        assertThat(new CodeQualityVisitor().countMcCabe(toTokens(code)), equalTo(2L));
    }

    @Test
    public void testMcCabeFor() throws Exception {
        String code = "";
        code += "for (int i = 0; i < 3 && i > -1; i++) {";
        assertThat(new CodeQualityVisitor().countMcCabe(toTokens(code)), equalTo(3L));
    }

    @Test
    public void testFileMetrics() throws Exception {
        String code = "";
        code += "class Aap {\n";
        code += " int noot; //not initialized\n";
        code += "}";

        Path tmpFile = Files.createTempFile("aap", ".java");
        Files.write(tmpFile, code.getBytes());

        CodeQualityVisitor visitor = new CodeQualityVisitor();
        visitor.preVisitDirectory(tmpFile.getParent(), null);
        visitor.visitFile(tmpFile, Files.readAttributes(tmpFile, BasicFileAttributes.class));

        assertThat(visitor.getMetricsForPath(tmpFile), notNullValue());
        assertThat(visitor.getMetricsForPath(tmpFile).getLinesOfCode(), equalTo(3L));
        assertThat(visitor.getMetricsForPath(tmpFile).getMcCabe(), equalTo(1L));
    }

    @Test
    public void testDirMetrics() throws Exception {
        String code = "";
        code += "class Aap {\n";
        code += " int noot; //not initialized\n";
        code += "}";

        Path tmpDir = Files.createTempDirectory("aap");
        Path tmpFile = Files.createTempFile(tmpDir, "aap", ".java");
        Path tmpFile2 = Files.createTempFile(tmpDir, "noot", ".java");
        Files.write(tmpFile, code.getBytes());
        Files.write(tmpFile2, code.getBytes());

        CodeQualityVisitor visitor = new CodeQualityVisitor();
        Files.walkFileTree(tmpDir, visitor);

        assertThat(visitor.getMetricsForPath(tmpDir), notNullValue());
        assertThat(visitor.getMetricsForPath(tmpDir).getLinesOfCode(), equalTo(6L));
        assertThat(visitor.getMetricsForPath(tmpDir).getMcCabe(), equalTo(2L));

    }

    @Test
    public void testSubDirMetrics() throws Exception {
        String code = "";
        code += "class Aap {\n";
        code += " int noot; //not initialized\n";
        code += "}";

        Path tmpDir = Files.createTempDirectory("aap");
        Path tmpSubDir = Files.createTempDirectory(tmpDir, "noot");
        Path tmpSubDir2 = Files.createTempDirectory(tmpSubDir, "mies");
        Path tmpFile = Files.createTempFile(tmpSubDir, "aap", ".java");
        Path tmpFile2 = Files.createTempFile(tmpSubDir2, "noot", ".java");
        Files.write(tmpFile, code.getBytes());
        Files.write(tmpFile2, code.getBytes());

        CodeQualityVisitor visitor = new CodeQualityVisitor();
        Files.walkFileTree(tmpDir, visitor);

        assertThat(visitor.getMetricsForPath(tmpDir), notNullValue());
        assertThat(visitor.getMetricsForPath(tmpDir).getLinesOfCode(), equalTo(6L));
        assertThat(visitor.getMetricsForPath(tmpDir).getMcCabe(), equalTo(2L));

        assertThat(visitor.getMetricsForPath(tmpSubDir), notNullValue());
        assertThat(visitor.getMetricsForPath(tmpSubDir).getLinesOfCode(), equalTo(6L));
        assertThat(visitor.getMetricsForPath(tmpSubDir).getMcCabe(), equalTo(2L));

        assertThat(visitor.getMetricsForPath(tmpSubDir2), notNullValue());
        assertThat(visitor.getMetricsForPath(tmpSubDir2).getLinesOfCode(), equalTo(3L));
        assertThat(visitor.getMetricsForPath(tmpSubDir2).getMcCabe(), equalTo(1L));
    }

    private List<Token> toTokens(String code) {
        return new CodeQualityVisitor().parseFile(code);
    }
}
