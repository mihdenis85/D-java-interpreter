package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class RealType implements ExpressionElement {
    public String value;
    public Span span;

    public RealType(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String serializeToJson() {
        return "\"RealType\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
