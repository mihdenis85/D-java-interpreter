package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;

public class ArrayIndex implements AssignmentIdentifier, ExpressionElement {
    public final Expression expression;
    public final Span span;

    public ArrayIndex(Expression expression, Span span) {
        this.expression = expression;
        this.span = span;
    }

    @Override
    public String toJSONString() {
        return "\"ArrayIndex\": {" + "span: " +
                span.toString() + "," +
                expression.toJSONString() + "}";
    }
}
