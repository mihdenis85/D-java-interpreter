package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;
import java.util.Optional;

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
        return "{\"PrintStatement\": {" + "Expressions: " + JSONConvertable.listToJsonString(expressions) + "," +
                "Span: " + span.toString() + "}}";
    }
}
