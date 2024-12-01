package src.core.token;

import src.core.enums.Code;

public class Token {
    public Span span;
    public Code type;
    public String value;

    public Token(Span span, Code type, String value) {
        this.span = span;
        this.type = type;
        this.value = value;
    }

    public Span getSpan() {
        return span;
    }

    public Code getType() {
        return type;
    }
}