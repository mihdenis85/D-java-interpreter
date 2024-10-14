package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class UnaryMinus implements ExpressionElement {
    public String value;
    public Span span;

    public UnaryMinus(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "{\"UnaryMinus\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
