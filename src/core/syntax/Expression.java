package src.core.syntax;

public class Expression implements ExpressionElement{
    // TODO: Should be not an ArrayList instead should be one of Literals/Punctuation/Operator

    public final ExpressionElement expression;

    public Expression(ExpressionElement expression) {
        this.expression = expression;
    }

    public ExpressionElement getExpression() {
        return expression;
    }
}
