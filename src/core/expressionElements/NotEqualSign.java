package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class NotEqualSign implements ExpressionElement {
    public String value;
    public Span span;

    public NotEqualSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "\"NotEqualSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
