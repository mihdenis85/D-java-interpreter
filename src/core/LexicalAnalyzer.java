package src.core;

import src.core.enums.Code;
import src.core.literals.BooleanLiteral;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;
import src.core.literals.StringLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<Token> tokens;
    private final BufferedReader reader;
    private final Span currentSpan;

    public LexicalAnalyzer(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source))) {
            this.reader = reader;
            this.currentSpan = new Span(1, 1, 0);
            this.tokens = new ArrayList<>();
            analyze();
        }
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public void analyze() throws IOException {
        while (reader.ready()) {
            //noinspection StatementWithEmptyBody
            while (skipComments() || skipSpaces()) {
            }
            if (!reader.ready())
                break;

            Token next = nextToken();
            tokens.add(next);

            currentSpan.lineNum = next.span.lineNum;
            currentSpan.posBegin = next.span.posEnd + 1;
            currentSpan.posEnd = currentSpan.posBegin - 1;
        }
    }

    private Token nextToken() throws IOException {
        char symbol = (char) reader.read();

        if (Character.isDigit(symbol))
            return expectNumber(symbol);

        if (Character.isLetter(symbol) || symbol == '_')
            return expectWord(symbol);

        if (isQuotationMark(symbol))
            return expectString(symbol);

        return expectPunct(symbol);
    }

    private boolean skipSpaces() throws IOException {
        boolean skipped = false;
        int intChar;
        while (true) {
            reader.mark(1);
            intChar = reader.read();
            if (intChar == -1) {
                break;
            }
            char symbol = (char) intChar;
            if (symbol == ' ' || symbol == '\t') {
                currentSpan.posBegin++;
                currentSpan.posEnd = currentSpan.posBegin - 1;
                skipped = true;
            } else if (symbol == '\n') {
                currentSpan.lineNum++;
                currentSpan.posBegin = 1;
                currentSpan.posEnd = 0;
                skipped = true;
            } else if (symbol == '\r') {
                currentSpan.posBegin = 1;
                currentSpan.posEnd = 0;
                skipped = true;
                reader.mark(1);
                int nextChar = reader.read();
                if (nextChar != -1 && (char) nextChar != '\n') {
                    reader.reset();
                }
            } else {
                reader.reset();
                break;
            }
        }
        return skipped;
    }

    private boolean skipComments() throws IOException {
        boolean skipped = false;
        while (true) {
            reader.mark(2);
            int firstChar = reader.read();
            if (firstChar != '/') {
                reader.reset();
                break;
            }
            int secondChar = reader.read();
            if (secondChar != '/') {
                reader.reset();
                reader.reset();
                break;
            }
            skipped = true;
            int ch;
            while ((ch = reader.read()) != -1 && ch != '\n' && ch != '\r') {}
            if (ch == '\n') {
                currentSpan.lineNum++;
                currentSpan.posBegin = 1;
                currentSpan.posEnd = 0;
            } else if (ch == '\r') {
                currentSpan.posBegin = 1;
                currentSpan.posEnd = 0;
                reader.mark(1);
                int nextCh = reader.read();
                if (nextCh == '\n') {
                    currentSpan.lineNum++;
                } else if (nextCh != -1) {
                    reader.reset();
                }
            }
        }
        return skipped;
    }

    private Token expectNumber(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        boolean hasDecimalPoint = false;

        reader.mark(1);
        while (reader.ready()) {
            int nextChar = reader.read();
            if (nextChar == -1) {
                break;
            }
            char ch = (char) nextChar;

            if (Character.isDigit(ch)) {
                builder.append(ch);
                reader.mark(1);
            } else if (!hasDecimalPoint && ch == '.') {
                hasDecimalPoint = true;
                builder.append(ch);
                reader.mark(1);
            } else {
                reader.reset();
                break;
            }
        }

        if (builder.charAt(builder.length() - 1) == '.') {
            builder.setLength(builder.length() - 1);
        }

        String word = builder.toString();
        Span span = new Span(currentSpan.lineNum, currentSpan.posBegin, currentSpan.posEnd + word.length());

        return startWordAnalysis(word, span);
    }

    private Token expectWord(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        while (reader.ready()) {
            reader.mark(1);
            int nextChar = reader.read();
            if (nextChar == -1) {
                break;
            }
            char c = (char) nextChar;
            if (isIdentifier(c)) {
                builder.append(c);
            } else {
                reader.reset();
                break;
            }
        }

        String word = builder.toString();
        Span span = new Span(currentSpan.lineNum, currentSpan.posBegin, currentSpan.posEnd + word.length());
        return startWordAnalysis(word, span);
    }

    private Token expectPunct(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        while (reader.ready()) {
            reader.mark(1);
            int nextChar = reader.read();
            if (nextChar == -1) {
                break;
            }
            char c = (char) nextChar;
            builder.append(c);
            if (isIdentifier(c) || !isPunctuation(builder.toString())) {
                builder.deleteCharAt(builder.length() - 1);
                reader.reset();
                break;
            } else {
                reader.mark(1);
            }
        }

        String word = builder.toString();
        Span span = new Span(currentSpan.lineNum, currentSpan.posBegin, currentSpan.posEnd + word.length());
        return startWordAnalysis(word, span);
    }

    private Token expectString(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        while (reader.ready()) {
            int nextChar = reader.read();
            if (nextChar == -1) {
                break;
            }
            char c = (char) nextChar;
            builder.append(c);
            if (isQuotationMark(c)) {
                break;
            }
        }

        String word = builder.toString();
        Span span = new Span(currentSpan.lineNum, currentSpan.posBegin, currentSpan.posEnd + word.length());
        return startWordAnalysis(word, span);
    }

    private Token analyzeWord(String string, Span span) {
        Token token;
        if (isInteger(string)) {
            token = new IntegerLiteral(span, string);
        } else if (isFloat(string)) {
            token = new RealLiteral(span, string);
        } else if (isBoolean(string)) {
            token = new BooleanLiteral(span, string);
        } else if (string.contains("'") || string.contains("\"")) {
            token = new StringLiteral(span, string);
        } else {
            token = new Token(span, checkWord(string), string);
        }
        return token;
    }

    private Token startWordAnalysis(String string, Span span) {
        boolean isPayload = false;
        StringBuilder builder = new StringBuilder();
        boolean isString = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!isString && isQuotationMark(c)) {
                isString = true;
            } else if (isString && isQuotationMark(c)) {
                isString = false;
            }
            if ((c == ' ' || c == '\n') && !isString) {
                if (isPayload)
                    span.posBegin++;
                else
                    span.posEnd--;
            } else if (c != '\r') {
                builder.append(c);
                isPayload = true;
            }
        }
        return analyzeWord(builder.toString(), span);
    }

    private boolean isInteger(String wordFromInput) {
        return wordFromInput.matches("-?\\d+");
    }

    private boolean isFloat(String wordFromInput) {
        return wordFromInput.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isBoolean(String wordFromInput) {
        return wordFromInput.equals("true") || wordFromInput.equals("false");
    }

    private static boolean isIdentifier(char symbol) {
        return Character.isLetter(symbol) || Character.isDigit(symbol) || symbol == '_';
    }

    private static boolean isPunctuation(String wordFromInput) {
        return switch (wordFromInput) {
            case "*", ">", "/", "+", "-", "=", "<", "<=", ">=", "/=", ":=", ",", ";", ":", "[", "]", "{", "}", "=>",
                 "(", ")" -> true;
            default -> false;
        };
    }

    private static boolean isQuotationMark(char symbol) {
        return symbol == '\'' || symbol == '"';
    }

    private Code checkWord(String wordFromInput) {
        return switch (wordFromInput) {
            case "var" -> Code.tkVar;
            case "int" -> Code.tkInteger;
            case "real" -> Code.tkReal;
            case "string" -> Code.tkString;
            case "bool" -> Code.tkBoolean;
            case "empty" -> Code.tkEmpty;
            case "." -> Code.tkDot;
            case "*" -> Code.tkMultiplySign;
            case "/" -> Code.tkDivideSign;
            case "+" -> Code.tkPlusSign;
            case "-" -> Code.tkMinusSign;
            case "=" -> Code.tkEqualSign;
            case "<" -> Code.tkLessSign;
            case "<=" -> Code.tkLessEqualSign;
            case ">" -> Code.tkGreaterSign;
            case ">=" -> Code.tkGreaterEqualSign;
            case "/=" -> Code.tkNotEqualSign;
            case "not" -> Code.tkNot;
            case "or" -> Code.tkOr;
            case "and" -> Code.tkAnd;
            case "xor" -> Code.tkXor;
            case "return" -> Code.tkReturn;
            case "print" -> Code.tkPrint;
            case "for" -> Code.tkForLoop;
            case "while" -> Code.tkWhileLoop;
            case "if" -> Code.tkIfStatement;
            case ":=" -> Code.tkAssignment;
            case "func" -> Code.tkFunction;
            case "end" -> Code.tkEnd;
            case "loop" -> Code.tkLoop;
            case "else" -> Code.tkElse;
            case "is" -> Code.tkIs;
            case "\n" -> Code.tkNewline;
            case "," -> Code.tkComma;
            case ";" -> Code.tkSemicolon;
            case ":" -> Code.tkColon;
            case "then" -> Code.tkThen;
            case "in" -> Code.tkIn;
            case "[" -> Code.tkOpenedArrayBracket;
            case "]" -> Code.tkClosedArrayBracket;
            case "{" -> Code.tkOpenedTupleBracket;
            case "}" -> Code.tkClosedTupleBracket;
            case "(" -> Code.tkOpenedBracket;
            case ")" -> Code.tkClosedBracket;
            case "=>" -> Code.tkArrowFunctionSign;
            default -> Code.tkIdentifier;
        };
    }
}
