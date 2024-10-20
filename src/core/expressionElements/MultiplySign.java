package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class MultiplySign implements ExpressionElement {
    public String value;
    public Span span;

    public MultiplySign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "\"MultiplySign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
