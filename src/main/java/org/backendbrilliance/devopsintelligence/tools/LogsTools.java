package org.backendbrilliance.devopsintelligence.tools;

import org.backendbrilliance.devopsintelligence.clients.mocks.LogsMockClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class LogsTools {

    private final LogsMockClient logsClient;

    public LogsTools(LogsMockClient logsClient) {
        this.logsClient = logsClient;
    }

    @McpTool(
            name = "logs_search",
            description = "Search application logs for specific patterns or services"
    )
    public String searchLogs(
            @McpToolParam(description = "Service or pattern to search", required = true)
            String query,

            @McpToolParam(description = "Time range in minutes", required = false)
            Integer minutes) {
        int timeRange = minutes != null ? minutes : 60;
        var logs = logsClient.search(query, timeRange);

        StringBuilder sb = new StringBuilder(String.format("Logs (last %d minutes):\n", timeRange));
        logs.forEach(l ->
                sb.append(String.format("  [%s] %s/%s: %s - %s\n",
                        l.level(), l.namespace(), l.service(), l.message(), l.timestamp()))
        );

        return sb.toString();
    }

    @McpTool(
            name = "logs_errors",
            description = "Get recent error logs"
    )
    public String getErrorLogs(
            @McpToolParam(description = "Time range in hours", required = false)
            Integer hours) {
        int timeRange = hours != null ? hours : 2;
        var logs = logsClient.getErrorLogs(timeRange);

        StringBuilder sb = new StringBuilder(String.format("Error Logs (last %d hours):\n", timeRange));
        logs.forEach(l ->
                sb.append(String.format("  [%s] %s: %s\n", l.timestamp(), l.service(), l.message()))
        );

        return sb.toString();
    }
}
