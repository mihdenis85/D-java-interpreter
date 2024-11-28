package src.core;

import src.core.syntax.Program;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class Main {
    public static void main(String[] args) throws IOException {
        boolean isTest = true;
        if (isTest) {
            test();
        } else {
            String source = "1.dlang";
            InputStream inputStream = new FileInputStream(source);

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputStream);
            ArrayList<Token> tokens = lexicalAnalyzer.getTokens();

            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
            Program program = syntaxAnalyzer.analyze();

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(program.getProgram());
            Program updatedProgram = semanticAnalyzer.analyze();

            try {
                String result = updatedProgram.toJSONString().replaceAll("\n", "").replaceAll("\t", "");

                StringBuilder stringBuilder = new StringBuilder();
                int tabNumber = 0;
                for (int i = 0; i < result.length(); i++) {
                    char element = result.charAt(i);
                    if (element == '}' || element == ']') {
                        stringBuilder.append('\n');
                        tabNumber--;
                        stringBuilder.append("\t".repeat(Math.max(0, tabNumber)));
                    }
                    stringBuilder.append(element);
                    if (element == '{' || element == '[') {
                        stringBuilder.append('\n');
                        tabNumber++;
                        stringBuilder.append("\t".repeat(Math.max(0, tabNumber)));
                    }
                    if (element == ',') {
                        stringBuilder.append('\n');
                        stringBuilder.append("\t".repeat(Math.max(0, tabNumber)));
                    }
                }
                System.out.println(stringBuilder.toString());
            } catch (Exception e) {
                System.exit(0);
            }

            Interpreter interpreter = new Interpreter(updatedProgram.getProgram());
            interpreter.interpret();
        }
    }

    private static void test() throws IOException {
        File testDir = new File("tests/");
        File[] files = testDir.listFiles();

        assert files != null;
        File[] testFiles = Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".dlang"))
                .sorted(Comparator.comparing(File::getName))
                .toArray(File[]::new);

        int passedTests = 0;
        int totalTests = testFiles.length;

        for (File testFile : testFiles) {
            boolean testCrash = false;
            String testFileName = testFile.getName();
            String testName = testFileName.substring(0, testFileName.lastIndexOf('.')); // e.g., "1"

            String expectedOutputFileName = testName + "_expected.txt";
            String outputFileName = testName + "_output.txt";

            InputStream inputStream = new FileInputStream(testFile);

            // Redirect System.out to capture output
            PrintStream originalOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream newOut = new PrintStream(baos);
            System.setOut(newOut);

            try {
                LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputStream);
                ArrayList<Token> tokens = lexicalAnalyzer.getTokens();

                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
                Program program = syntaxAnalyzer.analyze();

                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(program.getProgram());
                Program updatedProgram = semanticAnalyzer.analyze();

                Interpreter interpreter = new Interpreter(updatedProgram.getProgram());
                interpreter.interpret();
            } catch (Exception e) {
                testCrash = true;
                // e.printStackTrace();
            } finally {
                System.out.flush();
                System.setOut(originalOut);
            }

            String output = baos.toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter("tests/" + outputFileName));
            writer.write(output);
            writer.close();

            File expectedOutputFile = new File("tests/" + expectedOutputFileName);
            if (!expectedOutputFile.exists()) {
                System.out.println("Expected output file " + expectedOutputFileName + " not found for test " + testName + ".");
                continue;
            }
            BufferedReader expectedReader = new BufferedReader(new FileReader(expectedOutputFile));
            StringBuilder expectedOutputBuilder = new StringBuilder();
            String line;
            while ((line = expectedReader.readLine()) != null) {
                expectedOutputBuilder.append(line).append("\n");
            }
            expectedReader.close();

            String expectedOutput = expectedOutputBuilder.toString();

            String normalizedOutput = normalizeLineEndings(output);
//            System.out.println(normalizedOutput);
            String normalizedExpectedOutput = normalizeLineEndings(expectedOutput);

            if (testCrash) {
                System.out.println("Test " + testName + " error.");
            } else if (normalizedOutput.trim().equals(normalizedExpectedOutput.trim())) {
                System.out.println("Test " + testName + " passed.");
                passedTests++;
            } else {
                System.out.println("Test " + testName + " failed.");
            }
        }

        System.out.println("Passed " + passedTests + " out of " + totalTests + " tests.");
    }

    private static String normalizeLineEndings(String str) {
        return str.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
    }
}
