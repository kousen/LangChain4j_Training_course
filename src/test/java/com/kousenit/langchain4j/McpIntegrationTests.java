package com.kousenit.langchain4j;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.mcp.toolprovider.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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
 * - Docker installed and running
 * - MCP "everything" server accessed via: docker run -i @modelcontextprotocol/server-everything@0.6.2
 * <p>
 * Key Concepts:
 * - MCP allows AI applications to access tools from external services
 * - LangChain4j provides MCP client support (not server)
 * - External MCP tools integrate seamlessly with local @Tool methods
 * - MCP enables distributed tool ecosystems
 */
class McpIntegrationTests {

    /**
     * Test 6.5.1: Basic MCP Client Setup
     * <p>
     * TODO: Implement this test to demonstrate creating an MCP client and tool provider.
     * 
     * Steps to implement:
     * 1. Create StdioMcpTransport with Docker command for "everything" server
     * 2. Create DefaultMcpClient with unique key and the transport
     * 3. Create McpToolProvider using the MCP client
     * 4. Verify both client and tool provider are created successfully
     * 5. No need for server availability checks - Docker handles this
     */
    @Test
    void basicMcpClientSetup() {
        // TODO: Implement basic MCP client setup test
        
        // TODO: Create stdio transport for MCP "everything" server via Docker
        // McpTransport transport = new StdioMcpTransport.Builder()
        //         .command(List.of("docker", "run", "-i", "@modelcontextprotocol/server-everything@0.6.2"))
        //         .logEvents(true)
        //         .build();

        // TODO: Create MCP client with unique key
        // McpClient mcpClient = new DefaultMcpClient.Builder()
        //         .key("EverythingClient")
        //         .transport(transport)
        //         .build();

        // TODO: Create MCP tool provider
        // McpToolProvider toolProvider = McpToolProvider.builder()
        //         .mcpClients(mcpClient)
        //         .build();

        // TODO: Verify setup and print success message
        // System.out.println("Successfully created MCP client and tool provider");
        // assertNotNull(toolProvider, "MCP tool provider should be created");
        // assertNotNull(mcpClient, "MCP client should be created");
        // System.out.println("MCP setup completed successfully!");
    }

    /**
     * Test 6.5.2: MCP Tools with AiServices
     * <p>
     * TODO: Implement this test to demonstrate integrating MCP tools with LangChain4j AiServices.
     * 
     * Steps to implement:
     * 1. Configure ChatModel with OpenAI GPT-4-1-Nano
     * 2. Create MCP transport and client using stdio approach
     * 3. Create McpToolProvider from the MCP client
     * 4. Define AI assistant interface
     * 5. Build AI service with .toolProvider(mcpToolProvider)
     * 6. Test asking about available tools and verify responses
     */
    @Test
    void mcpToolsWithAiServices() {
        // TODO: Implement MCP tools integration with AiServices
        
        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.3)
        //         .build();

        // TODO: Create stdio transport for MCP "everything" server
        // McpTransport transport = new StdioMcpTransport.Builder()
        //         .command(List.of("docker", "run", "-i", "@modelcontextprotocol/server-everything@0.6.2"))
        //         .build();

        // TODO: Create MCP client with unique key
        // McpClient mcpClient = new DefaultMcpClient.Builder()
        //         .key("AiServicesClient")
        //         .transport(transport)
        //         .build();

        // TODO: Create MCP tool provider
        // McpToolProvider mcpToolProvider = McpToolProvider.builder()
        //         .mcpClients(mcpClient)
        //         .build();
        
        // TODO: Define AI assistant interface
        // interface McpAssistant {
        //     String chat(String message);
        // }

        // TODO: Build AI service with MCP tools
        // McpAssistant assistant = AiServices.builder(McpAssistant.class)
        //         .chatModel(chatModel)
        //         .toolProvider(mcpToolProvider)
        //         .build();

        // TODO: Test using MCP tools
        // String response1 = assistant.chat("What tools are available to you from the MCP server?");
        // String response2 = assistant.chat("Can you use any filesystem or utility tools to help me?");
        
