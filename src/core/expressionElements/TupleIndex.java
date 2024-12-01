package src.core.expressionElements;

import src.core.token.Span;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;

public class TupleIndex implements ExpressionElement {
    private Identifier identifier;
    private final int intValue;
    private final Span span;

    public TupleIndex(int value, Span span, Identifier identifier) {
        this.intValue = value;
        this.identifier = identifier;
        this.span = span;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getValue() {
        return intValue;
    }

    @Override
    public String serializeToJson() {
        return "\"TupleIndex\": {\n" + "value: " + intValue + ",\n" + "span: " + span.toString() + "\n}";
    }
}
