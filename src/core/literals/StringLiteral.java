package src.core.literals;

import src.core.enums.Code;
import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class StringLiteral extends Literal implements ExpressionElement {
    public StringLiteral(Span span, String value) {
        super(span, Code.tkStringLiteral, value);
    }

    public String getValue() {
        return this.value;
    }
}
