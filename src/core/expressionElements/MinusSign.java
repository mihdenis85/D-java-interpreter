package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class MinusSign implements ExpressionElement {
    public String value;
    public Span span;

    public MinusSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "\"MinusSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
