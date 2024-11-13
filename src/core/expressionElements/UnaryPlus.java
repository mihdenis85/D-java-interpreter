package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class UnaryPlus implements ExpressionElement {
    public String value;
    public Span span;

    public UnaryPlus(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static double evaluate(Object arg1) {
        return Double.parseDouble(arg1.toString()) + 1;
    }

    @Override
    public String toJSONString() {
        return "\"UnaryPlus\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
