package src.core.syntax.statements;

import src.core.Span;
import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;

public class DotNotation implements StatementElement, ExpressionElement {
    private final Identifier identifier;
    private final ExpressionElement attribute;
    private final Span span;

    public DotNotation(Identifier identifier, ExpressionElement attribute, Span span) {
        this.identifier = identifier;
        this.attribute = attribute;
        this.span = span;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public ExpressionElement getAttribute() {
        return attribute;
    }

    public Span getSpan() {
        return span;
    }

    @Override
    public String serializeToJson() {
        return "\"DotNotation\": {"  + identifier.serializeToJson() + ",\n" +
                "Attribute: " + attribute.serializeToJson() + ",\n" +
                "Span: " + span.toString() + "\n}";
    }
}
