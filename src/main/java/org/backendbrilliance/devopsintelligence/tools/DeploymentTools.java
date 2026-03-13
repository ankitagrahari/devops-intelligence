package org.backendbrilliance.devopsintelligence.tools;

import org.backendbrilliance.devopsintelligence.clients.mocks.GithubMockClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class DeploymentTools {

    private final GithubMockClient githubClient;

    public DeploymentTools(GithubMockClient githubClient) {
        this.githubClient = githubClient;
    }

    @McpTool(
            name = "deployment_history",
            description = "Get recent deployment history"
    )
    public String getDeploymentHistory(
            @McpToolParam(description = "Number of recent deployments", required = false)
            Integer limit) {
        int deploymentLimit = limit != null ? limit : 10;
        var deployments = githubClient.listDeployments(deploymentLimit);

        StringBuilder sb = new StringBuilder("Recent Deployments:\n");
        deployments.forEach(d ->
                sb.append(String.format(
                        "  %s - %s v%s by %s (%s) - %d changes\n",
                        d.deployedAt(), d.service(), d.version(), d.deployedBy(),
                        d.status(), d.changes()
                ))
        );

        return sb.toString();
    }
}
