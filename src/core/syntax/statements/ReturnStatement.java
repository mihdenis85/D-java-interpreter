package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;
import java.util.Optional;

public class ReturnStatement implements StatementElement {
    private final ExpressionElement returnValue;
    private final Span span;

    public ReturnStatement(ExpressionElement returnValue, Span span) {
        this.returnValue = returnValue;
        this.span = span;
    }

    public Optional<ExpressionElement> getReturnValue() {
        return Optional.ofNullable(returnValue);
    }

    public Span getSpan() {
        return span;
    }

    @Override
    public String toJSONString() {
        return "{\"ReturnStatement\": {\n" + "ReturnValue: " + returnValue.toJSONString() + ",\n" +
                "Span: " + span.toString() + "\n}";
    }
}