package src.core.syntax;

public class Expression implements ExpressionElement{
    // TODO: Should be not an ArrayList instead should be one of Literals/Punctuation/Operator

    public final ExpressionElement expressions;

    public Expression(ExpressionElement expressions) {
        this.expressions = expressions;
    }

    public ExpressionElement getExpressions() {
        return expressions;
    }
}
