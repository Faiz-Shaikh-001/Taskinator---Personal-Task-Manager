package JsonParser;

import java.util.Collection;
import java.util.Map;

public class JsonSerializer {
    public static String serialize(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map) {
            return serializeMap((Map<?, ?>) obj);
        }
        if (obj instanceof Collection) {
            return serializeCollection((Collection<?>) obj);
        }
        if (obj.getClass().isArray()) {
            return serializeArray(obj);
        }
        // Fallback fallback for unknown objects (treated as string)
        return "\"" + escapeString(obj.toString()) + "\"";
    }

    private static String serializeMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            // JSON keys must always be valid strings
            sb.append("\"").append(escapeString(String.valueOf(entry.getKey()))).append("\":");
            sb.append(serialize(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String serializeCollection(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(",");
            }
            sb.append(serialize(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String serializeArray(Object array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(serialize(java.lang.reflect.Array.get(array, i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (ch < ' ') { // Control characters
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u").append("0".repeat(4 - hex.length())).append(hex);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
