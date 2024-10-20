package src.core.literals;

import src.core.enums.Code;
import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class IntegerLiteral extends Literal implements ExpressionElement {
    private final int intValue;

    public IntegerLiteral(Span span, String value) {
        super(span, Code.tkIntegerLiteral, value);
        this.intValue = Integer.parseInt(value);
    }

    public int getValue() {
        return intValue;
    }

    @Override
    public String toJSONString() {
        return "\"IntegerLiteral\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
