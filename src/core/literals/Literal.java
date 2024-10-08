package src.core.literals;

import src.core.Code;
import src.core.Span;
import src.core.Token;
import src.core.syntax.ExpressionElement;

public class Literal extends Token implements ExpressionElement {
    public Literal(Span span, Code type, String value) {
        super(span, type, value);
    }
}
