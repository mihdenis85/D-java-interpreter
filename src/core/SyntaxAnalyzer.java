package src.core;

import src.core.enums.Punct;
import src.core.exceptions.InvalidIdentifierNameException;
import src.core.exceptions.InvalidSyntaxException;
import src.core.exceptions.TokenOutOfIndexException;
import src.core.exceptions.UnexpectedTokenException;
import src.core.literals.BooleanLiteral;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;
import src.core.literals.StringLiteral;
import src.core.syntax.*;
import src.core.enums.Code;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;
import src.core.syntax.statements.ForLoop;
import src.core.syntax.statements.IfStatement;
import src.core.syntax.statements.WhileLoop;

import java.util.ArrayList;
import java.util.List;
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

    private void skipToken() {
        currentTokenIndex++;
    }

    private Token peekToken(int ahead) throws TokenOutOfIndexException {
        if (currentTokenIndex + ahead < tokensList.size())
            return tokensList.get(currentTokenIndex + ahead);
        throw new TokenOutOfIndexException("Could not parse the token");
    }

    private void matchPunct(Code code) throws UnexpectedTokenException {
        Token nextToken = getNextToken();
        if (!(Punct.contains(nextToken.type) && code != null)) {
            throw new UnexpectedTokenException(nextToken.span, nextToken.type, code);
        }
    }

    private Code getIdentifierOnMatch() throws UnexpectedTokenException {
        Token nextToken = getNextToken();
        if (nextToken.type == Code.tkIdentifier) {
            return Code.tkIdentifier;
        } else {
            throw new UnexpectedTokenException(nextToken.span, nextToken.type, null);
        }
    }

    private boolean expectPunct(Code code, int ahead) throws TokenOutOfIndexException {
        return currentTokenIndex + ahead < tokensList.size()
                && Punct.contains(peekToken(ahead).type)
                && code != null;
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
                    case tkWhileLoop:
                        syntaxElements.add(analyzeWhileLoopDeclaration(token));
                    case tkForLoop:
                        syntaxElements.add(analyzeForLoopDeclaration(token));
                    default:
                        throw new Exception("Error");
                };
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Program(syntaxElements);
    }

    private SyntaxElement parseStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token peek = peekToken(0);
        return switch (peek.type) {
            case Code.tkIfStatement -> analyzeIfStatementDeclaration(peek);
            case Code.tkReturn -> analyzeReturnStatement();
            case Code.tkWhileLoop -> analyzeWhileLoopDeclaration(peek);
            case Code.tkVar -> analyzeVariableDeclaration(peek);
            case Code.tkIdentifier -> analyzeIdentifierStatement(peek);
            default -> throw new InvalidSyntaxException("Expected `"
                    + Code.tkIfStatement.name() + "`, `"
                    + Code.tkReturn.name() + "`, `"
                    + Code.tkWhileLoop.name() + "`, `"
                    + Code.tkVar.name() + "` or `"
                    + "`, but got `" + peek + "` instead"
                    + "\n\tat line " + peek.span.lineNum + " column " + peek.span.posBegin);
        };
    }

    private SyntaxElement analyzeIdentifierStatement(Token peek) {
        return null;
    }

    private SyntaxElement parseAssignment() {
        return null;
    }

    private StatementElement analyzeReturnStatement() {
        return null;
    }

    private SyntaxElement analyzeForLoopDeclaration(Token token) {
        try {
            Identifier ident = expectIdentifier();

            expectKeyword(Code.tkIn);
            Expression expression = parseExpression();

            expectKeyword(Code.tkLoop);
            ArrayList<SyntaxElement> loopBody = parseBody();
            currentTokenIndex++;

            expectKeyword(Code.tkEnd);

            return new ForLoop(ident, expression, loopBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public WhileLoop analyzeWhileLoopDeclaration(Token token) {
        try {
            Expression expression = new Expression(new BooleanLiteral(token.span, token.value)); // TODO: Add method for parsing condition
            currentTokenIndex++;

            expectKeyword(Code.tkLoop);
            System.out.println("Analyzing body"); // TODO: Add method for parsing body

            ArrayList<StatementElement> statementBody = new ArrayList<>();
            currentTokenIndex++;

            expectKeyword(Code.tkEnd);
            return new WhileLoop(expression, statementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
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

    public Expression parseExpression() throws UnexpectedTokenException, TokenOutOfIndexException {
        ExpressionElement expression = parseExpressionElement();

        List<Expression> chain = new ArrayList<>();
        while (expectPunct(Code.tkDot, 0)) {
            skipToken();

            Code id = getIdentifierOnMatch();

            List<ExpressionElement> arguments = null;
            if (expectPunct(Code.tkOpenedBracket, 0)) {
                skipToken();

                arguments = new ArrayList<>();
                if (!expectPunct(Code.tkClosedBracket, 0)) {
                    arguments = parseArguments();
                }

                matchPunct(Code.tkClosedBracket);
            }

            chain.add(new Expression((src.core.syntax.interfaces.ExpressionElement) arguments));
            // TODO: Implement normal arguments parsing
        }

        return new Expression(expression);
    }


    public ExpressionElement parseExpressionElement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token peek = peekToken(0);
        return switch (peek) {
            case IntegerLiteral il -> { skipToken(); yield il; }
            case RealLiteral rl -> { skipToken(); yield rl; }
            case BooleanLiteral bl -> { skipToken(); yield bl; }
            case StringLiteral sl -> { skipToken(); yield sl; }
            default -> throw new UnexpectedTokenException(peek.span, peek.getType(), null);
        };
    }
    private List<ExpressionElement> parseArguments() throws TokenOutOfIndexException, UnexpectedTokenException {
        List<ExpressionElement> arguments = new ArrayList<>();

        if (!expectPunct(Code.tkClosedBracket, 0)) {
            arguments.add(parseExpression());

            while (expectPunct(Code.tkComma, 0)) {
                skipToken();

                arguments.add(parseExpression());
            }
        }

        return arguments;
    }

    private ArrayList<SyntaxElement> parseBody() throws UnexpectedTokenException, TokenOutOfIndexException {
        ArrayList<SyntaxElement> body = new ArrayList<>();

        while (!expectKeyword(Code.tkEnd) && !expectKeyword(Code.tkElse)) {
            body.add(parseStatement());
        }

        return body;
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