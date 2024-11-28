package src.core.literals;

import src.core.enums.Code;
import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class BooleanLiteral extends Literal implements ExpressionElement {
    private final boolean booleanValue;

    public BooleanLiteral(Span span, String value) {
        super(span, Code.tkBooleanLiteral, value);
        this.booleanValue = Boolean.parseBoolean(value);
    }

    public boolean getValue() {
        return booleanValue;
    }

    @Override
    public String serializeToJson() {
        return "\"BooleanLiteral\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
