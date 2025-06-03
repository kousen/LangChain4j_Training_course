package com.kousenit.langchain4j;

import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Lab 6.5: MCP (Model Context Protocol) Integration
 * <p>
 * This lab demonstrates how to integrate external tools via the Model Context Protocol (MCP).
 * You'll learn how to:
 * - Connect to external MCP servers using LangChain4j MCP client
 * - Use the official MCP "everything" demo server
 * - Integrate external MCP tools with LangChain4j AiServices
 * - Understand the difference between local @Tool methods and external MCP tools
 * <p>
 * Prerequisites:
 * - Understanding of @Tool annotation from Lab 6
 * - Docker for running the MCP "everything" server
 * - MCP "everything" server running: docker run -p 3000:3000 -p 8080:8080 docker.cloudsmith.io/mcp/public/servers/everything:latest
 * <p>
 * Key Concepts:
 * - MCP allows AI applications to access tools from external services
 * - LangChain4j provides MCP client support (not server)
 * - External MCP tools integrate seamlessly with local @Tool methods
 * - MCP enables distributed tool ecosystems
 */
class McpIntegrationTests {

    /**
     * Test 6.5.1: Basic MCP Client Connection
     * <p>
     * Demonstrates connecting to an MCP server and listing available tools.
     * This test shows the fundamental MCP client setup and connection process.
     */
    @Test
    void basicMcpClientConnection() {
        // Check if MCP "everything" server is available
        assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // Create HTTP transport to connect to MCP server
        McpTransport transport = new HttpMcpTransport.Builder()
                .baseUrl("http://localhost:3000")
                .timeout(Duration.ofSeconds(30))
                .build();

        // Create MCP client with the transport
        McpClient mcpClient = new McpClient.Builder()
                .transport(transport)
                .build();

        try {
            // Initialize the client connection
            mcpClient.initialize();
            System.out.println("Successfully connected to MCP server");

            // List available tools from the MCP server
            var tools = mcpClient.listTools();
            System.out.println("Available MCP tools: " + tools.size());
            
            tools.forEach(tool -> {
                System.out.println("- " + tool.getName() + ": " + tool.getDescription());
            });

            // Verify we found some tools
            assertFalse(tools.isEmpty(), "MCP server should provide tools");
            
        } finally {
            // Clean up the client connection
            mcpClient.close();
        }
    }

    /**
     * Test 6.5.2: MCP Tools with AiServices
     * <p>
     * Demonstrates integrating MCP tools with LangChain4j AiServices.
     * This shows how external MCP tools can be used alongside local @Tool methods.
     */
    @Test
    void mcpToolsWithAiServices() {
        // Check if MCP server is available
        assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.3)
                .build();

        // Create MCP transport and client
        McpTransport transport = new HttpMcpTransport.Builder()
                .baseUrl("http://localhost:3000")
                .timeout(Duration.ofSeconds(30))
                .build();

        McpClient mcpClient = new McpClient.Builder()
                .transport(transport)
                .build();