        // TODO: Verify responses
        // assertNotNull(response1, "Response about available tools should not be null");
        // assertNotNull(response2, "Response about tool capabilities should not be null");
        // assertFalse(response1.trim().isEmpty(), "Response should contain information about tools");
    }

    /**
     * Test 6.5.3: Combining Local Tools and MCP Tools
     * <p>
     * TODO: Implement this test to demonstrate using both local @Tool methods and external MCP tools together.
     * 
     * Steps to implement:
     * 1. Configure ChatModel
     * 2. Create MCP client and tool provider using stdio approach
     * 3. Build AI service with both .tools() (DateTimeTool, CalculatorTool) and .toolProvider()
     * 4. Test questions that require both local and external tools
     * 5. Verify responses demonstrate both tool types working together
     */
    @Test
    void combiningLocalAndMcpTools() {
        // TODO: Implement hybrid local + MCP tools test
        
        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.2)
        //         .build();

        // TODO: Create stdio transport for MCP "everything" server
        // McpTransport transport = new StdioMcpTransport.Builder()
        //         .command(List.of("docker", "run", "-i", "@modelcontextprotocol/server-everything@0.6.2"))
        //         .build();

        // TODO: Create MCP client with unique key
        // McpClient mcpClient = new DefaultMcpClient.Builder()
        //         .key("HybridClient")
        //         .transport(transport)
        //         .build();

        // TODO: Create MCP tool provider
        // McpToolProvider mcpToolProvider = McpToolProvider.builder()
        //         .mcpClients(mcpClient)
        //         .build();

        // TODO: Define AI assistant interface
        // interface HybridAssistant {
        //     String chat(String message);
        // }

        // TODO: Build AI service with both local tools and MCP tools
        // HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
        //         .chatModel(chatModel)
        //         .tools(new DateTimeTool(), new CalculatorTool()) // Local tools
        //         .toolProvider(mcpToolProvider) // External MCP tools
        //         .build();

        // TODO: Test combining local and external tools
        // String response1 = assistant.chat("What's the current date and time, and what tools do you have available?");
        // String response2 = assistant.chat("Calculate 15 * 23, and also tell me what MCP tools you can access");

        // TODO: Verify responses demonstrate both tool types
        // assertNotNull(response1, "Hybrid response should not be null");
        // assertNotNull(response2, "Mixed tool response should not be null");
        // assertTrue(response1.length() > 20, "Response should be substantive");
        // assertTrue(response2.length() > 20, "Response should be substantive");
    }

    /**
     * Test 6.5.4: MCP Tool Provider Configuration
     * <p>
     * TODO: Implement this test to demonstrate MCP tool provider configuration options.
     * 
     * Steps to implement:
     * 1. Configure ChatModel
     * 2. Create MCP client using stdio approach
     * 3. Create McpToolProvider with configuration options
     * 4. Build AI service with the tool provider
     * 5. Test the configured tool provider functionality
     */
    @Test
    void mcpToolProviderConfiguration() {
        // TODO: Implement MCP tool provider configuration test
        
        // TODO: Configure chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.3)
        //         .build();

        // TODO: Create stdio transport for MCP "everything" server
        // McpTransport transport = new StdioMcpTransport.Builder()
        //         .command(List.of("docker", "run", "-i", "@modelcontextprotocol/server-everything@0.6.2"))
        //         .build();

        // TODO: Create MCP client with unique key
        // McpClient mcpClient = new DefaultMcpClient.Builder()
        //         .key("ConfiguredClient")
        //         .transport(transport)
        //         .build();

        // TODO: Create MCP tool provider with configuration
        // McpToolProvider toolProvider = McpToolProvider.builder()
        //         .mcpClients(mcpClient)
        //         // Note: Additional configuration options may be available
        //         .build();

        // TODO: Define AI assistant interface
        // interface ConfiguredAssistant {
        //     String chat(String message);
        // }

        // TODO: Build AI service with configured tool provider
        // ConfiguredAssistant assistant = AiServices.builder(ConfiguredAssistant.class)
        //         .chatModel(chatModel)
        //         .toolProvider(toolProvider)
        //         .build();

        // TODO: Test configured tool provider
        // String response = assistant.chat("What tools do you have available from the MCP server?");
        // System.out.println("MCP tools response: " + response);

        // TODO: Verify response
        // assertNotNull(response, "MCP response should not be null");
        // assertFalse(response.trim().isEmpty(), "Response should contain tool information");
    }
}