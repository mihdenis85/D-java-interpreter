package src.core;

import src.core.enums.Keywords;
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
import src.core.syntax.statements.ReturnStatement;
import src.core.syntax.statements.WhileLoop;

import javax.swing.plaf.nimbus.State;
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

    public boolean hasNextToken() {
        return currentTokenIndex < tokensList.size();
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

    private void matchKeyword(Code code) {
        Token nextToken = getNextToken();
        if (!(Keywords.contains(nextToken.type) && code != null)) {
            throw new InvalidSyntaxException("Expected keyword `" + code.name()
                    + "`, but got `" + nextToken + "` instead"
                    + "\n\tat line " + nextToken.span.lineNum + " column " + nextToken.span.posBegin);
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
                syntaxElements.add(parseStatement());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Program(syntaxElements);
    }

    private StatementElement parseStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token peek = peekToken(0);
        return switch (peek.type) {
            case Code.tkIfStatement -> analyzeIfStatementDeclaration(peek);
            case Code.tkReturn -> analyzeReturnStatement();
            case Code.tkWhileLoop -> analyzeWhileLoopDeclaration(peek);
            case Code.tkVar -> analyzeVariableDeclaration(peek);
            case Code.tkIdentifier -> analyzeIdentifierStatement(peek);
            case Code.tkForLoop -> analyzeForLoopDeclaration(peek);
            case Code.tkPrint -> analyzePrintStatement(peek);
            default -> throw new InvalidSyntaxException("Expected `"
                    + Code.tkIfStatement.name() + "`, `"
                    + Code.tkReturn.name() + "`, `"
                    + Code.tkWhileLoop.name() + "`, `"
                    + Code.tkVar.name() + "` or `"
                    + "`, but got `" + peek + "` instead"
                    + "\n\tat line " + peek.span.lineNum + " column " + peek.span.posBegin);
        };
    }

    private StatementElement analyzePrintStatement(Token peek) {
        System.out.println(peek.value);
        skipToken();
        return null;
    }

    private StatementElement analyzeIdentifierStatement(Token peek) {
        return null;
    }

    private StatementElement analyzeReturnStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Span span = peekToken(0).span;

        matchKeyword(Code.tkReturn);

        Expression returnValue = null;
        if (hasNextToken() && !expectKeyword(Code.tkEnd, 0) && !expectKeyword(Code.tkElse, 0)) {
            returnValue = parseExpression();
        }

        return new ReturnStatement(returnValue, span);
    }

    private StatementElement analyzeForLoopDeclaration(Token token) {
        try {
            Identifier ident = expectIdentifier();

            expectKeyword(Code.tkIn, 0);
            Expression expression = parseExpression();

            skipToken();

            expectKeyword(Code.tkLoop, 0);
            ArrayList<StatementElement> loopBody = parseBody();

            skipToken();

            expectKeyword(Code.tkEnd, 0);

            return new ForLoop(ident, expression, loopBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public WhileLoop analyzeWhileLoopDeclaration(Token token) {
        try {
            expectKeyword(Code.tkWhileLoop, 0);

            Expression expression = parseExpression();

            skipToken();

            expectKeyword(Code.tkLoop, 0);

            ArrayList<StatementElement> statementBody = parseBody();

            skipToken();

            expectKeyword(Code.tkEnd, 0);
            return new WhileLoop(expression, statementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public IfStatement analyzeIfStatementDeclaration(Token token) {
        try {
            expectKeyword(Code.tkIfStatement, 0);
            skipToken();

            Expression expression = parseExpression();
            skipToken();

            expectKeyword(Code.tkThen, 0);

            ArrayList<StatementElement> statementBody = parseBody();

            skipToken();

            ArrayList<StatementElement> elseStatementBody = parseBody();

            skipToken();

            expectKeyword(Code.tkEnd, 0);

            return new IfStatement(expression, statementBody, elseStatementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    public Expression parseExpression() throws UnexpectedTokenException, TokenOutOfIndexException {
        ExpressionElement expression = parseExpressionElement();

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

    private ArrayList<StatementElement> parseBody() throws UnexpectedTokenException, TokenOutOfIndexException {
        ArrayList<StatementElement> body = new ArrayList<>();

        while (!expectKeyword(Code.tkEnd, 0) && !expectKeyword(Code.tkElse, 0)) {
            body.add(parseStatement());
        }

        return body;
    }

    public boolean expectKeyword(Code tokenType, int ahead) throws TokenOutOfIndexException {
        if (currentTokenIndex + ahead < tokensList.size()) {
            Token token = peekToken(ahead);
            return token.type == tokenType;
        }

        return false;
    }

    public Variable analyzeVariableDeclaration(Token token) {
        try {
            matchKeyword(Code.tkVar);

            Keyword keyword = new Keyword(token.value, token.span);
            Identifier identifier = expectIdentifier();

            if (expectKeyword(Code.tkAssignment, 0)) {
                parseExpression();
            }

            matchPunct(Code.tkSemicolon);

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
        boolean isKeyword = Keywords.contains(token.type);

        if (!isValid || isKeyword) {
            throw new InvalidIdentifierNameException(token.span);
        }

        return new Identifier(token.value, token.span);
    }
}