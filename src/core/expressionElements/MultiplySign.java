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

    public static Object evaluate(Object arg1, Object arg2) {
        try {
            int num1 = Integer.parseInt(arg1.toString());
            int num2 = Integer.parseInt(arg2.toString());
            return num2 * num1;
        } catch (Exception e) {
            return Double.parseDouble(arg1.toString()) * Double.parseDouble(arg2.toString());
        }
    }

    @Override
    public String serializeToJson() {
        return "\"MultiplySign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
