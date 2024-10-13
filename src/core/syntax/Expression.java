package src.core.syntax;

import src.core.syntax.interfaces.ExpressionElement;

import java.util.ArrayList;

public class Expression implements ExpressionElement {

    public final ArrayList<ExpressionElement> expressions;

    public Expression(ArrayList<ExpressionElement> expressions) {
        this.expressions = expressions;
    }

    public ArrayList<ExpressionElement> getExpressions() {
        return expressions;
    }
}
