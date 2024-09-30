package src.core.literals;

import src.core.Code;
import src.core.Span;

public class BooleanLiteral extends Literal {
    private final boolean booleanValue;

    public BooleanLiteral(Span span, String value) {
        super(span, Code.tkBooleanLiteral, value);
        this.booleanValue = Boolean.parseBoolean(value);
    }

    public boolean getValue() {
        return booleanValue;
    }
}
