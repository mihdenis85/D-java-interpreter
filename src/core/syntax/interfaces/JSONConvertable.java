package src.core.syntax.interfaces;

import java.util.List;
import java.util.Map;

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

    static String mapToJsonString(Map<? extends JSONConvertable, ? extends JSONConvertable> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (JSONConvertable j : map.keySet()) {
            sb.append(j.toJSONString());
            sb.append(',');
        }
        if (!map.isEmpty())
            sb.deleteCharAt(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }
}
