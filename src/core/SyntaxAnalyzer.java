package src.core;

import src.core.exceptions.TokenOutOfIndexException;
import src.core.syntax.*;

import java.util.ArrayList;

public class SyntaxAnalyzer {
    private final ArrayList<Token> tokensList;
    private int currentTokenIndex;

    public SyntaxAnalyzer(ArrayList<Token> tokensList) {
        this.tokensList = tokensList;
        this.currentTokenIndex = 0;
    }

    public Token getNextToken() {
        if (currentTokenIndex < tokensList.size()) {
            return tokensList.get(currentTokenIndex++);
        }

        return null;
    }

    public Program buildProgram() {
        ArrayList<Variable> variableDeclaration = new ArrayList<>();
        ArrayList<Operator> operatorDeclaration = new ArrayList<>();

        try {
            while (currentTokenIndex < tokensList.size()) {
                Token token = getNextToken();
                if (token.type == Code.tkVar) {
                    variableDeclaration.add(analyzeVariableDeclaration(token.span));
                }
//                else {
//                    operatorDeclaration.add(analyzeOperatorDeclaration());
//                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Program(variableDeclaration, operatorDeclaration);
    }

    private Variable analyzeVariableDeclaration(Span span) {
        Keyword keyword =  new Keyword("var", span);
        Identifier identifier = expectIdentifier();

        return new Variable(keyword, identifier);
    }

//    private Operator analyzeOperatorDeclaration() {
//        Token token = getNextToken();
//        Keyword keyword = new Keyword(token.value, token.span);
//        Identifier identifier = expectIdentifier();
//        Expression expression = analyzeExpression();
//
//        return new Operator(keyword, identifier, expression);
//    }
//
//    private Expression analyzeExpression() {
//
//        // return new Expression();
//    }

    private Identifier expectIdentifier() {
        Token token = getNextToken();

        // TODO: Check whether Identifier is correct

        return new Identifier(token.value, token.span);
    }
}