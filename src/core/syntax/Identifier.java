package src.core.syntax;

import src.core.Span;
import src.core.Token;
import src.core.enums.Code;

public class Identifier extends Token {
    private final String value;
    private final Span span;

    public Identifier(String value, Span span) {
        super(span, Code.tkIdentifier, value);
        this.span = span;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Span getSpan() {
        return span;
    }
}
