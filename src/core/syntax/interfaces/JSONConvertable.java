package src.core.syntax.interfaces;

import java.util.List;

public interface JSONConvertable {
    String toJSONString();
    static String listToJsonString(List<? extends JSONConvertable> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (JSONConvertable j : list) {
            sb.append(j.toJSONString());
            sb.append(',');
        }
        if (!list.isEmpty())
            sb.deleteCharAt(sb.length() - 1);
        sb.append(']');
        return sb.toString();
    }
}
