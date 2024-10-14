package src.core.syntax.statements;

import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

public class AssignmentStatement implements StatementElement, JSONConvertable {
    private final AssignmentIdentifier identifier;
    private final Expression expression;

    public AssignmentStatement(AssignmentIdentifier identifier, Expression expression) {
        this.expression = expression;
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public AssignmentIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toJSONString() {
        return "{\"AssignmentStatement\": {" + identifier.toJSONString() + "," +
                expression.toJSONString() + "}";
    }
}
