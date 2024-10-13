package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class IsOperator implements ExpressionElement {
    public String value;
    public Span span;

    public IsOperator(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
