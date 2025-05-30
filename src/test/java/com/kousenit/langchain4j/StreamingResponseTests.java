package com.kousenit.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 2: Streaming Responses
 * <p>
 * This lab demonstrates streaming chat responses using LangChain4j's StreamingChatModel.
 * You'll learn how to:
 * - Create and configure a StreamingChatModel for real-time responses
 * - Handle streaming tokens as they arrive
 * - Manage completion and error states
 * - Use streaming with conversation context
 * - Work with CountDownLatch for asynchronous operations
 */
class StreamingResponseTests {

    /**
     * Test 2.1: Streaming with Reactive Streams
     * <p>
     * TODO: Implement streaming chat that receives tokens in real-time
     * 1. Create an OpenAI streaming chat model using the builder pattern
     * 2. Set up a UserMessage with an interesting prompt
     * 3. Create a CountDownLatch to wait for completion
     * 4. Use a StringBuilder to collect the full response
     * 5. Implement StreamingResponseHandler with onNext, onComplete, and onError
     * 6. Print tokens as they arrive and wait for completion
     */
    @Test
    void streamingChat() throws InterruptedException {
        // TODO: Create OpenAI streaming chat model
        // StreamingChatModel model = OpenAiStreamingChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create user message
        // String userMessage = "Tell me a story about a brave robot.";
        
        // TODO: Set up synchronization and response collection
        // CountDownLatch latch = new CountDownLatch(1);
        // StringBuilder fullResponse = new StringBuilder();

        // TODO: Start streaming with response handler
        // model.chat(userMessage, new StreamingChatResponseHandler() {
        //     @Override
        //     public void onPartialResponse(String token) {
        //         System.out.print(token);
        //         fullResponse.append(token);
        //     }
        //
        //     @Override
        //     public void onCompleteResponse(ChatResponse response) {
        //         System.out.println("\n\nStreaming completed!");
        //         System.out.println("Full response: " + fullResponse.toString());
        //         latch.countDown();
        //     }
        //
        //     @Override
        //     public void onError(Throwable error) {
        //         System.err.println("Error: " + error.getMessage());
        //         latch.countDown();
        //     }
        // });

        // TODO: Wait for completion
        // latch.await();
        
        // TODO: Verify response was received
        // assertFalse(fullResponse.toString().isEmpty());
    }

    /**
     * Test 2.2: Streaming with Multiple Messages
     * <p>
     * TODO: Implement streaming with conversation context (system + user messages)
     * 1. Create an OpenAI streaming chat model
     * 2. Create both SystemMessage and UserMessage
     * 3. Set up CountDownLatch for synchronization
     * 4. Stream with a list of messages
     * 5. Handle completion with finish reason logging
     * 6. Add timeout to the await call for safety
     */
    @Test
    void streamingWithContext() throws InterruptedException {
        // TODO: Create OpenAI streaming chat model
        // StreamingChatModel model = OpenAiStreamingChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create system and user messages
        // SystemMessage systemMessage = SystemMessage.from("You are a helpful coding assistant.");
        // UserMessage userMessage = UserMessage.from("Explain recursion in simple terms.");
        
        // TODO: Set up synchronization
        // CountDownLatch latch = new CountDownLatch(1);
        // StringBuilder responseBuilder = new StringBuilder();

        // TODO: Stream with multiple messages
        // model.chat(Arrays.asList(systemMessage, userMessage), 
        //     new StreamingChatResponseHandler() {
        //         @Override
        //         public void onPartialResponse(String token) {
        //             System.out.print(token);
        //             responseBuilder.append(token);
        //         }
        //
        //         @Override
        //         public void onCompleteResponse(ChatResponse response) {
        //             System.out.println("\n\nResponse completed with: " + response.finishReason());
        //             latch.countDown();
        //         }
        //
        //         @Override
        //         public void onError(Throwable error) {
        //             System.err.println("Error occurred: " + error.getMessage());
        //             latch.countDown();
        //         }
        //     });

        // TODO: Wait for completion with timeout
        // boolean completed = latch.await(30, TimeUnit.SECONDS);
        // assertTrue(completed, "Streaming should complete within 30 seconds");
        // assertFalse(responseBuilder.toString().isEmpty(), "Response should not be empty");
    }

    /**
     * Test 2.3: Error Handling in Streaming
     * <p>
     * TODO: Implement streaming with deliberate error handling
     * 1. Create a streaming model with invalid configuration (to trigger error)
     * 2. Attempt to stream a message
     * 3. Verify that onError is called appropriately
     * 4. Log error details for debugging
     */
    @Test
    void streamingErrorHandling() throws InterruptedException {
        // TODO: Create a streaming model that might fail
        // Note: You could use an invalid API key or model name to test error handling
        // StreamingChatModel model = OpenAiStreamingChatModel.builder()
        //         .apiKey("invalid-key") // This should cause an error
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Set up error tracking
        // CountDownLatch latch = new CountDownLatch(1);
        // final boolean[] errorOccurred = {false};
        // final String[] errorMessage = {null};

        // TODO: Attempt streaming with error handling
        // String userMessage = "This should fail due to invalid API key";
        // 
        // model.chat(userMessage, new StreamingChatResponseHandler() {
        //     @Override
        //     public void onPartialResponse(String token) {
        //         System.out.print(token);
        //     }
        //
        //     @Override
        //     public void onCompleteResponse(ChatResponse response) {
        //         System.out.println("Unexpected completion");
        //         latch.countDown();
        //     }
        //
        //     @Override
        //     public void onError(Throwable error) {
        //         System.err.println("Expected error occurred: " + error.getMessage());
        //         errorOccurred[0] = true;
        //         errorMessage[0] = error.getMessage();
        //         latch.countDown();
        //     }
        // });

        // TODO: Wait and verify error was handled
        // boolean completed = latch.await(10, TimeUnit.SECONDS);
        // assertTrue(completed, "Error handling should complete within 10 seconds");
        // assertTrue(errorOccurred[0], "Error should have been handled");
        // assertNotNull(errorMessage[0], "Error message should be captured");
    }
}