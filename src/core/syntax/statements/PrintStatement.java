package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;

import java.util.Optional;

public class PrintStatement implements StatementElement {
    private final ExpressionElement printValue;
    private final Span span;

    public PrintStatement(ExpressionElement returnValue, Span span) {
        this.printValue = returnValue;
        this.span = span;
    }

    public ExpressionElement getPrintValue() {
        return printValue;
    }

    public Span getSpan() {
        return span;
    }
}
