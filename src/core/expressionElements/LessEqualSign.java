package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class LessEqualSign implements ExpressionElement {
    public String value;
    public Span span;

    public LessEqualSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
