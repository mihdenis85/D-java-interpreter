package src.core.literals;

import src.core.enums.Code;
import src.core.token.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class RealLiteral extends Literal implements ExpressionElement {
    private final float realValue;

    public RealLiteral(Span span, String value) {
        super(span, Code.tkRealLiteral, value);
        this.realValue = Float.parseFloat(value);
    }

    public float getValue() {
        return realValue;
    }

    @Override
    public String serializeToJson() {
        return "\"RealLiteral\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
