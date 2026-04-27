# API Response Validator Tool
### A Core Java tool that hits REST APIs, validates response schemas, and logs discrepancies

---

## Overview

A Java-based API testing and validation tool that makes live HTTP GET requests to configured REST API endpoints, validates each JSON response against a predefined schema (field presence + data type checking), and generates a structured report — logged to both console and a timestamped log file.

Built to simulate real-world API support workflows: hitting an endpoint, checking if the response looks correct, and documenting any issues found — exactly what a support or integration engineer does when debugging client-reported defects.

---

## How It Works

```
  [ EndpointConfig ]  →  defines endpoints + expected schemas
          ↓
  [ ApiClient ]       →  makes live HTTP GET requests (Java HttpURLConnection)
          ↓
  [ SchemaValidator ] →  checks each field:
                            • Field present?
                            • Type correct? (STRING / INTEGER / BOOLEAN / OBJECT / ARRAY)
                            • Value null or empty?
          ↓
  [ ReportLogger ]    →  prints formatted report to console
                          + saves timestamped .log file to /logs folder
```

---

## Project Structure

```
ApiResponseValidator/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── client/
│   │   └── ApiClient.java                 ← HTTP GET handler (no external libs)
│   ├── config/
│   │   └── EndpointConfig.java            ← All endpoints + expected schemas
│   ├── model/
│   │   ├── ApiEndpoint.java               ← Endpoint data model
│   │   ├── FieldResult.java               ← Per-field validation result
│   │   └── ValidationReport.java          ← Full report for one endpoint
│   ├── validator/
│   │   ├── SchemaValidator.java           ← Core validation logic
│   │   └── SimpleJsonParser.java          ← Lightweight JSON field extractor
│   └── report/
│       └── ReportLogger.java              ← Console + file logging
└── logs/
    └── validation_YYYY-MM-DD_HH-mm-ss.log ← Auto-generated per run
```

---

## OOP Concepts Used

| Concept | Where Applied |
|---|---|
| **Encapsulation** | All model classes use private fields + getters |
| **Separation of Concerns** | Client / Validator / Logger are independent layers |
| **Collections** | `ArrayList`, `LinkedHashMap` for schema and results |
| **Exception Handling** | `try-catch` for network errors, timeouts, file I/O |
| **File I/O** | `PrintWriter`, `FileWriter` for log file generation |
| **Java Stream API** | `stream().filter().count()` for report summary stats |
| **LocalDateTime API** | Timestamped log file names and report headers |
| **Enum** | `FieldResult.Status` — PASS / FAIL / MISSING |

---

## Validation Checks Performed

For every field defined in the schema, the tool checks:

| Check | What it catches |
|---|---|
| **Field Presence** | Field missing entirely from response |
| **Type Match** | e.g. `id` expected as `INTEGER` but came as `STRING` |
| **Null Value** | Field exists but value is `null` |
| **Empty String** | String field exists but is `""` |

---

## APIs Used

Uses **JSONPlaceholder** — a free, public REST API for testing. No authentication or API key required.

| Endpoint | URL |
|---|---|
| Single User Profile | `https://jsonplaceholder.typicode.com/users/1` |
| Blog Post | `https://jsonplaceholder.typicode.com/posts/1` |
| To-Do Item | `https://jsonplaceholder.typicode.com/todos/1` |
| Comment (with deliberate missing field test) | `https://jsonplaceholder.typicode.com/comments/1` |

---

## Sample Output

