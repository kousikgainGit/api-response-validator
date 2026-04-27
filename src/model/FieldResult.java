package model;

/**
 * Stores the validation result for a single field in an API response.
 */
public class FieldResult {

    public enum Status { PASS, FAIL, MISSING }

    private String fieldName;
    private String expectedType;
    private String actualValue;
    private Status status;
    private String message;

    public FieldResult(String fieldName, String expectedType,
                       String actualValue, Status status, String message) {
        this.fieldName    = fieldName;
        this.expectedType = expectedType;
        this.actualValue  = actualValue;
        this.status       = status;
        this.message      = message;
    }

    public String    getFieldName()    { return fieldName; }
    public String    getExpectedType() { return expectedType; }
    public String    getActualValue()  { return actualValue; }
    public Status    getStatus()       { return status; }
    public String    getMessage()      { return message; }
}
