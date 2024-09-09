package src.core.literals;

import src.core.Code;
import src.core.Span;
import src.core.Token;

public class Literal extends Token {
    public Literal(Span span, String value) {
        super(span, Code.tkLiteral, value);
    }
}
