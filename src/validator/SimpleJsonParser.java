package validator;

/**
 * Minimal JSON field extractor — no external libraries required.
 *
 * Supports flat JSON objects. Extracts field values and detects basic types.
 * This is intentionally simple: production code would use Jackson or Gson,
 * but this demonstrates understanding of string parsing and type detection.
 */
public class SimpleJsonParser {

    /**
     * Checks if a field key exists in the JSON string.
     */
    public boolean hasField(String json, String key) {
        return json.contains("\"" + key + "\"");
    }

    /**
     * Extracts the raw value (as string) for a given key from a flat JSON object.
     * Returns null if the key is not found.
     */
    public String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex + searchKey.length());
        if (colonIndex == -1) return null;

        // Skip whitespace after colon
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (valueStart >= json.length()) return null;

        char firstChar = json.charAt(valueStart);

        if (firstChar == '"') {
            // String value
            int endQuote = json.indexOf('"', valueStart + 1);
            return endQuote != -1 ? json.substring(valueStart + 1, endQuote) : null;

        } else if (firstChar == '{') {
            return "{...}"; // nested object

        } else if (firstChar == '[') {
            return "[...]"; // array

        } else {
            // number, boolean, null
            int end = valueStart;
            while (end < json.length() && json.charAt(end) != ','
                   && json.charAt(end) != '}' && json.charAt(end) != ' ') {
                end++;
            }
            return json.substring(valueStart, end).trim();
        }
    }

    /**
     * Detects the JSON type of a raw extracted value.
     * Returns: STRING, INTEGER, BOOLEAN, OBJECT, ARRAY, NULL, UNKNOWN
     */
    public String detectType(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return "MISSING";

        int colonIndex = json.indexOf(":", keyIndex + searchKey.length());
        if (colonIndex == -1) return "UNKNOWN";

        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (valueStart >= json.length()) return "UNKNOWN";

        char firstChar = json.charAt(valueStart);

        if (firstChar == '"')  return "STRING";
        if (firstChar == '{')  return "OBJECT";
        if (firstChar == '[')  return "ARRAY";

        String raw = extractValue(json, key);
        if (raw == null)        return "UNKNOWN";
        if (raw.equals("null")) return "NULL";
        if (raw.equals("true") || raw.equals("false")) return "BOOLEAN";

        try {
            Integer.parseInt(raw);
            return "INTEGER";
        } catch (NumberFormatException e1) {
            try {
                Double.parseDouble(raw);
                return "DECIMAL";
            } catch (NumberFormatException e2) {
                return "UNKNOWN";
            }
        }
    }
}
