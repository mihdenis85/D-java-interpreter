package src.core.syntax.statements;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONSerializable;
import src.core.syntax.interfaces.StatementElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FunctionStatement implements ExpressionElement, StatementElement {
    private final ArrayList<Identifier> arguments;
    private final ArrayList<StatementElement> body;
    public final Map<String, Object> variablesInScope;

    public FunctionStatement(ArrayList<Identifier> arguments, ArrayList<StatementElement> body) {
        this.arguments = arguments;
        this.body = body;
        this.variablesInScope = new HashMap<>();
    }

    public ArrayList<Identifier> getArguments() {
        return arguments;
    }
    public ArrayList<StatementElement> getBody() {
        return body;
    }

    @Override
    public String toJSONString() {
        return "\"FunctionStatement\": {" + "Arguments: " + JSONSerializable.serializeListToJson(arguments) + "," +
                "Body: " + JSONSerializable.serializeListToJson(body) + "}";
    }
}
