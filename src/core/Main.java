package src.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String source = "1.dlang";
        InputStream inputStream = new FileInputStream(source);

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputStream);

        ArrayList<Token> tokens = lexicalAnalyzer.getTokens();
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);

        syntaxAnalyzer.analyzeProgram();

//        for (int i = 0; i < tokens.size(); i++) {
//            Token token = tokens.get(i);
//            System.out.print(token.value + " ");
//        }
//        System.out.println();
//        System.out.println();
//
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
