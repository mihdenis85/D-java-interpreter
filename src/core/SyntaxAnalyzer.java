package src.core;

import src.core.enums.ExpressionStopper;
import src.core.enums.Keywords;
import src.core.enums.Punct;
import src.core.exceptions.InvalidIdentifierNameException;
import src.core.exceptions.InvalidSyntaxException;
import src.core.exceptions.TokenOutOfIndexException;
import src.core.exceptions.UnexpectedTokenException;
import src.core.expressionElements.*;
import src.core.literals.*;
import src.core.syntax.*;
import src.core.enums.Code;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;
import src.core.syntax.statements.*;

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


    // TODO: Function expression
    // TODO: Empty literal for array literal, when element has no value
    private StatementElement parseStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token peek = peekToken(0);
        return switch (peek.type) {
            case Code.tkIfStatement -> analyzeIfStatementDeclaration();
            case Code.tkReturn -> analyzeReturnStatement();
            case Code.tkForLoop -> analyzeForLoopDeclaration();
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
            AssignmentIdentifier identifier = expectIdentifier();

            Token token = peekToken(0);
            if (token.type == Code.tkOpenedArrayBracket) {
                skipToken();

                Expression expression = parseExpression();
                identifier = new ArrayIndex(expression, token.span);

                matchPunct(Code.tkClosedArrayBracket);
            }

            matchPunct(Code.tkAssignment);

            Expression expression = parseExpression();

            matchPunct(Code.tkSemicolon);

            return new AssignmentStatement(identifier, expression);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    private StatementElement analyzePrintStatement() {
        try {
            matchKeyword(Code.tkPrint);

            Expression expression = parseExpression();

            ArrayList<Expression> expressions = new ArrayList<>();
            expressions.add(expression);
            Token token = peekToken(0);

            while (!ExpressionStopper.contains(token.type) || token.type == Code.tkComma) {
                if (token.type == Code.tkComma) {
                    skipToken();
                    token = peekToken(0);
                    continue;
                }

                expressions.add(parseExpression());
                token = peekToken(0);
            }

            matchPunct(Code.tkSemicolon);
            return new PrintStatement(expressions, token.span);
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

    private StatementElement analyzeForLoopDeclaration() {
        try {
            matchKeyword(Code.tkForLoop);

            Identifier ident = expectIdentifier();

            matchKeyword(Code.tkIn);

            Expression expression = parseExpression();

            matchKeyword(Code.tkLoop);

            ArrayList<StatementElement> loopBody = parseBody();

            matchKeyword(Code.tkEnd);
            matchPunct(Code.tkSemicolon);

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
            while (expectKeyword(Code.tkElse, 0)) {
                skipToken();
                elseStatementBody.add(parseStatement());
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

        token = peekToken(0);
        while (!ExpressionStopper.contains(token.type)) {
            expressions.add(parseExpressionElement());
            token = peekToken(0);
        }

        return new Expression(expressions);
    }

    public ArrayLiteral parseArray() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token token = peekToken(0);
        ArrayLiteral array = new ArrayLiteral(new ArrayList<>(), token.span);
        while (token.type != Code.tkClosedArrayBracket) {
            ArrayList<Expression> elements = array.getElements();
            if (token.type == Code.tkOpenedArrayBracket) {
                elements.add(parseExpression());
                array.setElements(elements);
                token = peekToken(0);
                continue;
            }

            if (token.type == Code.tkComma) {
                skipToken();
                token = peekToken(0);
                continue;
            }

            elements.add(parseExpression());
            array.setElements(elements);
            token = peekToken(0);
        }

        skipToken();
        return array;
    }

    public TupleLiteral parseTuple() throws TokenOutOfIndexException, InvalidIdentifierNameException, UnexpectedTokenException {
        Token token = peekToken(0);
        TupleLiteral tuple = new TupleLiteral(new ArrayList<>());


        while (token.type != Code.tkClosedTupleBracket) {
            if (token.type == Code.tkComma) {
                skipToken();
                token = peekToken(0);
                continue;
            }
            ArrayList<TupleElement> elements = tuple.getElements();

            Identifier identifier = expectIdentifier();

            Token nextToken = peekToken(0);
            if (nextToken.type == Code.tkComma) {
                skipToken();
                ExpressionElement element = new EmptyLiteral(token.span);
                Expression expression = new Expression(new ArrayList<>());
                expression.expressions.add(element);
                TupleElement tupleElement = new TupleElement(identifier, expression);
                elements.add(tupleElement);
                tuple.setElements(elements);
                token = peekToken(0);
                continue;
            }

            matchPunct(Code.tkAssignment);

            Expression element = parseExpression();
            TupleElement tupleElement = new TupleElement(identifier, element);
            elements.add(tupleElement);
            tuple.setElements(elements);
            token = peekToken(0);
        }

        skipToken();
        return tuple;
    }
    public ExpressionElement parseExpressionElement() throws TokenOutOfIndexException, UnexpectedTokenException {
        try {
            Token token = getNextToken();
            return switch(token.type) {
                case Code.tkOpenedArrayBracket -> {
                    Token prevToken = peekToken(-2);
                    if (prevToken.type == Code.tkAssignment || prevToken.type == Code.tkComma || prevToken.type == Code.tkOpenedArrayBracket) {
                        yield parseArray();
                    }

                    yield parseExpression();
                }
                case Code.tkOpenedTupleBracket -> {
                    Token prevToken = peekToken(-2);
                    if (prevToken.type == Code.tkAssignment || prevToken.type == Code.tkPlusSign) {
                        yield parseTuple();
                    }

                    yield parseExpression();
                }
                case Code.tkIntegerLiteral -> new IntegerLiteral(token.span, token.value);
                case Code.tkRealLiteral -> new RealLiteral(token.span, token.value);
                case Code.tkStringLiteral -> new StringLiteral(token.span, token.value);
                case Code.tkBooleanLiteral -> new BooleanLiteral(token.span, token.value);
                case Code.tkIdentifier -> {
                    Token nextToken = peekToken(0);
                    if (nextToken.type == Code.tkDot){
                        skipToken();

                        yield parseExpressionElement();
                    }

                    yield new Identifier(token.value, token.span);
                }
                case Code.tkPlusSign -> {
                    Token nextToken = peekToken(0);
                    Token prevToken = peekToken(-2);
                    if (nextToken.type == Code.tkIdentifier && prevToken.type != Code.tkIdentifier) {
                        yield new UnaryPlus(token.value, token.span);
                    }

                    yield new PlusSign(token.value, token.span);
                }
                case Code.tkMinusSign -> {
                    Token nextToken = peekToken(0);
                    if (nextToken.type == Code.tkIdentifier) {
                        yield new UnaryMinus(token.value, token.span);
                    }

                    yield new MinusSign(token.value, token.span);
                }
                case Code.tkNot -> new UnaryNot(token.value, token.span);
                case Code.tkDivideSign -> new DivideSign(token.value, token.span);
                case Code.tkEqualSign -> new EqualSign(token.value, token.span);
                case Code.tkGreaterEqualSign -> new GreaterEqualSign(token.value, token.span);
                case Code.tkGreaterSign -> new GreaterSign(token.value, token.span);
                case Code.tkIs -> new IsOperator(token.value, token.span);
                case Code.tkLessEqualSign -> new LessEqualSign(token.value, token.span);
                case Code.tkLessSign -> new LessSign(token.value, token.span);
                case Code.tkAnd -> new LogicalAnd(token.value, token.span);
                case Code.tkOr -> new LogicalOr(token.value, token.span);
                case Code.tkXor -> new LogicalXor(token.value, token.span);
                case Code.tkMultiplySign -> new MultiplySign(token.value, token.span);
                case Code.tkNotEqualSign -> new NotEqualSign(token.value, token.span);
                case Code.tkString -> new StringType(token.value, token.span);
                case Code.tkArray -> new ArrayType(token.value, token.span);
                case Code.tkTuple -> new TupleType(token.value, token.span);
                case Code.tkBoolean -> new BooleanType(token.value, token.span);
                case Code.tkReal -> new RealType(token.value, token.span);
                case Code.tkEmpty -> new EmptyType(token.value, token.span);
                case Code.tkInteger -> new IntegerType(token.value, token.span);
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
            matchKeyword(Code.tkVar);

            Keyword keyword = new Keyword(token.value, token.span);
            Identifier identifier = expectIdentifier();
            token = peekToken(0);
            Expression expression = new Expression(new ArrayList<>());
            expression.expressions.add(new EmptyLiteral(token.span));

            if (expectKeyword(Code.tkAssignment, 0)) {
                skipToken();
                expression = parseExpression();
            }

            matchPunct(Code.tkSemicolon);

            return new Variable(keyword, identifier, expression);
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