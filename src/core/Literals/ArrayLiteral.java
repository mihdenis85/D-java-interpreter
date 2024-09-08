package src.core.Literals;

import java.util.ArrayList;

public class ArrayLiteral<T> {
    public ArrayList<T> arrayValue;

    public ArrayLiteral() {
        this.arrayValue = new ArrayList<>();
    }

    public ArrayList<T> getArrayValue() {
        return this.arrayValue;
    }
}
