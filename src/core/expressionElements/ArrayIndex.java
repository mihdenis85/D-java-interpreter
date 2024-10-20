package src.core.expressionElements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.interfaces.AssignmentIdentifier;

public class ArrayIndex implements AssignmentIdentifier {
    public final Expression expression;
    public final Span span;

    public ArrayIndex(Expression expression, Span span) {
        this.expression = expression;
        this.span = span;
    }
}
