package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class LessSign implements ExpressionElement {
    public String value;
    public Span span;

    public LessSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(Object arg1, Object arg2) {
        return Double.parseDouble(arg2.toString()) < Double.parseDouble(arg1.toString());
    }

    @Override
    public String serializeToJson() {
        return "\"LessSign\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
