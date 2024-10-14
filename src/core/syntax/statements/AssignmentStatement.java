package src.core.syntax.statements;

import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.StatementElement;

public class AssignmentStatement implements StatementElement {
    private final Identifier identifier;
    private final Expression expression;

    public AssignmentStatement(Identifier identifier, Expression expression) {
        this.expression = expression;
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
