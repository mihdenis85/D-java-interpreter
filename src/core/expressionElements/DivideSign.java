package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class DivideSign implements ExpressionElement {
    public String value;
    public Span span;

    public DivideSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static double evaluate(Object arg1, Object arg2) {
        if (Double.parseDouble(arg1.toString()) != 0.0) {
            return Double.parseDouble(arg2.toString()) / Double.parseDouble(arg1.toString());
        }

        throw new Error("Division by 0");
    }

    @Override
    public String serializeToJson() {
        return "\"DivideSign\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
