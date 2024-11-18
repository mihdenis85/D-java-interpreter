package src.core;

import src.core.syntax.Operator;
import src.core.syntax.Program;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.SyntaxElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String source = "1.dlang";
        InputStream inputStream = new FileInputStream(source);

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputStream);

        ArrayList<Token> tokens = lexicalAnalyzer.getTokens();
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);

        Program program = syntaxAnalyzer.buildProgram();

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(program.getProgram());
        Program updatedProgram = semanticAnalyzer.analyze();

        Interpreter interpreter = new Interpreter(updatedProgram.getProgram());

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

        interpreter.interpret();
    }
}
