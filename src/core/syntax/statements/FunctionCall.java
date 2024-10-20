package src.core.syntax.statements;

import src.core.syntax.Identifier;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class FunctionCall implements ExpressionElement, StatementElement {
    private final AssignmentIdentifier identifier;
    private final ArrayList<ExpressionElement> arguments;

    public FunctionCall(AssignmentIdentifier identifier, ArrayList<ExpressionElement> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public ArrayList<ExpressionElement> getArguments() {
        return arguments;
    }
    public AssignmentIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toJSONString() {
        return "\"FunctionCall\": {" + identifier.toJSONString() + "," +
                identifier.toJSONString() + "," +
                "Arguments: " + JSONConvertable.listToJsonString(arguments) + "}";
    }
}
