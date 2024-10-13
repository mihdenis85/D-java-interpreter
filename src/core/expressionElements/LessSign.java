package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class LessSign implements ExpressionElement {
    public String value;
    public Span span;

    public LessSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
