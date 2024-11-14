package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

import java.util.Objects;

public class PlusSign implements ExpressionElement {
    public String value;
    public Span span;

    public PlusSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static Object evaluate(Object arg1, Object arg2) {
        boolean isStr1 = arg1.toString().charAt(0) == '\'' && arg1.toString().charAt(arg1.toString().length() - 1) == '\'';
        boolean isStr2 = arg2.toString().charAt(0) == '\'' && arg2.toString().charAt(arg2.toString().length() - 1) == '\'';

        try {
            int num1 = Integer.parseInt(arg1.toString());
            int num2 = Integer.parseInt(arg2.toString());
            return num1 + num2;
        } catch (NumberFormatException e) {
            if (isStr1 && isStr2) {
                return arg2.toString().substring(1, arg2.toString().length() - 1) + arg1.toString().substring(1, arg1.toString().length() - 1);
            }

            return Double.parseDouble(arg1.toString()) + Double.parseDouble(arg2.toString());
        }
    }

    @Override
    public String toJSONString() {
        return "\"PlusSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
