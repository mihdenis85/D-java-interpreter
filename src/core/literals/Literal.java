package src.core.literals;

import src.core.enums.Code;
import src.core.Span;
import src.core.Token;
import src.core.syntax.interfaces.ExpressionElement;

public class Literal extends Token implements ExpressionElement {
    public Literal(Span span, Code type, String value) {
        super(span, type, value);
    }

    @Override
    public String serializeToJson() {
        return "\"Literal\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
