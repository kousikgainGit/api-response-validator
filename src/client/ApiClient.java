package client;

import java.io.*;
import java.net.*;

/**
 * Handles HTTP GET requests to API endpoints.
 * Uses Java's built-in HttpURLConnection — no external libraries needed.
 */
public class ApiClient {

    private static final int TIMEOUT_MS = 5000; // 5 second timeout

    public static class ApiResponse {
        public final int    statusCode;
        public final String body;
        public final long   responseTimeMs;

        public ApiResponse(int statusCode, String body, long responseTimeMs) {
            this.statusCode    = statusCode;
            this.body          = body;
            this.responseTimeMs = responseTimeMs;
        }
    }

    public ApiResponse get(String urlString) {
        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Accept", "application/json");

            int statusCode = conn.getResponseCode();
            long elapsed   = System.currentTimeMillis() - startTime;

            InputStream stream = (statusCode >= 200 && statusCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String body = readStream(stream);
            conn.disconnect();

            return new ApiResponse(statusCode, body, elapsed);

        } catch (SocketTimeoutException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ApiResponse(-1, "{\"error\":\"Request timed out after " + TIMEOUT_MS + "ms\"}", elapsed);

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ApiResponse(-1, "{\"error\":\"" + e.getMessage() + "\"}", elapsed);
        }
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder  sb     = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line.trim());
        return sb.toString();
    }
}
