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
     * TODO: Implement this test to demonstrate connecting to an MCP server and listing available tools.
     * 
     * Steps to implement:
     * 1. Use assumeTrue() to check if MCP server is available (call isMcpServerAvailable())
     * 2. Create HttpMcpTransport with baseUrl "http://localhost:3000" and 30-second timeout
     * 3. Create McpClient with the transport
     * 4. Initialize the client connection with mcpClient.initialize()
     * 5. List available tools using mcpClient.listTools()
     * 6. Print tool information and verify tools are found
     * 7. Use try-finally to ensure mcpClient.close() is called
     */
    @Test
    void basicMcpClientConnection() {
        // TODO: Implement basic MCP client connection test
        
        // Check if MCP "everything" server is available
        // assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // TODO: Create HTTP transport to connect to MCP server
        // McpTransport transport = new HttpMcpTransport.Builder()
        //         .baseUrl("http://localhost:3000")
        //         .timeout(Duration.ofSeconds(30))
        //         .build();

        // TODO: Create MCP client with the transport
        // McpClient mcpClient = new McpClient.Builder()
        //         .transport(transport)
        //         .build();

        // TODO: Use try-finally to manage client lifecycle
        // try {
        //     // Initialize the client connection
        //     mcpClient.initialize();
        //     
        //     // List available tools from the MCP server
        //     var tools = mcpClient.listTools();
        //     
        //     // Print and verify tools
        //     System.out.println("Available MCP tools: " + tools.size());
        //     tools.forEach(tool -> System.out.println("- " + tool.getName() + ": " + tool.getDescription()));
        //     
        //     assertFalse(tools.isEmpty(), "MCP server should provide tools");
        // } finally {
        //     mcpClient.close();
        // }
    }

    /**
     * Test 6.5.2: MCP Tools with AiServices
     * <p>
     * TODO: Implement this test to demonstrate integrating MCP tools with LangChain4j AiServices.
     * 
     * Steps to implement:
     * 1. Check if MCP server is available using assumeTrue()
     * 2. Configure ChatModel with OpenAI GPT-4-1-Nano
     * 3. Create MCP transport and client, then initialize
     * 4. Create ToolProvider from MCP client using ToolProvider.from()
     * 5. Define AI assistant interface
     * 6. Build AI service with .toolProviders(mcpToolProvider)
     * 7. Test asking about available tools and verify responses
     */
    @Test
    void mcpToolsWithAiServices() {
        // TODO: Implement MCP tools integration with AiServices
        
        // Check if MCP server is available
        // assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.3)
        //         .build();

        // TODO: Create MCP transport and client
        // McpTransport transport = new HttpMcpTransport.Builder()
        //         .baseUrl("http://localhost:3000")
        //         .timeout(Duration.ofSeconds(30))
        //         .build();
        //
        // McpClient mcpClient = new McpClient.Builder()
        //         .transport(transport)
        //         .build();

        // TODO: Use try-finally to manage resources
        // try {
        //     mcpClient.initialize();
        //     
        //     // Create tool provider from MCP client
        //     ToolProvider mcpToolProvider = ToolProvider.from(mcpClient);
        //     
        //     // Define AI assistant interface
        //     interface McpAssistant {
        //         String chat(String message);
        //     }
        //
        //     // Build AI service with MCP tools
        //     McpAssistant assistant = AiServices.builder(McpAssistant.class)
        //             .chatModel(chatModel)
        //             .toolProviders(mcpToolProvider)
        //             .build();
        //
        //     // Test using MCP tools
        //     String response1 = assistant.chat("What tools are available to you from the MCP server?");
        //     String response2 = assistant.chat("Can you use any filesystem or utility tools to help me?");
        //     
        //     // Verify responses
        //     assertNotNull(response1, "Response about available tools should not be null");
        //     assertNotNull(response2, "Response about tool capabilities should not be null");
        //     
        // } finally {
        //     mcpClient.close();
        // }
    }

    /**
     * Test 6.5.3: Combining Local Tools and MCP Tools
     * <p>
     * TODO: Implement this test to demonstrate using both local @Tool methods and external MCP tools together.
     * 
     * Steps to implement:
     * 1. Check MCP server availability
     * 2. Configure ChatModel and create MCP client
     * 3. Build AI service with both .tools() (DateTimeTool, CalculatorTool) and .toolProviders()
     * 4. Test questions that require both local and external tools
     * 5. Verify responses demonstrate both tool types working together
     */
    @Test
    void combiningLocalAndMcpTools() {
        // TODO: Implement hybrid local + MCP tools test
        
        // Check if MCP server is available
        // assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.2)
        //         .build();

        // TODO: Create MCP client and tool provider
        // McpTransport transport = new HttpMcpTransport.Builder()
        //         .baseUrl("http://localhost:3000")
        //         .timeout(Duration.ofSeconds(30))
        //         .build();
        //
        // McpClient mcpClient = new McpClient.Builder()
        //         .transport(transport)
        //         .build();

        // TODO: Use try-finally for resource management
        // try {
        //     mcpClient.initialize();
        //     
        //     ToolProvider mcpToolProvider = ToolProvider.from(mcpClient);
        //
        //     // Define AI assistant interface
        //     interface HybridAssistant {
        //         String chat(String message);
        //     }
        //
        //     // Build AI service with both local tools and MCP tools
        //     HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
        //             .chatModel(chatModel)
        //             .tools(new DateTimeTool(), new CalculatorTool()) // Local tools
        //             .toolProviders(mcpToolProvider) // External MCP tools
        //             .build();
        //
        //     // Test combining local and external tools
        //     String response1 = assistant.chat("What's the current date and time, and what tools do you have available?");
        //     String response2 = assistant.chat("Calculate 15 * 23, and also tell me what MCP tools you can access");
        //
        //     // Verify responses demonstrate both tool types
        //     assertNotNull(response1, "Hybrid response should not be null");
        //     assertNotNull(response2, "Mixed tool response should not be null");
        //     
        // } finally {
        //     mcpClient.close();
        // }
    }

    /**
     * Test 6.5.4: MCP Tool Filtering and Selection
     * <p>
     * TODO: Implement this test to demonstrate how to filter and select specific tools from an MCP server.
     * 
     * Steps to implement:
     * 1. Check MCP server availability
     * 2. Configure ChatModel and create MCP client
     * 3. List all available tools first
     * 4. Create filtered ToolProvider using .filter() with a predicate (e.g., filesystem tools)
     * 5. Build AI service with filtered tool provider
     * 6. Test asking about filtered tools and verify only selected tools are available
     */
    @Test
    void mcpToolFilteringAndSelection() {
        // TODO: Implement MCP tool filtering test
        
        // Check if MCP server is available
        // assumeTrue(isMcpServerAvailable(), "MCP 'everything' server is not available");

        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.3)
        //         .build();

        // TODO: Create MCP client
        // McpTransport transport = new HttpMcpTransport.Builder()
        //         .baseUrl("http://localhost:3000")
        //         .timeout(Duration.ofSeconds(30))
        //         .build();
        //
        // McpClient mcpClient = new McpClient.Builder()
        //         .transport(transport)
        //         .build();

        // TODO: Use try-finally for resource management
        // try {
        //     mcpClient.initialize();
        //     
        //     // List all available tools first
        //     var allTools = mcpClient.listTools();
        //     System.out.println("All available MCP tools (" + allTools.size() + "):");
        //     allTools.forEach(tool -> System.out.println("- " + tool.getName()));
        //
        //     // Create filtered tool provider (example: only filesystem-related tools)
        //     ToolProvider filteredToolProvider = ToolProvider.from(mcpClient)
        //             .filter(tool -> {
        //                 String name = tool.getName().toLowerCase();
        //                 return name.contains("file") || name.contains("dir") || name.contains("read");
        //             });
        //
        //     // Define AI assistant interface
        //     interface FilteredAssistant {
        //         String chat(String message);
        //     }
        //
        //     // Build AI service with filtered MCP tools
        //     FilteredAssistant assistant = AiServices.builder(FilteredAssistant.class)
        //             .chatModel(chatModel)
        //             .toolProviders(filteredToolProvider)
        //             .build();
        //
        //     String response = assistant.chat("What filesystem or file-related tools do you have available?");
        //     System.out.println("Filtered tools response: " + response);
        //
        //     // Verify response
        //     assertNotNull(response, "Filtered response should not be null");
        //     
        // } finally {
        //     mcpClient.close();
        // }
    }

    /**
     * Helper method to check if the MCP "everything" server is available.
     * <p>
     * TODO: Implement this helper method to check MCP server availability.
     * 
     * Steps to implement:
     * 1. Create HttpClient and HttpRequest for localhost:3000
     * 2. Send request with 5-second timeout
     * 3. Return true if status code < 500, false on any exception
     * 4. Include helpful error message about starting the MCP server
     */
    private boolean isMcpServerAvailable() {
        // TODO: Implement MCP server availability check
        
        // try {
        //     HttpClient client = HttpClient.newHttpClient();
        //     HttpRequest request = HttpRequest.newBuilder()
        //             .uri(URI.create("http://localhost:3000"))
        //             .timeout(Duration.ofSeconds(5))
        //             .build();
        //     
        //     HttpResponse<String> response = client.send(request, 
        //             HttpResponse.BodyHandlers.ofString());
        //     
        //     // MCP server should respond to basic HTTP requests
        //     return response.statusCode() < 500;
        //     
        // } catch (Exception e) {
        //     System.out.println("MCP server not available: " + e.getMessage());
        //     System.out.println("Start with: docker run -p 3000:3000 -p 8080:8080 docker.cloudsmith.io/mcp/public/servers/everything:latest");
        //     return false;
        // }
        
        return false; // TODO: Remove this line and implement the method
    }
}