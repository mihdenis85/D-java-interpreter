package src.core.expressionElements;

import src.core.Span;
import src.core.literals.BooleanLiteral;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;
import src.core.literals.StringLiteral;
import src.core.syntax.interfaces.ExpressionElement;

public class IsOperator implements ExpressionElement {
    public String value;
    public Span span;

    public IsOperator(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public boolean evaluate(String type, ExpressionElement value) {
        switch (type) {
            case "int" -> { return value instanceof IntegerLiteral; }
            case "real" -> { return value instanceof RealLiteral; }
            case "string" -> { return value instanceof StringLiteral; }
            default -> { return false; }
        }
    }

    @Override
    public String toJSONString() {
        return "\"IsOperator\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
