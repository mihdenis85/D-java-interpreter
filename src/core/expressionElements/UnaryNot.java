package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class UnaryNot implements ExpressionElement {
    public String value;
    public Span span;

    public UnaryNot(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static Object evaluate(Object value) {
        return !Boolean.parseBoolean(value.toString());
    }

    @Override
    public String serializeToJson() {
        return "\"UnaryNot\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
