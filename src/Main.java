import client.ApiClient;
import config.EndpointConfig;
import model.ApiEndpoint;
import model.FieldResult;
import model.ValidationReport;
import report.ReportLogger;
import validator.SchemaValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * API Response Validator Tool
 * ─────────────────────────────────────────────────────────────────────────
 * Hits configured REST API endpoints, validates each response against an
 * expected schema (field presence + type checking), logs discrepancies,
 * and generates a full validation summary report to console + log file.
 *
 * Usage:
 *   java -cp out Main
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("\n  Initializing API Response Validator...");

        List<ApiEndpoint>     endpoints  = EndpointConfig.getEndpoints();
        ApiClient             client     = new ApiClient();
        SchemaValidator       validator  = new SchemaValidator();
        List<ValidationReport> reports   = new ArrayList<>();

        System.out.println("  " + endpoints.size() + " endpoint(s) loaded. Starting validation...");

        for (ApiEndpoint endpoint : endpoints) {
            System.out.println("\n  → Calling: " + endpoint.getUrl());

            // Step 1: Hit the API
            ApiClient.ApiResponse response = client.get(endpoint.getUrl());

            // Step 2: Validate schema
            List<FieldResult> fieldResults = new ArrayList<>();

            if (response.statusCode == -1) {
                // Connection failure — mark all fields as failed
                for (String field : endpoint.getExpectedSchema().keySet()) {
                    fieldResults.add(new FieldResult(
                        field,
                        endpoint.getExpectedSchema().get(field),
                        "N/A",
                        FieldResult.Status.FAIL,
                        "CONNECTION FAILED — could not reach endpoint"
                    ));
                }
            } else {
                fieldResults = validator.validate(endpoint, response.body);
            }

            // Step 3: Build report
            reports.add(new ValidationReport(
                endpoint.getName(),
                endpoint.getUrl(),
                response.statusCode,
                response.body,
                fieldResults,
                response.responseTimeMs
            ));
        }

        // Step 4: Log everything
        ReportLogger logger = new ReportLogger("logs");
        logger.logAll(reports);
    }
}
