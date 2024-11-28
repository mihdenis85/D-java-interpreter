package src.core.syntax.interfaces;

import java.util.Iterator;

import java.util.List;

public interface JSONSerializable {
    String serializeToJson();

    static String serializeListToJson(List<? extends JSONSerializable> list) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        Iterator<? extends JSONSerializable> iterator = list.iterator();
        while (iterator.hasNext()) {
            JSONSerializable item = iterator.next();
            jsonBuilder.append(item.serializeToJson());
            if (iterator.hasNext()) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
}
