package model;

import java.util.Map;

/**
 * Represents a single API endpoint to be validated.
 * Stores the URL, endpoint name, and the expected schema
 * (field name → expected data type).
 */
public class ApiEndpoint {

    private String name;
    private String url;
    private Map<String, String> expectedSchema; // field -> expected type: STRING, INTEGER, BOOLEAN, OBJECT, ARRAY

    public ApiEndpoint(String name, String url, Map<String, String> expectedSchema) {
        this.name           = name;
        this.url            = url;
        this.expectedSchema = expectedSchema;
    }

    public String getName()                     { return name; }
    public String getUrl()                      { return url; }
    public Map<String, String> getExpectedSchema() { return expectedSchema; }
}
