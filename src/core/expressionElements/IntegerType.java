package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class IntegerType implements ExpressionElement {
    public String value;
    public Span span;

    public IntegerType(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
