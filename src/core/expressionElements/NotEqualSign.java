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

    public boolean evaluate(ExpressionElement arg1, ExpressionElement arg2) {
        return switch (arg1) {
            case IntegerLiteral num1 when arg2 instanceof IntegerLiteral num2 ->
                    (int) num1.getValue() != (int) num2.getValue();
            case IntegerLiteral num1 when arg2 instanceof RealLiteral num2 ->
                    (float) num1.getValue() != (float) num2.getValue();
            case RealLiteral num1 when arg2 instanceof IntegerLiteral num2 ->
                    (float) num1.getValue() != (float) num2.getValue();
            case RealLiteral num1 when arg2 instanceof RealLiteral num2 ->
                    (float) num1.getValue() != (float) num2.getValue();
            case null, default -> throw new Error("Invalid arguments type");
        };
    }

    @Override
    public String toJSONString() {
        return "\"NotEqualSign\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
