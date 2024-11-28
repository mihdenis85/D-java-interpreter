package src.core.literals;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class EmptyLiteral implements ExpressionElement {
    private final Integer value;
    private final Span span;

    public EmptyLiteral(Span span) {
        this.value = null;
        this.span = span;
    }

    public Span getSpan() {
        return span;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String serializeToJson() {
        return "\"EmptyLiteral\": {\n" + "value: " + "null" + ",\n" + "span: " + span.toString() + "\n}";
    }
}
