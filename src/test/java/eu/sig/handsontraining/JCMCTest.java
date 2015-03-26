package eu.sig.handsontraining;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class JCMCTest {

    @Test
    public void testSmallCodeBase() throws Exception {
        String code = "";
        code += "class Aap {\n";
        code += " int noot; //not initialized\n";
        code += "}";

        Path tmpDir = Files.createTempDirectory("aap");
        Path tmpFile = Files.createTempFile(tmpDir, "aap", ".java");
        Path tmpFile2 = Files.createTempFile(tmpDir, "noot", ".java");
        Files.write(tmpFile, code.getBytes());
        Files.write(tmpFile2, code.getBytes());

        Path outputFile = Files.createTempFile(tmpDir, "output", ".txt");
        JCMC.main(new String[] {tmpDir.toString(), outputFile.toString()});

        List<String> lines = Files.readAllLines(outputFile);
        assertThat(lines.size(), equalTo(3));
    }
}