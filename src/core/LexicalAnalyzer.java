package src.core;

import src.core.literals.BooleanLiteral;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<Token> tokens;
    private final BufferedReader reader;
    private final Span globalSpan;

    public LexicalAnalyzer(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source))) {
            this.reader = reader;
            this.globalSpan = new Span(1, 1, 0);
            this.tokens = new ArrayList<>();
            analyzeText();
        }
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public void analyzeText() throws IOException {
        while (reader.ready()) {
            //noinspection StatementWithEmptyBody
            while (skipComments() || skipWhitespaces()) {
            }
            if (!reader.ready())
                break;

            Token next = nextToken();
            tokens.add(next);

            globalSpan.lineNum = next.span.lineNum;
            globalSpan.posBegin = next.span.posEnd + 1;
            globalSpan.posEnd = globalSpan.posBegin - 1;
        }
    }

    private Token nextToken() throws IOException {
        char symbol = (char) reader.read();

        // Integer or Real Literal
        if (Character.isDigit(symbol))
            return expectNumber(symbol);

        // Boolean Literal, Keyword or Identifier
        if (Character.isLetter(symbol) || symbol == '_')
            return expectWord(symbol);

        // Punct
        return expectPunct(symbol);
    }

    private boolean skipWhitespaces() throws IOException {
        if (!reader.ready()) return false;

        boolean skipped = false;

        do {
            reader.mark(1);
            char symbol = (char) reader.read();

            switch (symbol) {
                case '\r' -> {
                    globalSpan.posBegin = 1;
                    globalSpan.posEnd = 0;
                    skipped = true;
                }
                case ' ', '\t' -> {
                    globalSpan.posBegin++;
                    globalSpan.posEnd = globalSpan.posBegin - 1;
                    skipped = true;
                }
                case '\n' -> {
                    globalSpan.lineNum++;
                    globalSpan.posBegin = 1;
                    globalSpan.posEnd = 0;
                    skipped = true;
                }
                default -> {
                    reader.reset();
                    return skipped;
                }
            }
        } while (reader.ready());

        return skipped;
    }

    private boolean skipComments() throws IOException {
        if (!reader.ready()) return false;

        boolean skipped = false;

        do {
            reader.mark(2);
            char symbol = (char) reader.read();
            if (symbol != '/') {
                reader.reset();
                return skipped;
            }

            if (reader.ready() && reader.read() == '/') {
                reader.readLine();
                globalSpan.lineNum++;
                globalSpan.posBegin = 1;
                globalSpan.posEnd = 0;
                skipped = true;
            } else {
                reader.reset();
                System.exit(0);
            }
        } while (reader.ready());

        return skipped;
    }

    private Token expectNumber(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        boolean real = false;

        reader.mark(2);
        while (reader.ready()) {
            char followup = (char) reader.read();
            if (Character.isDigit(followup)) {
                builder.append(followup);
                reader.mark(2);
            } else if (!real && followup == '.') {
                builder.append(followup);
                real = true;
            } else {
                reader.reset();
                break;
            }
        }

        // For instance, `123.UnaryMinus()`
        if (builder.charAt(builder.length() - 1) == '.') {
            builder.deleteCharAt(builder.length() - 1);
            real = false;
        }

        String word = builder.toString();

        Span span = new Span(globalSpan.lineNum, globalSpan.posBegin, globalSpan.posEnd);
        span.posEnd += word.length();

        if (real) {
            return analyzeWord(word, span);
        } else {
            return analyzeWord(word, span);
        }
    }

    private Token expectWord(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        while (reader.ready()) {
            reader.mark(1);
            char c = (char) reader.read();
            if (isIdentifier(c)) {
                builder.append(c);
            } else {
                reader.reset();
                break;
            }
        }

        String word = builder.toString();
        Span span = new Span(globalSpan.lineNum, globalSpan.posBegin, globalSpan.posEnd);
        span.posEnd += word.length();
        return analyzeWord(word, span);
    }

    private Token expectPunct(char firstSymbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSymbol);

        while (reader.ready()) {
            reader.mark(1);
            char c = (char) reader.read();
            if (!isIdentifier(c) && isPunct(builder.toString())) {
                builder.append(c);
            } else {
                reader.reset();
                break;
            }
        }

        String word = builder.toString();
        Span span = new Span(globalSpan.lineNum, globalSpan.posBegin, globalSpan.posEnd);
        span.posEnd += word.length();
        return analyzeWord(word, span);
    }

    private Token analyzeWord(String string, Span span) {
        Token token;
        if (isInteger(string)) {
            token = new IntegerLiteral(span, string);
        } else if (isFloat(string)) {
            token = new RealLiteral(span, string);
        } else if (isBoolean(string)) {
            token = new BooleanLiteral(span, string);
        } else {
            token = new Token(span, checkWord(string), string);
        }
        return token;
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

    private static boolean isPunct(String wordFromInput) {
        return switch (wordFromInput) {
            case "*", ">", "/", "+", "-", "=", "<", "<=", ">=", "/=", ":=", ",", ";", ":", "[", "]", "{", "}", "=>" ->
                    true;
            default -> false;
        };
    }

    private Code checkWord(String wordFromInput) {
        return switch (wordFromInput) {
            case "var" -> Code.tkVar;
            case "int" -> Code.tkInteger;
            case "real" -> Code.tkReal;
            case "string" -> Code.tkString;
            case "bool" -> Code.tkBoolean;
            case "empty" -> Code.tkEmpty;
            case "*" -> Code.tkMultiplySign;
            case "/" -> Code.tkDivideSign;
            case "+" -> Code.tkPlusSign;
            case "-" -> Code.tkMinusSign;
            case "=" -> Code.tkEqualSign;
            case "<" -> Code.tkLessSign;
            case "<=" -> Code.tkLessEqualSign;
            case ">" -> Code.tkGreaterSign;
            case ">=" -> Code.tkGreaterEqualSign;
            case "/=" -> Code.tkDivideEqualSign;
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
            case "=>" -> Code.tkArrowFunctionSign;
            default -> Code.tkIdentifier;
        };
    }
}
