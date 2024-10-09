package src.core.literals;

import src.core.enums.Code;
import src.core.Span;
import src.core.Token;
import src.core.syntax.interfaces.ExpressionElement;

import java.util.ArrayList;

public class ArrayLiteral extends Token implements ExpressionElement {
    private final ArrayList<ExpressionElement> elements;

    public ArrayLiteral(Span span, Code type, String value, ArrayList<ExpressionElement> elements) {
        super(span, type, value);
        this.elements = elements;
    }
    public ArrayList<ExpressionElement> getValue() {
        return elements;
    }
}
