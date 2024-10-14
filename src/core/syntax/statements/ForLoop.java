package src.core.syntax.statements;

import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class ForLoop implements StatementElement {
    private final Identifier identifier;
    private final ExpressionElement expression;
    private final ArrayList<StatementElement> body;

    public ForLoop(Identifier identifier, ExpressionElement expression, ArrayList<StatementElement> body) {
        this.identifier = identifier;
        this.expression = expression;
        this.body = body;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
    public ExpressionElement getExpression() {
        return expression;
    }
    public ArrayList<StatementElement> getBody() {
        return body;
    }

    @Override
    public String toJSONString() {
        return "{\"ForLoop\": {\n" + identifier.toJSONString() + "," +
                expression.toJSONString() + "," +
                "body: " + JSONConvertable.listToJsonString(body) + "}}";
    }
}
