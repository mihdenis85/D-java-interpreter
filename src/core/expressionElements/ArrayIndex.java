package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;

public class ArrayIndex implements AssignmentIdentifier, ExpressionElement {
    public final Expression expression;
    public final Identifier identifier;
    public final Span span;

    public ArrayIndex(Expression expression, Identifier identifier, Span span) {
        this.identifier = identifier;
        this.expression = expression;
        this.span = span;
    }

    @Override
    public String serializeToJson() {
        return "\"ArrayIndex\": {" + "span: " +
                span.toString() + "," +
                expression.serializeToJson() + "," +
                identifier.serializeToJson() + "}";
    }
}
