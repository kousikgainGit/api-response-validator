package config;

import model.ApiEndpoint;

import java.util.*;

/**
 * Central registry of all API endpoints to be validated.
 * Each endpoint has a name, URL, and a schema map (field → expected type).
 *
 * Uses JSONPlaceholder — a free, public fake REST API for testing.
 * No authentication or API key required.
 * Docs: https://jsonplaceholder.typicode.com
 */
public class EndpointConfig {

    public static List<ApiEndpoint> getEndpoints() {
        List<ApiEndpoint> endpoints = new ArrayList<>();

        // ── Endpoint 1: Single User ──────────────────────────────────────
        Map<String, String> userSchema = new LinkedHashMap<>();
        userSchema.put("id",       "INTEGER");
        userSchema.put("name",     "STRING");
        userSchema.put("username", "STRING");
        userSchema.put("email",    "STRING");
        userSchema.put("phone",    "STRING");
        userSchema.put("website",  "STRING");
        userSchema.put("address",  "OBJECT");
        userSchema.put("company",  "OBJECT");

        endpoints.add(new ApiEndpoint(
            "GET /users/1 — Single User Profile",
            "https://jsonplaceholder.typicode.com/users/1",
            userSchema
        ));

        // ── Endpoint 2: Single Post ──────────────────────────────────────
        Map<String, String> postSchema = new LinkedHashMap<>();
        postSchema.put("userId", "INTEGER");
        postSchema.put("id",     "INTEGER");
        postSchema.put("title",  "STRING");
        postSchema.put("body",   "STRING");

        endpoints.add(new ApiEndpoint(
            "GET /posts/1 — Single Blog Post",
            "https://jsonplaceholder.typicode.com/posts/1",
            postSchema
        ));

        // ── Endpoint 3: To-Do Item ───────────────────────────────────────
        Map<String, String> todoSchema = new LinkedHashMap<>();
        todoSchema.put("userId",    "INTEGER");
        todoSchema.put("id",        "INTEGER");
        todoSchema.put("title",     "STRING");
        todoSchema.put("completed", "BOOLEAN");

        endpoints.add(new ApiEndpoint(
            "GET /todos/1 — To-Do Item",
            "https://jsonplaceholder.typicode.com/todos/1",
            todoSchema
        ));

        // ── Endpoint 4: Comment ──────────────────────────────────────────
        Map<String, String> commentSchema = new LinkedHashMap<>();
        commentSchema.put("postId", "INTEGER");
        commentSchema.put("id",     "INTEGER");
        commentSchema.put("name",   "STRING");
        commentSchema.put("email",  "STRING");
        commentSchema.put("body",   "STRING");
        commentSchema.put("rating", "INTEGER"); // ← intentional: field doesn't exist → MISSING test

        endpoints.add(new ApiEndpoint(
            "GET /comments/1 — Comment (with deliberate MISSING field test)",
            "https://jsonplaceholder.typicode.com/comments/1",
            commentSchema
        ));

        return endpoints;
    }
}
