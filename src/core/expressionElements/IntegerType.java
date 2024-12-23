package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class IntegerType implements ExpressionElement {
    public String value;
    public Span span;

    public IntegerType(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String serializeToJson() {
        return "\"IntegerType\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
