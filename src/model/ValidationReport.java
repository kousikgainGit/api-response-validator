package model;

import java.util.List;

/**
 * Holds the complete validation report for one API endpoint call:
 * HTTP status, raw response, and per-field results.
 */
public class ValidationReport {

    private String endpointName;
    private String url;
    private int    httpStatus;
    private String rawResponse;
    private List<FieldResult> fieldResults;
    private long   responseTimeMs;

    public ValidationReport(String endpointName, String url, int httpStatus,
                            String rawResponse, List<FieldResult> fieldResults,
                            long responseTimeMs) {
        this.endpointName   = endpointName;
        this.url            = url;
        this.httpStatus     = httpStatus;
        this.rawResponse    = rawResponse;
        this.fieldResults   = fieldResults;
        this.responseTimeMs = responseTimeMs;
    }

    public String            getEndpointName()   { return endpointName; }
    public String            getUrl()             { return url; }
    public int               getHttpStatus()      { return httpStatus; }
    public String            getRawResponse()     { return rawResponse; }
    public List<FieldResult> getFieldResults()    { return fieldResults; }
    public long              getResponseTimeMs()  { return responseTimeMs; }

    public boolean isOverallPass() {
        return fieldResults.stream()
               .allMatch(r -> r.getStatus() == FieldResult.Status.PASS);
    }

    public long failCount() {
        return fieldResults.stream()
               .filter(r -> r.getStatus() != FieldResult.Status.PASS)
               .count();
    }
}
