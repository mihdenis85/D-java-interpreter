package src.core.expressionElements;

import src.core.Span;
import src.core.literals.BooleanLiteral;
import src.core.syntax.interfaces.ExpressionElement;

public class LogicalOr implements ExpressionElement {
    public String value;
    public Span span;

    public LogicalOr(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(Object arg1, Object arg2) {
        return Double.parseDouble(arg1.toString()) != 0.0 || Double.parseDouble(arg2.toString()) != 0.0;
    }

    @Override
    public String toJSONString() {
        return "\"LogicalOr\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
