package src.core;

import src.core.enums.Keywords;
import src.core.enums.Punct;
import src.core.exceptions.InvalidIdentifierNameException;
import src.core.exceptions.InvalidSyntaxException;
import src.core.exceptions.TokenOutOfIndexException;
import src.core.exceptions.UnexpectedTokenException;
import src.core.expressionElements.UnaryMinus;
import src.core.expressionElements.UnaryNot;
import src.core.expressionElements.UnaryPlus;
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
            throw new InvalidSyntaxException("Expected keyword `" + code
                    + "`, but got `" + nextToken.type + "` instead"
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
            System.exit(0);
        }

        return new Program(syntaxElements);
    }

    private StatementElement parseStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token peek = peekToken(0);
        return switch (peek.type) {
            case Code.tkIfStatement -> analyzeIfStatementDeclaration();
            case Code.tkReturn -> analyzeReturnStatement();
//            case Code.tkForLoop -> analyzeForLoopDeclaration();
            case Code.tkWhileLoop -> analyzeWhileLoopDeclaration();
            case Code.tkVar -> analyzeDeclarationStatement(peek);
            case Code.tkIdentifier -> analyzeAssignmentStatement();
            case Code.tkPrint -> analyzePrintStatement();
            default -> throw new InvalidSyntaxException("Expected `"
                    + Code.tkIfStatement.name() + "`, `"
                    + Code.tkReturn.name() + "`, `"
                    + Code.tkWhileLoop.name() + "`, `"
                    + Code.tkVar.name() + "` or `"
                    + "`, but got `" + peek.type + "` instead"
                    + "\n\tat line " + peek.span.lineNum + " column " + peek.span.posBegin);
        };
    }

    public StatementElement analyzeAssignmentStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        try {
            Identifier identifier = expectIdentifier();

            matchPunct(Code.tkAssignment);

//            Expression expression = parseExpression();
            skipToken();
            skipToken();
            skipToken();

            matchPunct(Code.tkSemicolon);

//            return new AssignmentStatement(identifier, expression);
            System.out.println("Assignment statement");
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    private StatementElement analyzePrintStatement() {
        try {
            matchPunct(Code.tkPrint);

            Expression expression = parseExpression();

            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private StatementElement analyzeReturnStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token token = peekToken(0);

        matchKeyword(Code.tkReturn);

        Expression returnValue = null;
        if (hasNextToken() && !expectKeyword(Code.tkEnd, 0) && !expectKeyword(Code.tkElse, 0)) {
            returnValue = parseExpression();
        }

        matchPunct(Code.tkSemicolon);

        return new ReturnStatement(returnValue, token.span);
    }

    private SyntaxElement analyzeForLoopDeclaration() {
        try {
            matchKeyword(Code.tkForLoop);

            Identifier ident = expectIdentifier();

            matchKeyword(Code.tkIn);

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

    public WhileLoop analyzeWhileLoopDeclaration() {
        try {
            matchKeyword(Code.tkWhileLoop);

            Expression expression = parseExpression();

            matchKeyword(Code.tkLoop);

            ArrayList<StatementElement> statementBody = parseBody();

            matchKeyword(Code.tkEnd);
            matchPunct(Code.tkSemicolon);
            return new WhileLoop(expression, statementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public IfStatement analyzeIfStatementDeclaration() {
        try {
            matchKeyword(Code.tkIfStatement);

            Expression expression = parseExpression();

            matchKeyword(Code.tkThen);

            ArrayList<StatementElement> statementBody = parseBody();

            ArrayList<StatementElement> elseStatementBody = new ArrayList<>();
            if (expectKeyword(Code.tkElse, 0)) {
                elseStatementBody = parseBody();
            }

            matchKeyword(Code.tkEnd);
            matchPunct(Code.tkSemicolon);

            return new IfStatement(expression, statementBody, elseStatementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    public boolean expectUnaryOperator(Token token) {
        return token.type == Code.tkPlusSign || token.type == Code.tkMinusSign || token.type == Code.tkNot;
    }

    public Expression parseExpression() throws UnexpectedTokenException, TokenOutOfIndexException {
        Token token = peekToken(0);
        ArrayList<ExpressionElement> expressions = new ArrayList<>();

        if (expectUnaryOperator(token)) {
            expressions.add(parseExpressionElement());
        }

        expressions.add(parseExpressionElement());

//        while (expectPunct(Code.tkDot, 0)) {
//            skipToken();
//
//            Code id = getIdentifierOnMatch();
//
//            List<ExpressionElement> arguments = null;
//            if (expectPunct(Code.tkOpenedBracket, 0)) {
//                skipToken();
//
//                arguments = new ArrayList<>();
//                if (!expectPunct(Code.tkClosedBracket, 0)) {
//                    arguments = parseArguments();
//                }
//
//                matchPunct(Code.tkClosedBracket);
//            }
//
//            chain.add(new Expression((src.core.syntax.interfaces.ExpressionElement) arguments));
//        }

        return new Expression(expressions);
    }


    public ExpressionElement parseExpressionElement() throws TokenOutOfIndexException, UnexpectedTokenException {
        try {
            Token token = peekToken(0);
            skipToken();
            return switch(token.type) {
                case Code.tkIntegerLiteral -> new IntegerLiteral(token.span, token.value);
                case Code.tkRealLiteral -> new RealLiteral(token.span, token.value);
                case Code.tkStringLiteral -> new StringLiteral(token.span, token.value);
                case Code.tkBooleanLiteral -> new BooleanLiteral(token.span, token.value);
                case Code.tkIdentifier -> new Identifier(token.value, token.span);
                case Code.tkPlusSign -> new UnaryPlus(token.value, token.span);
                case Code.tkMinusSign -> new UnaryMinus(token.value, token.span);
                case Code.tkNewline -> new UnaryNot(token.value, token.span);
                default -> throw new UnexpectedTokenException(token.span, token.type, null);
            };
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
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

    public Variable analyzeDeclarationStatement(Token token) {
        try {
            System.out.println("Analyzing declaration statement");
            matchKeyword(Code.tkVar);

            Keyword keyword = new Keyword(token.value, token.span);
            Identifier identifier = expectIdentifier();

            if (expectKeyword(Code.tkAssignment, 0)) {
                System.out.println("Analyzing assignment expression");
//                parseExpression();
                skipToken();
                skipToken();
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