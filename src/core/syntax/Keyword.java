package src.core.syntax;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class Keyword implements ExpressionElement {
    private final String value;
    private final Span span;


    public Keyword(String value, Span span) {
        this.span = span;
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public Span getSpan() {
        return span;
    }

    @Override
    public String toJSONString() {
        return "\"Keyword\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
