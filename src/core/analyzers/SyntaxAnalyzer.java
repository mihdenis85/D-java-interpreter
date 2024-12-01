package src.core.analyzers;

import src.core.token.Token;
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
import src.core.syntax.statements.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.*;

public class SyntaxAnalyzer {
    private final ArrayList<Token> tokensList;
    private int currentTokenIndex;

    public SyntaxAnalyzer(ArrayList<Token> tokensList) {
        this.tokensList = tokensList;
        this.currentTokenIndex = 0;
    }

    private void checkPunctuation(Code code) throws UnexpectedTokenException {
        Token token = getNextToken();
        boolean isValid = code != null && Punct.contains(token.type);
        if (!isValid) {
            throw new UnexpectedTokenException(token.span, token.type, code);
        }
    }

    private void checkKeyword(Code code) {
        Token token = getNextToken();
        boolean isValid = code != null && Keywords.contains(token.type);
        if (!isValid) {
            String message = "Expected keyword `" + code
                    + "`, but got `" + token.type + "` instead"
                    + "\n\tat line " + token.span.lineNum + " column " + token.span.posBegin;
            throw new InvalidSyntaxException(message);
        }
    }

    private boolean expectPunctuation(Code code) throws TokenOutOfIndexException {
        Token token = peekToken(0);
        return Punct.contains(token.type) && code != null && code == token.type;
    }

    public Program analyze() {
        ArrayList<StatementElement> syntaxElements = new ArrayList<>();

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
                identifier = new ArrayIndex(expression, (Identifier) identifier, token.span);

                checkPunctuation(Code.tkClosedArrayBracket);
            }
            if (token.type == Code.tkOpenedBracket) {
                skipToken();

                ArrayList<Expression> arguments = analyzeCallArguments();
                checkPunctuation(Code.tkClosedBracket);
                checkPunctuation(Code.tkSemicolon);
                return new FunctionCall(identifier, arguments);
            }

            checkPunctuation(Code.tkAssignment);

            Expression expression = parseExpression();

            checkPunctuation(Code.tkSemicolon);

