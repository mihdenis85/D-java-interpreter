package src.core.syntax.statements;

import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class FunctionCall implements ExpressionElement, StatementElement {
    private final AssignmentIdentifier identifier;
    private final ArrayList<Expression> arguments;

    public FunctionCall(AssignmentIdentifier identifier, ArrayList<Expression> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public ArrayList<Expression> getArguments() {
        return arguments;
    }
    public AssignmentIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toJSONString() {
        return "\"FunctionCall\": {" + identifier.toJSONString() + "," +
                "Arguments: " + JSONConvertable.listToJsonString(arguments) + "}";
    }
}
