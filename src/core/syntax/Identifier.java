package src.core.syntax;

import src.core.token.Span;
import src.core.token.Token;
import src.core.enums.Code;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;

public class Identifier extends Token implements ExpressionElement, AssignmentIdentifier {
    private final String value;
    private final Span span;

    public Identifier(String value, Span span) {
        super(span, Code.tkIdentifier, value);
        this.span = span;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Span getSpan() {
        return span;
    }

    @Override
    public String serializeToJson() {
        return "\"Identifier\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + "\n}";
    }
}
