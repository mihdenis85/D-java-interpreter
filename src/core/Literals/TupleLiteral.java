package src.core.Literals;

import java.util.HashMap;
import java.util.Map;

public class TupleLiteral<T, K> {
    public Map<T, K> tupleValue;

    public TupleLiteral(Object value) {
        this.tupleValue = new HashMap<>();
    }

    public Map<T, K> getTupleValue() {
        return this.tupleValue;
    }
}