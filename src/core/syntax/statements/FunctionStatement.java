package src.core.syntax.statements;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;
import java.util.ArrayList;

public class FunctionStatement implements ExpressionElement, StatementElement {
    private final ArrayList<Identifier> arguments;
    private final ArrayList<StatementElement> body;

    public FunctionStatement(ArrayList<Identifier> arguments, ArrayList<StatementElement> body) {
        this.arguments = arguments;
        this.body = body;
    }

    public ArrayList<Identifier> getArguments() {
        return arguments;
    }
    public ArrayList<StatementElement> getBody() {
        return body;
    }

    @Override
    public String toJSONString() {
        return "\"FunctionStatement\": {" + "Arguments: " + JSONConvertable.listToJsonString(arguments) + "," +
                "Body: " + JSONConvertable.listToJsonString(body) + "}";
    }
}
