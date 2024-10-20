package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class LogicalXor implements ExpressionElement {
    public String value;
    public Span span;

    public LogicalXor(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
