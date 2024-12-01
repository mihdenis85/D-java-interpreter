package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class EmptyType implements ExpressionElement {
    public String value;
    public Span span;

    public EmptyType(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String serializeToJson() {
        return "\"EmptyType\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
