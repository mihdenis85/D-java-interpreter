package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;

public class TupleIndex implements ExpressionElement {
    private final int intValue;
    private final Span span;

    public TupleIndex(int value, Span span) {
        this.intValue = value;
        this.span = span;
    }

    public int getValue() {
        return intValue;
    }

    @Override
    public String toJSONString() {
        return "\"TupleIndex\": {\n" + "value: " + intValue + ",\n" + "span: " + span.toString() + "\n}";
    }
}
