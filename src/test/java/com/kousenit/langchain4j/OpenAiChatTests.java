package com.kousenit.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 1: Basic Chat Interactions
 * 
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
     * 
     * TODO: Implement a basic chat interaction
     * 1. Create an OpenAI chat model using the builder pattern
     * 2. Set the API key from environment variable: System.getenv("OPENAI_API_KEY")
     * 3. Use GPT_4_O_MINI model
     * 4. Send a simple user message and get the response
     * 5. Print the response and verify it's not null or empty
     */
    @Test
    void simpleQuery() {
        // TODO: Create OpenAI chat model using builder pattern
        // ChatModel model = OpenAiChatModel.builder()...
        
        // TODO: Send a user message and get the response
        // String response = model.chat("Why is the sky blue?");

        // TODO: Print and verify the response
        // System.out.println(response);
        // assertNotNull(response);
        // assertFalse(response.isEmpty());
    }

    /**
     * Test 1.2: System Message
     * 
     * TODO: Implement a chat interaction with a system message
     * 1. Create the same OpenAI chat model
     * 2. Create a SystemMessage to modify the model's behavior
     * 3. Create a UserMessage with your query
     * 4. Send both messages and get the Response object
     * 5. Extract the text from the response and verify it
     */
    @Test
    void simpleQueryWithSystemMessage() {
        // TODO: Create OpenAI chat model
        
        // TODO: Create system and user messages
        // SystemMessage systemMessage = SystemMessage.from("You are a helpful assistant that responds like a pirate.");
        // UserMessage userMessage = UserMessage.from("Why is the sky blue?");

        // TODO: Generate response with both messages
        // ChatResponse response = model.chat(systemMessage, userMessage);

        // TODO: Extract and verify the response
        // String responseText = response.aiMessage().text();
        // System.out.println(responseText);
        // assertNotNull(responseText);
    }

    /**
     * Test 1.3: Accessing Response Metadata
     * 
     * TODO: Implement a chat interaction that accesses response metadata
     * 1. Create the OpenAI chat model
     * 2. Create a UserMessage
     * 3. Generate a response and get the full Response object
     * 4. Extract and print the content, token usage, and finish reason
     * 5. Verify all metadata is present
     */
    @Test
    void simpleQueryWithMetadata() {
        // TODO: Create OpenAI chat model
        
        // TODO: Create user message
        // UserMessage userMessage = UserMessage.from("Why is the sky blue?");
        
        // TODO: Generate response
        // ChatResponse response = model.chat(userMessage);

        // TODO: Extract and print metadata
        // System.out.println("Content: " + response.aiMessage().text());
        // System.out.println("Token Usage: " + response.tokenUsage());
        // System.out.println("Finish Reason: " + response.finishReason());
        
        // TODO: Verify the response content
        // assertNotNull(response.aiMessage().text());
    }
}