package org.backendbrilliance.devopsintelligence.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Node(
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,  // Ready, NotReady
        @JsonProperty("cpu_capacity") String cpuCapacity,
        @JsonProperty("cpu_allocatable") String cpuAllocatable,
        @JsonProperty("cpu_used") String cpuUsed,
        @JsonProperty("memory_capacity") String memoryCapacity,
        @JsonProperty("memory_allocatable") String memoryAllocatable,
        @JsonProperty("memory_used") String memoryUsed,
        @JsonProperty("pod_capacity") int podCapacity,
        @JsonProperty("pod_allocated") int podAllocated
) {}