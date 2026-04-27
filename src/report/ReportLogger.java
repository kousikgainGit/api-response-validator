package report;

import model.FieldResult;
import model.FieldResult.Status;
import model.ValidationReport;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Writes validation results to both console and a log file.
 * Each run creates a timestamped log file in the /logs directory.
 */
public class ReportLogger {

    private final String logDir;
    private PrintWriter fileWriter;
    private String logFilePath;

    public ReportLogger(String logDir) {
        this.logDir = logDir;
        initLogFile();
    }

    private void initLogFile() {
        try {
            new File(logDir).mkdirs();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            logFilePath = logDir + "/validation_" + timestamp + ".log";
            fileWriter  = new PrintWriter(new FileWriter(logFilePath));
        } catch (IOException e) {
            System.out.println("[WARN] Could not create log file: " + e.getMessage());
        }
    }

    public void logAll(List<ValidationReport> reports) {
        String header = buildHeader();
        print(header);

        for (ValidationReport report : reports) {
            logReport(report);
        }

        logSummary(reports);

        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
            print("\n  Log saved to: " + logFilePath);
        }
    }

    private void logReport(ValidationReport report) {
        String div = repeat("-", 72);
        print("\n" + div);
        print("  ENDPOINT : " + report.getEndpointName());
        print("  URL      : " + report.getUrl());
        print("  HTTP     : " + formatHttpStatus(report.getHttpStatus()));
        print("  LATENCY  : " + report.getResponseTimeMs() + "ms");
        print("  RESULT   : " + (report.isOverallPass() ? "✔ ALL PASS" : "✘ " + report.failCount() + " ISSUE(S) FOUND"));
        print(div);

        print(String.format("  %-18s %-12s %-25s %s",
                "FIELD", "EXPECTED", "ACTUAL VALUE", "STATUS"));
        print(repeat("·", 72));

        for (FieldResult r : report.getFieldResults()) {
            String statusIcon = r.getStatus() == Status.PASS    ? "✔ PASS"
                              : r.getStatus() == Status.MISSING ? "⚠ MISSING"
                              : "✘ FAIL";

            String actualDisplay = r.getActualValue() != null
                    ? (r.getActualValue().length() > 22
                       ? r.getActualValue().substring(0, 22) + "…"
                       : r.getActualValue())
                    : "null";

            print(String.format("  %-18s %-12s %-25s %s",
                    r.getFieldName(), r.getExpectedType(), actualDisplay, statusIcon));

            if (r.getStatus() != Status.PASS) {
                print("    → " + r.getMessage());
            }
        }
    }

    private void logSummary(List<ValidationReport> reports) {
        print("\n" + repeat("=", 72));
        print("  VALIDATION RUN SUMMARY");
        print(repeat("=", 72));

        int totalEndpoints  = reports.size();
        long passEndpoints  = reports.stream().filter(ValidationReport::isOverallPass).count();
        long failEndpoints  = totalEndpoints - passEndpoints;
        long totalFields    = reports.stream().mapToLong(r -> r.getFieldResults().size()).sum();
        long passFields     = reports.stream().flatMap(r -> r.getFieldResults().stream())
                                     .filter(f -> f.getStatus() == Status.PASS).count();
        long failFields     = totalFields - passFields;
        double avgLatency   = reports.stream().mapToLong(ValidationReport::getResponseTimeMs)
                                     .average().orElse(0);

        print(String.format("  Endpoints Tested  : %d", totalEndpoints));
        print(String.format("  Endpoints Passed  : %d", passEndpoints));
        print(String.format("  Endpoints Failed  : %d", failEndpoints));
        print(String.format("  Fields Validated  : %d", totalFields));
        print(String.format("  Fields Passed     : %d", passFields));
        print(String.format("  Discrepancies     : %d", failFields));
        print(String.format("  Avg Response Time : %.0fms", avgLatency));
        print(repeat("=", 72));

        if (failFields > 0) {
            print("\n  DISCREPANCY LOG:");
            for (ValidationReport r : reports) {
                for (FieldResult f : r.getFieldResults()) {
                    if (f.getStatus() != Status.PASS) {
                        print("  [" + r.getEndpointName().split("—")[0].trim() + "] "
                              + f.getFieldName() + " → " + f.getMessage());
                    }
                }
            }
        }
        print(repeat("=", 72) + "\n");
    }

    private String buildHeader() {
        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "\n" + repeat("=", 72) + "\n"
             + "     API RESPONSE VALIDATOR — RUN REPORT\n"
             + "     Timestamp : " + ts + "\n"
             + repeat("=", 72);
    }

    private String formatHttpStatus(int code) {
        if (code == 200) return "200 OK";
        if (code == 201) return "201 Created";
        if (code == 404) return "404 Not Found";
        if (code == 500) return "500 Internal Server Error";
        if (code == -1)  return "CONNECTION FAILED";
        return String.valueOf(code);
    }

    private void print(String line) {
        System.out.println(line);
        if (fileWriter != null) fileWriter.println(line);
    }

    private String repeat(String ch, int n) {
        return ch.repeat(n);
    }
}