```
========================================================================
     API RESPONSE VALIDATOR — RUN REPORT
     Timestamp : 2025-03-10 14:32:07
========================================================================

------------------------------------------------------------------------
  ENDPOINT : GET /users/1 — Single User Profile
  URL      : https://jsonplaceholder.typicode.com/users/1
  HTTP     : 200 OK
  LATENCY  : 312ms
  RESULT   : ✔ ALL PASS
------------------------------------------------------------------------
  FIELD              EXPECTED     ACTUAL VALUE              STATUS
  ·······················································
  id                 INTEGER      1                         ✔ PASS
  name               STRING       Leanne Graham             ✔ PASS
  username           STRING       Bret                      ✔ PASS
  email              STRING       Sincere@april.biz         ✔ PASS
  phone              STRING       1-770-736-8031 x56442     ✔ PASS
  website            STRING       hildegard.org             ✔ PASS
  address            OBJECT       {...}                     ✔ PASS
  company            OBJECT       {...}                     ✔ PASS

------------------------------------------------------------------------
  ENDPOINT : GET /comments/1 — Comment (with deliberate MISSING field test)
  URL      : https://jsonplaceholder.typicode.com/comments/1
  HTTP     : 200 OK
  LATENCY  : 289ms
  RESULT   : ✘ 1 ISSUE(S) FOUND
------------------------------------------------------------------------
  FIELD              EXPECTED     ACTUAL VALUE              STATUS
  ·······················································
  postId             INTEGER      1                         ✔ PASS
  id                 INTEGER      1                         ✔ PASS
  name               STRING       id labore ex et…          ✔ PASS
  email              STRING       Eliseo@gardner.biz        ✔ PASS
  body               STRING       laudantium enim…          ✔ PASS
  rating             INTEGER      NOT FOUND                 ⚠ MISSING
    → MISSING — field 'rating' not found in response

========================================================================
  VALIDATION RUN SUMMARY
========================================================================
  Endpoints Tested  : 4
  Endpoints Passed  : 3
  Endpoints Failed  : 1
  Fields Validated  : 25
  Fields Passed     : 24
  Discrepancies     : 1
  Avg Response Time : 298ms

  DISCREPANCY LOG:
  [GET /comments/1] rating → MISSING — field 'rating' not found in response
========================================================================

  Log saved to: logs/validation_2025-03-10_14-32-07.log
```

---

## How to Run

### Prerequisites
- Java JDK 8 or above
- Active internet connection (hits live public APIs)
- Verify Java: `java -version`
- Download from: [oracle.com/java](https://www.oracle.com/java/technologies/downloads/)

### Step 1 — Compile

Open terminal inside the `ApiResponseValidator` folder:

```bash
mkdir out

javac -d out src/model/ApiEndpoint.java src/model/FieldResult.java src/model/ValidationReport.java src/client/ApiClient.java src/validator/SimpleJsonParser.java src/validator/SchemaValidator.java src/config/EndpointConfig.java src/report/ReportLogger.java src/Main.java
```

### Step 2 — Run

```bash
java -cp out Main
```

Log file is automatically saved in the `logs/` folder with a timestamp.

---

## How to Add a New Endpoint

Open `src/config/EndpointConfig.java` and add a new block:

```java
Map<String, String> mySchema = new LinkedHashMap<>();
mySchema.put("id",    "INTEGER");
mySchema.put("title", "STRING");
mySchema.put("done",  "BOOLEAN");

endpoints.add(new ApiEndpoint(
    "GET /my-endpoint — Description",
    "https://your-api-url.com/endpoint",
    mySchema
));
```

No other files need to change.

---

## Supported Field Types

| Type keyword | Matches JSON |
|---|---|
| `STRING` | `"value"` |
| `INTEGER` | `123` |
| `DECIMAL` | `12.5` |
| `BOOLEAN` | `true` / `false` |
| `OBJECT` | `{ ... }` |
| `ARRAY` | `[ ... ]` |

---

## Tech Stack

- **Language:** Core Java (JDK 8+)
- **HTTP:** `java.net.HttpURLConnection` (no external libraries)
- **JSON Parsing:** Custom lightweight parser (no Gson/Jackson dependency)
- **Logging:** `java.io.FileWriter` + `PrintWriter`
- **Concepts:** OOP, Enums, Streams, Exception Handling, File I/O, Collections
- **Test API:** JSONPlaceholder (free public REST API)
