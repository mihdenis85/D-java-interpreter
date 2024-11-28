package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class ArrayType implements ExpressionElement {
    public String value;
    public Span span;

    public ArrayType(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String serializeToJson() {
        return "\"ArrayType\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
