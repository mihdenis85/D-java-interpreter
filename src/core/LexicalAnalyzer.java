package src.core;

import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<Token> tokens;

    public LexicalAnalyzer() {
        this.tokens = new ArrayList<>();
        analyzeText();
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    private static void analyzeText() {
        System.out.println("Analyzing...");
    }
}
