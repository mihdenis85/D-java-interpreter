package src.core.syntax;

import java.util.ArrayList;

public class Expression {
    // TODO: Should be not an ArrayList instead should be one of Literals/Punctuation/Operator
    public final ArrayList<?> expressions;

    public Expression(ArrayList<?> expressions) {
        this.expressions = expressions;
    }

    public ArrayList<?> getExpressions() {
        return expressions;
    }
}
