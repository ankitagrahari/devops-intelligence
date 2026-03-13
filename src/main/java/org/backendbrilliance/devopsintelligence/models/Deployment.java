package org.backendbrilliance.devopsintelligence.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Deployment(
        @JsonProperty("id") String id,
        @JsonProperty("version") String version,
        @JsonProperty("service") String service,
        @JsonProperty("deployed_at") String deployedAt,
        @JsonProperty("deployed_by") String deployedBy,
        @JsonProperty("status") String status,  // success, in_progress, failed
        @JsonProperty("changes") int changes,   // Number of changed files
        @JsonProperty("branch") String branch
) {}