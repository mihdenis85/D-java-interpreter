package src.core.literals;

import src.core.Code;
import src.core.Span;
import src.core.syntax.ExpressionElement;

public class RealLiteral extends Literal implements ExpressionElement {
    private final float realValue;

    public RealLiteral(Span span, String value) {
        super(span, Code.tkRealLiteral, value);
        this.realValue = Float.parseFloat(value);
    }

    public float getValue() {
        return realValue;
    }
}
