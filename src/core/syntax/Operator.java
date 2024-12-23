package src.core.syntax;

import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONSerializable;
import src.core.syntax.interfaces.SyntaxElement;

import java.util.ArrayList;

public class Operator implements ExpressionElement, SyntaxElement {
    private final Keyword keyword;
    private final Identifier identifier;
    private final ArrayList<Expression> expressions;

    public Operator(Keyword keyword, Identifier identifier, ArrayList<Expression> expressions) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.expressions = expressions;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public ArrayList<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String serializeToJson() {
        return "\"Operator\": {\n" + "Keyword: " + keyword.serializeToJson() + ",\n" +
                "Identifier: " + identifier.serializeToJson() + ",\n" +
                "Expressions: " + JSONSerializable.serializeListToJson(expressions) + "\n}";
    }
}
