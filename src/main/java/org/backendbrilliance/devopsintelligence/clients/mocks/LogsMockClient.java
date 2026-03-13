package org.backendbrilliance.devopsintelligence.clients.mocks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.backendbrilliance.devopsintelligence.models.LogEntry;
import org.springframework.stereotype.Component;

@Component
public class LogsMockClient {

    public List<LogEntry> search(String query, int limitMinutes) {
        Instant now = Instant.now();
        return List.of(
                new LogEntry(
                        now.minus(30, ChronoUnit.MINUTES).toString(),
                        "api-server",
                        "INFO",
                        "Request latency: 45ms",
                        "api-server-1",
                        "production"
                ),
                new LogEntry(
                        now.minus(15, ChronoUnit.MINUTES).toString(),
                        "api-server",
                        "WARN",
                        "Database connection pool at 85% capacity",
                        "api-server-2",
                        "production"
                ),
                new LogEntry(
                        now.minus(10, ChronoUnit.MINUTES).toString(),
                        "problematic-service",
                        "ERROR",
                        "OutOfMemoryError: Java heap space",
                        "problematic-service",
                        "production"
                ),
                new LogEntry(
                        now.minus(9, ChronoUnit.MINUTES).toString(),
                        "problematic-service",
                        "ERROR",
                        "Exception in thread 'main' java.lang.OutOfMemoryError",
                        "problematic-service",
                        "production"
                ),
                new LogEntry(
                        now.minus(5, ChronoUnit.MINUTES).toString(),
                        "worker",
                        "INFO",
                        "Processing batch of 500 jobs",
                        "worker-1",
                        "production"
                ),
                new LogEntry(
                        now.minus(2, ChronoUnit.MINUTES).toString(),
                        "cache",
                        "WARN",
                        "Memory usage high: 89% of available",
                        "cache-1",
                        "production"
                )
        );
    }

    public List<LogEntry> getErrorLogs(int lastHours) {
        return search("level:ERROR", lastHours * 60);
    }
}
