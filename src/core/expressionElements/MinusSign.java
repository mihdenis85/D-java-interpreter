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

    public static Object evaluate(Object arg1, Object arg2) {
        try {
            int num1 = Integer.parseInt(arg1.toString());
            int num2 = Integer.parseInt(arg2.toString());
            return num2 - num1;
        } catch (Exception e) {
            return Double.parseDouble(arg2.toString()) - Double.parseDouble(arg1.toString());
        }
    }

    @Override
    public String toJSONString() {
        return "\"MinusSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
