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

        try {
            String result = program.toJSONString().replaceAll("\n", "").replaceAll("\t", "");

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




//        ArrayList<SyntaxElement> variables = program.getProgram();
//
//        for (SyntaxElement syntaxElement : variables) {
//            System.out.println(syntaxElement);
//        }

//        for (int i = 0; i < tokens.size(); i++) {
//            Token token = tokens.get(i);
//            System.out.print(token.value + " ");
//        }
//        System.out.println();
//        System.out.println();

//        for (int i = 0; i < tokens.size(); i++) {
//            Token token = tokens.get(i);
//            System.out.println("Type: " + token.getType());
//            System.out.println("Value: " + token.value);
//            System.out.println("Id: " + i);
//            System.out.print("Line number: " + token.span.lineNum + " ");
//            System.out.print("Position begin: " + token.span.posBegin + " ");
//            System.out.print("Position end: " + token.span.posEnd + " ");
//            System.out.println();
//            System.out.println();
//        }
    }
}
