package src.core.literals;

import src.core.Span;
import src.core.syntax.Expression;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONSerializable;

import java.util.ArrayList;

public class ArrayLiteral implements ExpressionElement {
    private ArrayList<Expression> elements;
    private final Span span;

    public ArrayLiteral(ArrayList<Expression> elements, Span span) {
        this.elements = elements;
        this.span = span;
    }

    public ArrayList<Expression> getElements() {
        return elements;
    }

    public Span getSpan() {
        return span;
    }

    public void setElements(ArrayList<Expression> elements) {
        this.elements = elements;
    }

    public Expression getExpressionByIndex(int index) {
        return elements.get(index + 1);
    }

    @Override
    public String toJSONString() {
        return "\"ArrayLiteral\": {\n" + "elements: " + JSONSerializable.serializeListToJson(elements) + "\n}";
    }
}
