package src.core;

import src.core.exceptions.InvalidIdentifierNameException;
import src.core.exceptions.UnexpectedTokenException;
import src.core.literals.BooleanLiteral;
import src.core.syntax.*;
import src.core.enums.Code;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;
import src.core.syntax.statements.IfStatement;

import java.util.ArrayList;
import java.util.regex.*;

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
        ArrayList<SyntaxElement> syntaxElements = new ArrayList<>();

        try {
            while (currentTokenIndex < tokensList.size()) {
                Token token = getNextToken();
                System.out.println(token.type);
                switch (token.type) {
                    case tkVar:
                        syntaxElements.add(analyzeVariableDeclaration(token));
                        break;
                    case tkIfStatement:
                        syntaxElements.add(analyzeIfStatementDeclaration(token));
                        break;
                    default:
                        throw new Exception("Error");
                };
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Program(syntaxElements);
    }

    public IfStatement analyzeIfStatementDeclaration(Token token) {
        // TODO: Remove currentTokenIndex, add it in functions

        try {
            Expression expression = new Expression(new BooleanLiteral(token.span, token.value)); // TODO: Add method for parsing condition
            currentTokenIndex++;

            expectKeyword(Code.tkThen);
            System.out.println("Analyzing body"); // TODO: Add method for parsing body

            ArrayList<StatementElement> statementBody = new ArrayList<>();
            currentTokenIndex++;

            ArrayList<StatementElement> elseStatementBody = new ArrayList<>();
            if (expectKeyword(Code.tkElse)) {
                System.out.println("Analyzing else body"); // TODO: Add method for parsing body
                currentTokenIndex++;
            };

            expectKeyword(Code.tkEnd);

            return new IfStatement(expression, statementBody, elseStatementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    public void expectExpression() {
        // TODO: Complete method
    }

    public boolean expectKeyword(Code tokenType) throws UnexpectedTokenException {
        Token token = getNextToken();
        if (token.type != tokenType) {
            throw new UnexpectedTokenException(token.span, token.type, tokenType);
        }

        return true;
    }

    public Variable analyzeVariableDeclaration(Token token) {
        System.out.println("Analyzing variable declaration");
        try {
            Keyword keyword = new Keyword(token.value, token.span);
            Identifier identifier = expectIdentifier();

            return new Variable(keyword, identifier);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    private Identifier expectIdentifier() throws InvalidIdentifierNameException {
        Token token = getNextToken();

        boolean isValid = Pattern.matches("^[a-zA-Z$_][a-zA-Z0-9$_]*$", token.value);

        if (!isValid) {
            throw new InvalidIdentifierNameException(token.span);
        }

        return new Identifier(token.value, token.span);
    }
}