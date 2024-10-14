package src.core.syntax.statements;

import src.core.syntax.Expression;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class IfStatement implements StatementElement {
    private final Expression condition;
    private final ArrayList<StatementElement> statementBody;
    private final ArrayList<StatementElement> elseStatementBody;

    public IfStatement(Expression condition, ArrayList<StatementElement> statementBody, ArrayList<StatementElement> elseStatementBody) {
        this.condition = condition;
        this.statementBody = statementBody;
        this.elseStatementBody = elseStatementBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public ArrayList<StatementElement> getBody() {
        return statementBody;
    }

    public ArrayList<StatementElement> getElseBody() {
        return elseStatementBody;
    }

    @Override
    public String toJSONString() {
        return "{\"IfStatement\": {\n\t" + "Condition: " + condition.toJSONString() + ",\n\t" +
                "statementBody: " + JSONConvertable.listToJsonString(statementBody) + ",\n\t" +
                "elseBody: " + JSONConvertable.listToJsonString(elseStatementBody) + "\n},";
    }
}
