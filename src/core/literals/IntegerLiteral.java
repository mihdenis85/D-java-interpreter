package src.core.literals;

import src.core.Span;

public class IntegerLiteral extends Literal {
    private final int intValue;

    public IntegerLiteral(Span span, String value) {
        super(span, value);
        this.intValue = Integer.parseInt(value);
    }

    public int getValue() {
        return intValue;
    }
}
