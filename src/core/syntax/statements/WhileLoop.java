package src.core.syntax.statements;

import src.core.syntax.Expression;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;
import java.util.ArrayList;

public class WhileLoop implements StatementElement {
    private final Expression condition;
    private final ArrayList<StatementElement> statementBody;

    public WhileLoop(Expression condition, ArrayList<StatementElement> statementBody) {
        this.condition = condition;
        this.statementBody = statementBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public ArrayList<StatementElement> getBody() {
        return statementBody;
    }

    @Override
    public String toJSONString() {
        return "{\"WhileLoop\": {" + "Condition: " + condition.toJSONString() + "," +
                "StatementBody: " + JSONConvertable.listToJsonString(statementBody) + "}}";
    }
}
