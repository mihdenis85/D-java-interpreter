package src.core.literals;

import src.core.Span;
import src.core.enums.Code;
import src.core.syntax.interfaces.ExpressionElement;

import java.util.HashMap;

public class TupleLiteral extends Literal implements ExpressionElement {
    private final HashMap<ExpressionElement, ExpressionElement> elements;

    public TupleLiteral(Span span, Code type, String value, HashMap<ExpressionElement, ExpressionElement> elements) {
        super(span, type, value);
        this.elements = elements;
    }

    public HashMap<ExpressionElement, ExpressionElement> getElements() {
        return elements;
    }

}
