package src.core.expressionElements;

import src.core.Span;
import src.core.literals.BooleanLiteral;
import src.core.syntax.interfaces.ExpressionElement;

public class LogicalXor implements ExpressionElement {
    public String value;
    public Span span;

    public LogicalXor(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(double arg1, double arg2) {
        return arg1 != 0.0 ^ arg2 != 0.0;
    }

    @Override
    public String toJSONString() {
        return "\"LogicalXor\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
