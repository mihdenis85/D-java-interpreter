package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import java.util.Optional;

public class ReturnStatement implements StatementElement {
    private final Expression returnValue;
    private final Span span;

    public ReturnStatement(Expression returnValue, Span span) {
        this.returnValue = returnValue;
        this.span = span;
    }

    public Optional<ExpressionElement> getReturnValue() {
        return Optional.ofNullable(returnValue);
    }

    public Expression getExpression() {
        return this.returnValue;
    }
    
    public Span getSpan() {
        return span;
    }

    @Override
    public String serializeToJson() {
        return "\"ReturnStatement\": {\n" + "ReturnValue: " + returnValue.serializeToJson() + ",\n" +
                "Span: " + span.toString() + "\n}";
    }
}