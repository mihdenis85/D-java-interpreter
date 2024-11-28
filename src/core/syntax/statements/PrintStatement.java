package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.interfaces.JSONSerializable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class PrintStatement implements StatementElement {
    private final ArrayList<Expression> expressions;
    private final Span span;

    public PrintStatement(ArrayList<Expression> expressions, Span span) {
        this.expressions = expressions;
        this.span = span;
    }

    public ArrayList<Expression> getExpressions() {
        return expressions;
    }

    public Span getSpan() {
        return span;
    }

    @Override
    public String toJSONString() {
        return "\"PrintStatement\": {" + "Expressions: " + JSONSerializable.serializeListToJson(expressions) + "," +
                "Span: " + span.toString() + "}";
    }
}
