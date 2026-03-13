package org.backendbrilliance.devopsintelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DevopsIntelligenceApplication {

    public static void main(String[] args) {
        System.out.println("[DEBUG] Starting application...");
        ApplicationContext context = SpringApplication.run(DevopsIntelligenceApplication.class, args);

        System.out.println("[DEBUG] Application started");
        System.out.println("[DEBUG] MCP Server should be listening on STDIO");

        // List all MCP-related beans
        String[] mcpBeanNames = context.getBeanDefinitionNames();
        System.out.println("[DEBUG] Searching for MCP beans...");
        for (String name : mcpBeanNames) {
            if (name.toLowerCase().contains("mcp")) {
                System.out.println("[✓] Found: " + name);
            }
        }

        // List beans with "Tool" in name
        String[] beanNames = context.getBeanDefinitionNames();
        int toolCount = 0;
        for (String name : beanNames) {
            if (name.toLowerCase().contains("tool")) {
                System.out.println("[DEBUG] Found tool bean: " + name);
                toolCount++;
            }
        }
        System.out.println("[DEBUG] Total tool beans found: " + toolCount);

        // Keep process alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
