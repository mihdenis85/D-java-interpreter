package src.core.syntax.statements;

import src.core.syntax.Identifier;
import src.core.syntax.interfaces.JSONSerializable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class ForLoop implements StatementElement {
    private final Identifier identifier;
    private final Identifier array;
    private final ArrayList<StatementElement> body;

    public ForLoop(Identifier identifier, Identifier array, ArrayList<StatementElement> body) {
        this.identifier = identifier;
        this.array = array;
        this.body = body;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
    public Identifier getExpression() {
        return array;
    }
    public ArrayList<StatementElement> getBody() {
        return body;
    }

    @Override
    public String serializeToJson() {
        return "\"ForLoop\": {\n" + identifier.serializeToJson() + "," +
                array.serializeToJson() + "," +
                "body: " + JSONSerializable.serializeListToJson(body) + "}";
    }
}
