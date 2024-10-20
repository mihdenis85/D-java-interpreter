package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class UnaryPlus implements ExpressionElement {
    public String value;
    public Span span;

    public UnaryPlus(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
