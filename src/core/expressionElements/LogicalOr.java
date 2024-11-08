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

    public boolean evaluate(ExpressionElement arg1, ExpressionElement arg2) {
        if (arg1 instanceof BooleanLiteral b1 && arg2 instanceof BooleanLiteral b2) {
            return (Boolean) b1.getValue() || (Boolean) b2.getValue();
        }

        throw new Error("Invalid arguments type");
    }

    @Override
    public String toJSONString() {
        return "\"LogicalOr\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