            return new AssignmentStatement(identifier, expression);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    private ExpressionElement analyzeFunctionDeclaration() {
        try {
            checkPunctuation(Code.tkOpenedBracket);
            ArrayList<Identifier> arguments = analyzeStatementArguments();
            ArrayList<StatementElement> body = new ArrayList<>();

            checkPunctuation(Code.tkClosedBracket);
            if (expectKeyword(Code.tkIs, 0)) {
                checkKeyword(Code.tkIs);
                body = parseBody();
                checkKeyword(Code.tkEnd);
            }
            if (expectKeyword(Code.tkArrowFunctionSign, 0)) {
                body.add(analyzeArrowFuncReturnStatement());
            }

            return new FunctionStatement(arguments, body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private StatementElement analyzePrintStatement() {
        try {
            checkKeyword(Code.tkPrint);

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

            checkPunctuation(Code.tkSemicolon);
            return new PrintStatement(expressions, token.span);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Token getNextToken() {
        if (currentTokenIndex >= tokensList.size()) {
            return null;
        }
        Token token = tokensList.get(currentTokenIndex);
        currentTokenIndex++;
        return token;
    }

    public boolean isNextToken() {
        return currentTokenIndex < tokensList.size();
    }

    private void skipToken() {
        if (currentTokenIndex < tokensList.size()) {
            currentTokenIndex++;
        }
    }

    private Token peekToken(int ahead) throws TokenOutOfIndexException {
        int index = currentTokenIndex + ahead;
        if (index < tokensList.size()) {
            return tokensList.get(index);
        }
        throw new TokenOutOfIndexException("Could not parse the token");
    }

    private StatementElement analyzeReturnStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token token = peekToken(0);

        checkKeyword(Code.tkReturn);

        Expression returnValue = null;
        if (isNextToken() && !expectKeyword(Code.tkEnd, 0) && !expectKeyword(Code.tkElse, 0)) {
            returnValue = parseExpression();
        }

        checkPunctuation(Code.tkSemicolon);

        return new ReturnStatement(returnValue, token.span);
    }

    private StatementElement analyzeArrowFuncReturnStatement() throws TokenOutOfIndexException, UnexpectedTokenException {
        Token token = peekToken(0);

        checkPunctuation(Code.tkArrowFunctionSign);

        Expression returnValue = null;
        if (isNextToken() && !expectKeyword(Code.tkEnd, 0) && !expectKeyword(Code.tkElse, 0)) {
            returnValue = parseExpression();
        }

        return new ReturnStatement(returnValue, token.span);
    }

    private StatementElement analyzeForLoopDeclaration() {
        try {
            checkKeyword(Code.tkForLoop);

            Identifier ident = expectIdentifier();

            checkKeyword(Code.tkIn);

            Identifier expression = expectIdentifier();

            checkKeyword(Code.tkLoop);

            ArrayList<StatementElement> loopBody = parseBody();

            checkKeyword(Code.tkEnd);
            checkPunctuation(Code.tkSemicolon);

            return new ForLoop(ident, expression, loopBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return null;
    }

    public WhileLoop analyzeWhileLoopDeclaration() {
        try {
            checkKeyword(Code.tkWhileLoop);

            Expression expression = parseExpression();

            checkKeyword(Code.tkLoop);

            ArrayList<StatementElement> statementBody = parseBody();

            checkKeyword(Code.tkEnd);
            checkPunctuation(Code.tkSemicolon);
            return new WhileLoop(expression, statementBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public IfStatement analyzeIfStatementDeclaration() {
        try {
            checkKeyword(Code.tkIfStatement);

            Expression expression = parseExpression();

            checkKeyword(Code.tkThen);

            ArrayList<StatementElement> statementBody = parseBody();

            ArrayList<StatementElement> elseStatementBody = new ArrayList<>();
            while (expectKeyword(Code.tkElse, 0)) {
                skipToken();
                elseStatementBody = parseBody();
            }

            checkKeyword(Code.tkEnd);
            checkPunctuation(Code.tkSemicolon);

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
            expressions.add(analyzeExpressionElement());
        }

        expressions.add(analyzeExpressionElement());

        if (isNextToken()) {
            token = peekToken(0);
            while (!ExpressionStopper.contains(token.type)) {
                expressions.add(analyzeExpressionElement());
                token = peekToken(0);
            }
        }

        return new Expression(expressions);
    }

    public ArrayLiteral analyzeArray() throws TokenOutOfIndexException, UnexpectedTokenException {
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

    public TupleLiteral analyzeTuple() throws TokenOutOfIndexException, InvalidIdentifierNameException, UnexpectedTokenException {
        Token token = peekToken(0);
        TupleLiteral tuple = new TupleLiteral(new ArrayList<>());


        while (token.type != Code.tkClosedTupleBracket) {
            if (token.type == Code.tkComma) {
                skipToken();
                token = peekToken(0);
                continue;
            }
            ArrayList<TupleElement> elements = tuple.getElements();
            Token nextToken = peekToken(1);
            Identifier identifier = new Identifier("", token.span);

            if (token.type == Code.tkIdentifier && !Objects.equals(nextToken.value, ",")) {
                identifier = expectIdentifier();
            }

            if (nextToken.type == Code.tkComma || nextToken.type == Code.tkClosedTupleBracket) {
                Expression expression = parseExpression();
                TupleElement tupleElement = new TupleElement(identifier, expression);
                elements.add(tupleElement);
                tuple.setElements(elements);
                token = peekToken(0);
                continue;
            }


            checkPunctuation(Code.tkAssignment);

            Expression element = parseExpression();
            TupleElement tupleElement = new TupleElement(identifier, element);
            elements.add(tupleElement);
            tuple.setElements(elements);
            token = peekToken(0);
        }

        skipToken();
        return tuple;
    }

    public ExpressionElement analyzeExpressionElement() throws TokenOutOfIndexException, UnexpectedTokenException {
        try {
            Token token = getNextToken();
            return switch (token.type) {
                case Code.tkOpenedArrayBracket -> {
                    Token prevToken = peekToken(-2);
                    if (prevToken.type == Code.tkIdentifier) {
                        Expression expression = parseExpression();
                        checkPunctuation(Code.tkClosedArrayBracket);
                        Identifier identifier = new Identifier(prevToken.value, prevToken.span);
                        yield new ArrayIndex(expression, identifier, token.span);
                    }

                    if (prevToken.type == Code.tkAssignment || prevToken.type == Code.tkComma || prevToken.type == Code.tkOpenedArrayBracket) {
                        yield analyzeArray();
                    }

                    yield parseExpression();
                }
                case Code.tkOpenedTupleBracket -> {
                    Token prevToken = peekToken(-2);
                    if (prevToken.type == Code.tkAssignment || prevToken.type == Code.tkPlusSign) {
                        yield analyzeTuple();
                    }

                    yield parseExpression();
                }
                case Code.tkIntegerLiteral -> {
                    Token nextToken = peekToken(0);
                    Token prevToken = peekToken(-2);
                    if (nextToken.type == Code.tkOpenedBracket && prevToken.type == Code.tkDot) {
                        Identifier identifier = new Identifier(token.value, token.span);
                        skipToken();
                        ArrayList<Expression> arguments = analyzeCallArguments();
                        checkPunctuation(Code.tkClosedBracket);
                        yield new FunctionCall(identifier, arguments);
                    }
                    yield new IntegerLiteral(token.span, token.value);
                }
                case Code.tkRealLiteral -> new RealLiteral(token.span, token.value);
                case Code.tkStringLiteral -> new StringLiteral(token.span, token.value);
                case Code.tkBooleanLiteral -> new BooleanLiteral(token.span, token.value);
                case Code.tkIdentifier -> {
                    Token nextToken = peekToken(0);
                    if (nextToken.type == Code.tkDot) {
                        skipToken();
                        Identifier identifier = new Identifier(token.value, token.span);
                        ExpressionElement attribute = analyzeExpressionElement();
                        if( attribute.getClass() ==  IntegerLiteral.class )
                            attribute = new TupleIndex(((IntegerLiteral) attribute).getValue(), ((IntegerLiteral) attribute).getSpan(), identifier);
                        yield new DotNotation(identifier, attribute, token.span);
                    }
                    if (nextToken.type == Code.tkOpenedBracket) {
                        Identifier identifier = new Identifier(token.value, token.span);
                        skipToken();
                        ArrayList<Expression> arguments = analyzeCallArguments();
                        checkPunctuation(Code.tkClosedBracket);
                        yield new FunctionCall(identifier, arguments);
                    }

                    yield new Identifier(token.value, token.span);
                }
                case Code.tkFunction -> analyzeFunctionDeclaration();
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
                    Token prevToken = peekToken(-2);
                    if (nextToken.type == Code.tkIdentifier && prevToken.type != Code.tkIdentifier) {
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

    private ArrayList<Expression> analyzeCallArguments() throws TokenOutOfIndexException, UnexpectedTokenException {
        ArrayList<Expression> arguments = new ArrayList<>();

        if (!expectPunctuation(Code.tkClosedBracket)) {
            arguments.add(parseExpression());

            while (expectPunctuation(Code.tkComma)) {
                skipToken();

                arguments.add(parseExpression());
            }
        }

        return arguments;
    }
    private ArrayList<Identifier> analyzeStatementArguments() throws TokenOutOfIndexException, UnexpectedTokenException {
        ArrayList<Identifier> arguments = new ArrayList<>();

        if (!expectPunctuation(Code.tkClosedBracket)) {
            Token argument = getNextToken();
            arguments.add(new Identifier(argument.value, argument.span));

            while (expectPunctuation(Code.tkComma)) {
                skipToken();
                argument = getNextToken();
                arguments.add(new Identifier(argument.value, argument.span));
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
            checkKeyword(Code.tkVar);

            Keyword keyword = new Keyword(token.value, token.span);
            Identifier identifier = expectIdentifier();
            token = peekToken(0);
            Expression expression = new Expression(new ArrayList<>());
            expression.expressions.add(new EmptyLiteral(token.span));

            if (expectKeyword(Code.tkAssignment, 0)) {
                skipToken();
                expression = parseExpression();
            }

            checkPunctuation(Code.tkSemicolon);

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