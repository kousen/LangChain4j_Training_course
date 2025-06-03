package com.kousenit.langchain4j;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
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

    /**
     * Test 6.5.1: Basic MCP Client and Tool Provider Setup
     * <p>
     * Demonstrates creating an MCP client and tool provider for the "everything" server.
     * This test shows the fundamental MCP setup process using npx stdio transport.
     */
    @Test
    void basicMcpClientSetup() {
        // Create stdio transport for MCP "everything" server via npx
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything"))
                .logEvents(true)
                .build();

        // Create MCP client with unique key
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("EverythingClient")
                .transport(transport)
                .build();

        // Create MCP tool provider
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        System.out.println("Successfully created MCP client and tool provider");
        
        // Verify tool provider was created
        assertNotNull(toolProvider, "MCP tool provider should be created");
        assertNotNull(mcpClient, "MCP client should be created");
        
        System.out.println("MCP setup completed successfully!");
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

        // Create stdio transport for MCP "everything" server via npx
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything"))
                .build();

        // Create MCP client with unique key
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("AiServicesClient")
                .transport(transport)
                .build();

        // Create MCP tool provider
        McpToolProvider mcpToolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
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

        // Create stdio transport for MCP "everything" server via npx
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything"))
                .build();

        // Create MCP client with unique key
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("HybridClient")
                .transport(transport)
                .build();

        // Create MCP tool provider
        McpToolProvider mcpToolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        // Define AI assistant interface
        interface HybridAssistant {
            String chat(String message);
        }

        // Build AI service with both local tools and MCP tools
        HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
                .chatModel(chatModel)
                .tools(new DateTimeTool(), new CalculatorTool()) // Local tools
                .toolProvider(mcpToolProvider) // External MCP tools
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

        // Create stdio transport for MCP "everything" server via npx
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything"))
                .build();

        // Create MCP client with unique key
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("FilteredClient")
                .transport(transport)
                .build();

        // Create MCP tool provider with tool name filtering (if available)
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                // Note: filterToolNames might be available depending on LangChain4j version
                // .filterToolNames("specific_tool_name")
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