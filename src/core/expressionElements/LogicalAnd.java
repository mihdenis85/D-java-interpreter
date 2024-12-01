package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class LogicalAnd implements ExpressionElement {
    public String value;
    public Span span;

    public LogicalAnd(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(Object arg1, Object arg2) {
        return Boolean.parseBoolean(arg1.toString()) && Boolean.parseBoolean(arg2.toString());
    }

    @Override
    public String serializeToJson() {
        return "\"LogicalAnd\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
