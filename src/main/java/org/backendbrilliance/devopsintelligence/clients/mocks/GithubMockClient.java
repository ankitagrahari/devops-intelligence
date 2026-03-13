package org.backendbrilliance.devopsintelligence.clients.mocks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.backendbrilliance.devopsintelligence.models.Deployment;
import org.springframework.stereotype.Component;

@Component
public class GithubMockClient {

    public List<Deployment> listDeployments(int limit) {
        Instant now = Instant.now();
        return List.of(
                new Deployment(
                        "deploy-1",
                        "2.1.0",
                        "api-server",
                        now.toString(),
                        "alice@company.com",
                        "success",
                        15,
                        "main"
                ),
                new Deployment(
                        "deploy-2",
                        "2.0.9",
                        "worker",
                        now.minus(2, ChronoUnit.HOURS).toString(),
                        "bob@company.com",
                        "success",
                        8,
                        "main"
                ),
                new Deployment(
                        "deploy-3",
                        "1.0.0",
                        "problematic-service",
                        now.minus(30, ChronoUnit.MINUTES).toString(),
                        "charlie@company.com",
                        "in_progress",
                        23,
                        "feature/new-feature"
                ),
                new Deployment(
                        "deploy-4",
                        "2.0.8",
                        "api-server",
                        now.minus(24, ChronoUnit.HOURS).toString(),
                        "alice@company.com",
                        "success",
                        12,
                        "main"
                )
        );
    }
}
