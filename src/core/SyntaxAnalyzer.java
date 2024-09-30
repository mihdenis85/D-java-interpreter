package src.core;

import src.core.exceptions.TokenOutOfIndexException;

import java.util.ArrayList;

public class SyntaxAnalyzer {
    private final ArrayList<Token> tokensList;
    private int currentTokenIndex;

    public SyntaxAnalyzer(ArrayList<Token> tokensList) {
        this.tokensList = tokensList;
        this.currentTokenIndex = 0;
    }

    public Token getNextToken() throws TokenOutOfIndexException {
        if (currentTokenIndex < tokensList.size()) {
            return tokensList.get(currentTokenIndex++);
        }

        throw new TokenOutOfIndexException("Exception raised:   Token out of index");
    }

    public void analyzeProgram() {
        try {
            while (currentTokenIndex < tokensList.size()) {
                Token token = getNextToken();
                System.out.println(token.type);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}