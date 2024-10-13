package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class UnaryNot implements ExpressionElement {
    private final Span span;
    private final String value;

    public UnaryNot(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public Span getSpan() {
        return span;
    }

    public String getValue() {
        return value;
    }
}
