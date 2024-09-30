package src.core.literals;

import src.core.Code;
import src.core.Span;

public class StringLiteral extends Literal {
    public StringLiteral(Span span, String value) {
        super(span, Code.tkStringLiteral, value);
    }

    public String getValue() {
        return this.value;
    }
}
