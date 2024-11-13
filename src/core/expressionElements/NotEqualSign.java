package src.core.expressionElements;

import src.core.Span;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;
import src.core.syntax.interfaces.ExpressionElement;

public class NotEqualSign implements ExpressionElement {
    public String value;
    public Span span;

    public NotEqualSign(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(Object arg1, Object arg2) {
        return Double.parseDouble(arg1.toString()) != Double.parseDouble(arg2.toString());
    }

    @Override
    public String toJSONString() {
        return "\"NotEqualSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
