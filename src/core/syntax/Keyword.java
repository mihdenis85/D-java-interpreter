package src.core.syntax;

import src.core.Span;

public class Keyword {
    private final String value;
    private final Span span;


    public Keyword(String value, Span span) {
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
