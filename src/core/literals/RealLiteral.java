package src.core.literals;

import src.core.Span;

public class RealLiteral extends Literal {
    private final float realValue;

    public RealLiteral(Span span, String value) {
        super(span, value);
        this.realValue = Float.parseFloat(value);
    }

    public float getValue() {
        return realValue;
    }
}
