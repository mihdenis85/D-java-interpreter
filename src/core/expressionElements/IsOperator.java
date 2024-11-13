package src.core.expressionElements;

import src.core.Span;
import src.core.literals.BooleanLiteral;
import src.core.literals.IntegerLiteral;
import src.core.literals.RealLiteral;
import src.core.literals.StringLiteral;
import src.core.syntax.interfaces.ExpressionElement;

import java.util.Objects;

public class IsOperator implements ExpressionElement {
    public String value;
    public Span span;

    public IsOperator(String value, Span span) {
        this.value = value;
        this.span = span;
    }

    public static boolean evaluate(Object type, Object value) {
        System.out.println(type + " " + value);
        switch (Objects.toString(type)) {
            case "int" -> {
                try {
                    Integer.parseInt(value.toString(), 10);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            case "real" -> {
                try {
                    Double.parseDouble(value.toString());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            case "boolean" -> {
                return value.toString().equals("true") || value.toString().equals("false");
            }
            case "string" -> {
                return value.toString().charAt(0) == '\'' && value.toString().charAt(value.toString().length() - 1) == '\'';
            }
            default -> { return false; }
        }
    }

    @Override
    public String toJSONString() {
        return "\"IsOperator\": {\n\t" + "value: " + value + ",\n\t" + "span: " + span.toString() + "\n}";
    }
}
