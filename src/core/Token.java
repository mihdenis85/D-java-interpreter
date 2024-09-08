package src.core;

public class Token {
    public Span span;

    public Token(Span span) {
        this.span = span;
    }

    public Span getSpan() {
        return span;
    }
}