package org.backendbrilliance.devopsintelligence.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pod(
        @JsonProperty("name") String name,
        @JsonProperty("namespace") String namespace,
        @JsonProperty("status") String status,  // Running, Pending, CrashLoopBackOff
        @JsonProperty("ready") String ready,    // 1/1, 0/1, etc
        @JsonProperty("restarts") int restarts,
        @JsonProperty("age") String age,
        @JsonProperty("cpu") String cpu,        // "100m", "500m"
        @JsonProperty("memory") String memory,  // "128Mi", "512Mi"
        @JsonProperty("image") String image
) {}
