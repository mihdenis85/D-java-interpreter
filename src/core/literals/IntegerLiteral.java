package src.core.literals;

import src.core.Code;
import src.core.Span;
import src.core.syntax.ExpressionElement;

public class IntegerLiteral extends Literal implements ExpressionElement {
    private final int intValue;

    public IntegerLiteral(Span span, String value) {
        super(span, Code.tkIntegerLiteral, value);
        this.intValue = Integer.parseInt(value);
    }

    public int getValue() {
        return intValue;
    }
}
