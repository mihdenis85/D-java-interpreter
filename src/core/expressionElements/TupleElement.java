package src.core.expressionElements;

import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;

public class TupleElement implements ExpressionElement {
    private final Identifier identifier;
    private final Expression expression;

    public TupleElement(Identifier identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toJSONString() {
        return "{\"TupleElement\": {\n" + identifier.toJSONString() + "," + expression.toJSONString() + "}";
    }
}
