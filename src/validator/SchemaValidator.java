package validator;

import model.ApiEndpoint;
import model.FieldResult;
import model.FieldResult.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates a raw JSON API response against an expected schema.
 *
 * For each expected field, checks:
 *   1. Field is present in the response
 *   2. Field type matches the expected type
 *   3. Field value is not null or empty
 */
public class SchemaValidator {

    private final SimpleJsonParser parser = new SimpleJsonParser();

    public List<FieldResult> validate(ApiEndpoint endpoint, String jsonResponse) {
        List<FieldResult> results = new ArrayList<>();
        Map<String, String> schema = endpoint.getExpectedSchema();

        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String fieldName     = entry.getKey();
            String expectedType  = entry.getValue();

            // Check 1: Field exists?
            if (!parser.hasField(jsonResponse, fieldName)) {
                results.add(new FieldResult(
                    fieldName, expectedType, "NOT FOUND",
                    Status.MISSING,
                    "MISSING — field '" + fieldName + "' not found in response"
                ));
                continue;
            }

            String actualValue = parser.extractValue(jsonResponse, fieldName);
            String actualType  = parser.detectType(jsonResponse, fieldName);

            // Check 2: Null value?
            if ("NULL".equals(actualType) || actualValue == null) {
                results.add(new FieldResult(
                    fieldName, expectedType, "null",
                    Status.FAIL,
                    "NULL VALUE — field '" + fieldName + "' is null"
                ));
                continue;
            }

            // Check 3: Type mismatch?
            if (!actualType.equalsIgnoreCase(expectedType)) {
                results.add(new FieldResult(
                    fieldName, expectedType, actualValue,
                    Status.FAIL,
                    "TYPE MISMATCH — expected: " + expectedType + ", got: " + actualType
                ));
                continue;
            }

            // Check 4: Empty string?
            if ("STRING".equals(actualType) && actualValue.trim().isEmpty()) {
                results.add(new FieldResult(
                    fieldName, expectedType, "(empty string)",
                    Status.FAIL,
                    "EMPTY VALUE — string field '" + fieldName + "' is blank"
                ));
                continue;
            }

            // All checks passed
            results.add(new FieldResult(
                fieldName, expectedType, actualValue,
                Status.PASS,
                "OK"
            ));
        }

        return results;
    }
}