        try {
            // Initialize MCP client
            mcpClient.initialize();
            System.out.println("Connected to MCP server");

            // Create tool provider from MCP client
            ToolProvider mcpToolProvider = ToolProvider.from(mcpClient);
            
            // Define AI assistant interface
            interface McpAssistant {
                String chat(String message);
            }

            // Build AI service with MCP tools
            McpAssistant assistant = AiServices.builder(McpAssistant.class)
                    .chatModel(chatModel)
                    .toolProviders(mcpToolProvider)
                    .build();

            // Test using MCP tools through the assistant
            System.out.println("\n=== Testing MCP Tool Integration ===");
            
            String response1 = assistant.chat("What tools are available to you from the MCP server?");
            System.out.println("Available tools response: " + response1);
            
            String response2 = assistant.chat("Can you use any filesystem or utility tools to help me?");
            System.out.println("Tool capabilities response: " + response2);

            // Verify responses
            assertNotNull(response1, "Response about available tools should not be null");
            assertNotNull(response2, "Response about tool capabilities should not be null");
            assertFalse(response1.trim().isEmpty(), "Response should contain information about tools");
            
        } finally {
            mcpClient.close();
        }
    }

    /**
     * Test 6.5.3: Combining Local Tools and MCP Tools
     * <p>
     * Demonstrates using both local @Tool methods and external MCP tools together.
     * This shows the power of LangChain4j's unified tool system.
     */
    @Test
    void combiningLocalAndMcpTools() {
        // Check if MCP server is available
        assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.2)
                .build();

        // Create MCP client
        McpTransport transport = new HttpMcpTransport.Builder()
                .baseUrl("http://localhost:3000")
                .timeout(Duration.ofSeconds(30))
                .build();

        McpClient mcpClient = new McpClient.Builder()
                .transport(transport)
                .build();

        try {
            // Initialize MCP client
            mcpClient.initialize();
            
            // Create tool provider from MCP client
            ToolProvider mcpToolProvider = ToolProvider.from(mcpClient);

            // Define AI assistant interface
            interface HybridAssistant {
                String chat(String message);
            }

            // Build AI service with both local tools and MCP tools
            HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
                    .chatModel(chatModel)
                    .tools(new DateTimeTool(), new CalculatorTool()) // Local tools
                    .toolProviders(mcpToolProvider) // External MCP tools
                    .build();

            System.out.println("\n=== Testing Hybrid Tool Integration ===");
            
            // Test combining local and external tools
            String response1 = assistant.chat("What's the current date and time, and what tools do you have available?");
            System.out.println("Hybrid tools response: " + response1);
            
            String response2 = assistant.chat("Calculate 15 * 23, and also tell me what MCP tools you can access");
            System.out.println("Mixed tool usage response: " + response2);

            // Verify responses demonstrate both tool types
            assertNotNull(response1, "Hybrid response should not be null");
            assertNotNull(response2, "Mixed tool response should not be null");
            assertTrue(response1.length() > 20, "Response should be substantive");
            assertTrue(response2.length() > 20, "Response should be substantive");
            
            System.out.println("\nSuccessfully demonstrated hybrid local + MCP tool integration!");
            
        } finally {
            mcpClient.close();
        }
    }

    /**
     * Test 6.5.4: MCP Tool Filtering and Selection
     * <p>
     * Demonstrates how to filter and select specific tools from an MCP server.
     * This is useful when you only want to expose certain external tools to your AI service.
     */
    @Test
    void mcpToolFilteringAndSelection() {
        // Check if MCP server is available
        assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.3)
                .build();

        // Create MCP client
        McpTransport transport = new HttpMcpTransport.Builder()
                .baseUrl("http://localhost:3000")
                .timeout(Duration.ofSeconds(30))
                .build();

        McpClient mcpClient = new McpClient.Builder()
                .transport(transport)
                .build();

        try {
            // Initialize MCP client
            mcpClient.initialize();
            
            // List all available tools first
            var allTools = mcpClient.listTools();
            System.out.println("All available MCP tools (" + allTools.size() + "):");
            allTools.forEach(tool -> System.out.println("- " + tool.getName()));

            // Create filtered tool provider (example: only filesystem-related tools)
            ToolProvider filteredToolProvider = ToolProvider.from(mcpClient)
                    .filter(tool -> {
                        String name = tool.getName().toLowerCase();
                        return name.contains("file") || name.contains("dir") || name.contains("read");
                    });

            // Define AI assistant interface
            interface FilteredAssistant {
                String chat(String message);
            }

            // Build AI service with filtered MCP tools
            FilteredAssistant assistant = AiServices.builder(FilteredAssistant.class)
                    .chatModel(chatModel)
                    .toolProviders(filteredToolProvider)
                    .build();

            System.out.println("\n=== Testing Filtered MCP Tools ===");
            
            String response = assistant.chat("What filesystem or file-related tools do you have available?");
            System.out.println("Filtered tools response: " + response);

            // Verify response
            assertNotNull(response, "Filtered response should not be null");
            assertFalse(response.trim().isEmpty(), "Response should contain tool information");
            
            System.out.println("\nSuccessfully demonstrated MCP tool filtering!");
            
        } finally {
            mcpClient.close();
        }
    }

    /**
     * Helper method to check if the MCP "everything" server is available.
     * The server should be running on localhost:3000.
     */
    private boolean isMcpServerAvailable() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000"))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            
            HttpResponse<String> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            // MCP server should respond to basic HTTP requests
            return response.statusCode() < 500;
            
        } catch (Exception e) {
            System.out.println("MCP server not available: " + e.getMessage());
            System.out.println("Start with: docker run -p 3000:3000 -p 8080:8080 docker.cloudsmith.io/mcp/public/servers/everything:latest");
            return false;
        }
    }
}