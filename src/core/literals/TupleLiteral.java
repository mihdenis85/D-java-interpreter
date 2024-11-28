package src.core.literals;

import src.core.expressionElements.TupleElement;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONSerializable;

import java.util.ArrayList;

public class TupleLiteral implements ExpressionElement {
    private ArrayList<TupleElement> elements;

    public TupleLiteral(ArrayList<TupleElement> elements) {
        this.elements = elements;
    }

    public ArrayList<TupleElement> getElements() {
        return elements;
    }

    public void setElements(ArrayList<TupleElement> elements) {
        this.elements = elements;
    }

    @Override
    public String serializeToJson() {
        return "\"TupleLiteral\": {" + "elements: " + JSONSerializable.serializeListToJson(elements) + "}";
    }
}
