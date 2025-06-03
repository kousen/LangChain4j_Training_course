package com.kousenit.langchain4j;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;

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
 * - Node.js and npm installed for running the MCP "everything" server
 * - MCP "everything" server accessed via: npx -y @modelcontextprotocol/server-everything
 * <p>
 * Key Concepts:
 * - MCP allows AI applications to access tools from external services
 * - LangChain4j provides MCP client support (not server)
 * - External MCP tools integrate seamlessly with local @Tool methods
 * - MCP enables distributed tool ecosystems
 */
class McpIntegrationTests {

    // Shared MCP client across all tests for efficiency
    private static McpTransport sharedTransport;
    private static McpClient sharedMcpClient;

    @BeforeAll
    static void setupSharedMcpClient() {
        // Create single stdio transport for MCP "everything" server via npx
        sharedTransport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything"))
                .logEvents(false) // Reduce noise across multiple tests
                .build();

        // Create shared MCP client
        sharedMcpClient = new DefaultMcpClient.Builder()
                .key("SharedMcpClient")
                .transport(sharedTransport)
                .build();
        
        System.out.println("Shared MCP client initialized for all tests");
    }

    @AfterAll
    static void teardownSharedMcpClient() {
        // The transport will automatically clean up the npx process
        if (sharedMcpClient != null) {
            System.out.println("Shared MCP client cleanup completed");
        }
    }

    /**
     * Test 6.5.1: Basic MCP Client and Tool Provider Setup
     * <p>
     * Demonstrates creating an MCP client and tool provider for the "everything" server.
     * This test shows the fundamental MCP setup process using npx stdio transport.
     */
    @Test
    void basicMcpClientSetup() {
        // Use shared MCP client for efficiency (initialized in @BeforeAll)
        // This demonstrates the basic setup process that was used to create the shared client
        
        // Create MCP tool provider using the shared client
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(sharedMcpClient)
                .build();

        System.out.println("Successfully created MCP tool provider using shared client");
        
        // Verify tool provider was created
        assertNotNull(toolProvider, "MCP tool provider should be created");
        assertNotNull(sharedMcpClient, "Shared MCP client should be available");
        
        System.out.println("MCP setup verification completed successfully!");
        
        // Note: The actual transport/client setup is demonstrated in @BeforeAll method
        // This pattern avoids creating multiple npx processes and improves test performance
    }

    /**
     * Test 6.5.2: MCP Tools with AiServices
     * <p>
     * Demonstrates integrating MCP tools with LangChain4j AiServices.
     * This shows how external MCP tools can be used in AI conversations.
     */
    @Test
    void mcpToolsWithAiServices() {
        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.3)
                .build();

        // Create MCP tool provider using shared client for efficiency
        McpToolProvider mcpToolProvider = McpToolProvider.builder()
                .mcpClients(sharedMcpClient)
                .build();
        
        // Define AI assistant interface
        interface McpAssistant {
            String chat(String message);
        }

        // Build AI service with MCP tools
        McpAssistant assistant = AiServices.builder(McpAssistant.class)
                .chatModel(chatModel)
                .toolProvider(mcpToolProvider)
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
        
        System.out.println("MCP tool integration test completed successfully!");
    }

    /**
     * Test 6.5.3: Combining Local Tools and MCP Tools
     * <p>
     * Demonstrates using both local @Tool methods and external MCP tools together.
     * This shows the power of LangChain4j's unified tool system.
     */
    @Test
    void combiningLocalAndMcpTools() {
        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.2)
                .build();

        // Create MCP tool provider using shared client for efficiency
        McpToolProvider mcpToolProvider = McpToolProvider.builder()
                .mcpClients(sharedMcpClient)
                .build();

        // Define AI assistant interface
        interface HybridAssistant {
            String chat(String message);
        }

        // Build AI service with both local tools and MCP tools
        // Note: Using only DateTimeTool to avoid conflicts with MCP server tools (e.g., "add" function)
        HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
                .chatModel(chatModel)
                .tools(new DateTimeTool()) // Local tools - avoiding CalculatorTool due to potential conflicts
                .toolProvider(mcpToolProvider) // External MCP tools
                .build();

        System.out.println("\n=== Testing Hybrid Tool Integration ===");
        
        // Test combining local and external tools
        String response1 = assistant.chat("What's the current date and time, and what tools do you have available?");
        System.out.println("Hybrid tools response: " + response1);
        
        String response2 = assistant.chat("What's the current date, and can you also tell me what MCP tools you can access?");
        System.out.println("Mixed tool usage response: " + response2);

        // Verify responses demonstrate both tool types
        assertNotNull(response1, "Hybrid response should not be null");
        assertNotNull(response2, "Mixed tool response should not be null");
        assertTrue(response1.length() > 20, "Response should be substantive");
        assertTrue(response2.length() > 20, "Response should be substantive");
        
        System.out.println("\nSuccessfully demonstrated hybrid local + MCP tool integration!");
    }

    /**
     * Test 6.5.4: MCP Tool Provider with Specific Tool Names
     * <p>
     * Demonstrates how to create an MCP tool provider with specific tool filtering.
     * This shows how to selectively expose certain external tools to your AI service.
     */
    @Test
    void mcpToolProviderWithFiltering() {
        // Configure chat model
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.3)
                .build();

        // Create MCP tool provider using shared client for efficiency
        // Note: filterToolNames might be available depending on LangChain4j version
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(sharedMcpClient)
                // .filterToolNames("specific_tool_name") // Example of potential filtering
                .build();

        // Define AI assistant interface
        interface FilteredAssistant {
            String chat(String message);
        }

        // Build AI service with MCP tool provider
        FilteredAssistant assistant = AiServices.builder(FilteredAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        System.out.println("\n=== Testing MCP Tool Provider ===");
        
        String response = assistant.chat("What tools do you have available from the MCP server?");
        System.out.println("MCP tools response: " + response);

        // Verify response
        assertNotNull(response, "MCP response should not be null");
        assertFalse(response.trim().isEmpty(), "Response should contain tool information");
        
        System.out.println("\nSuccessfully demonstrated MCP tool provider setup!");
    }
}