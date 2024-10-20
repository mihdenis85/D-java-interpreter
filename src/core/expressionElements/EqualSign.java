package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class EqualSign implements ExpressionElement {
    public String value;
    public Span span;

    public EqualSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "\"EqualSign\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
