package src.core.syntax;

import src.core.syntax.interfaces.ExpressionElement;

public class Expression implements ExpressionElement {

    public final ExpressionElement expression;

    public Expression(ExpressionElement expression) {
        this.expression = expression;
    }

    public ExpressionElement getExpression() {
        return expression;
    }
}
