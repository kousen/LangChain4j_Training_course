package com.kousenit.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

import static dev.langchain4j.model.openai.OpenAiChatModelName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Lab 1: Basic Chat Interactions
 * <p>
 * This lab demonstrates the fundamental patterns for interacting with LangChain4j's ChatModel.
 * You'll learn how to:
 * - Create and configure an OpenAI ChatModel
 * - Send simple queries to the model
 * - Use system messages to modify model behavior
 * - Access response metadata including token usage and finish reasons
 */
class OpenAiChatTests {

    /**
     * Test 1.1: A Simple Query
     * <p>
     * Demonstrates basic chat interaction with OpenAI's ChatModel.
     * Creates a model, sends a query, and verifies the response.
     */
    @Test
    void simpleQuery() {
        // TODO: Create OpenAI chat model using builder pattern
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();
        
        // TODO: Send a user message and get the response
        // String response = model.chat("Why is the sky blue?");

        // TODO: Print and verify the response
        // System.out.println("Simple Query Response:");
        // System.out.println(response);
        // System.out.println("=".repeat(50));
        // 
        // assertNotNull(response, "Response should not be null");
        // assertFalse(response.isEmpty(), "Response should not be empty");
    }

    /**
     * Test 1.2: System Message
     * <p>
     * Demonstrates how to use system messages to modify the model's behavior.
     * The system message acts as instructions that influence how the AI responds.
     */
    @Test
    void simpleQueryWithSystemMessage() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create system and user messages
        // SystemMessage systemMessage = SystemMessage.from("You are a helpful assistant that responds like a pirate.");
        // UserMessage userMessage = UserMessage.from("Why is the sky blue?");

        // TODO: Generate response with both messages
        // ChatResponse response = model.chat(systemMessage, userMessage);

        // TODO: Extract and verify the response
        // String responseText = response.aiMessage().text();
        // System.out.println("Pirate Response:");
        // System.out.println(responseText);
        // System.out.println("=".repeat(50));
        // 
        // assertNotNull(responseText, "Response text should not be null");
        // assertFalse(responseText.isEmpty(), "Response text should not be empty");
    }

    /**
     * Test 1.3: Accessing Response Metadata
     * <p>
     * Demonstrates how to access response metadata including token usage,
     * finish reason, and other information about the AI's response.
     */
    @Test
    void simpleQueryWithMetadata() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();
        
        // TODO: Create user message
        // UserMessage userMessage = UserMessage.from("Why is the sky blue?");
        
        // TODO: Generate response
        // ChatResponse response = model.chat(userMessage);

        // TODO: Extract and print metadata
        // assertNotNull(response, "Response should not be null");
        // String content = response.aiMessage().text();
        // System.out.println("Response with Metadata:");
        // System.out.println("Content: " + content);
        // 
        // if (response.tokenUsage() != null) {
        //     System.out.println("Token Usage: " + response.tokenUsage());
        //     System.out.println("  Input Tokens: " + response.tokenUsage().inputTokenCount());
        //     System.out.println("  Output Tokens: " + response.tokenUsage().outputTokenCount());
        //     System.out.println("  Total Tokens: " + response.tokenUsage().totalTokenCount());
        // } else {
        //     System.out.println("Token Usage: Not available");
        // }
        // 
        // if (response.finishReason() != null) {
        //     System.out.println("Finish Reason: " + response.finishReason());
        // } else {
        //     System.out.println("Finish Reason: Not available");
        // }
        // 
        // System.out.println("=".repeat(50));
        // 
        // // Verify the response content
        // assertNotNull(content, "Response content should not be null");
        // assertFalse(content.isEmpty(), "Response content should not be empty");
    }
}