package src.core.literals;

import src.core.Span;
import src.core.enums.Code;
import src.core.expressionElements.TupleElement;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.JSONConvertable;

import java.util.ArrayList;
import java.util.HashMap;

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
    public String toJSONString() {
        return "\"TupleLiteral\": {\n" + "value: " + value + ",\n" + "span: " + span.toString() + ",\n" + "elements: " + JSONConvertable.mapToJsonString(elements) + "\n}";
    }
}
