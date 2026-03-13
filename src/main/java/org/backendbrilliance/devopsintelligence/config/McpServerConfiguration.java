package org.backendbrilliance.devopsintelligence.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfiguration {


//    /**
//     * Create MCP server with STDIO transport
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public McpSyncServer mcpServer(ApplicationContext context) {
//
//        McpServer.SyncSpecification<McpServer.SingleSessionSyncSpecification> syncSpecification = McpServer
//                .sync(new StdioServerTransportProvider(new JacksonMcpJsonMapper(new ObjectMapper())))
//                .serverInfo("devops-intelligence", "1.0.0");
//
//        List<McpServerFeatures.SyncToolSpecification> syncToolSpecifications = new ArrayList<>();
//
//        String[] beanNames = context.getBeanDefinitionNames();
//        for(String beanName : beanNames) {
//            Object bean = context.getBean(beanName);
//            for(Method method : bean.getClass().getMethods()) {
//                McpTool mcpTool = method.getAnnotation(McpTool.class);
//                String toolName = mcpTool.name();
//                String toolDesc  = mcpTool.description();
//                System.err.println("[✓] Registering tool: " + toolName);
//                syncToolSpecifications.add(new McpSchema.Tool(
//                        toolName,
//                        toolDesc,
//                        mcpTool.,
//                        ,
//                        mcpTool.annotations(),
//                        )
//
//            }
//        }
//
//        return  .tools()
//                .build();
//    }
}