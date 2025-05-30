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
import static org.assertj.core.api.Assertions.assertThat;
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
     * Demonstrates streaming chat that receives tokens in real-time.
     * Shows how to collect tokens as they arrive and handle completion.
     */
    @Test
    void streamingChat() throws InterruptedException {
        // Create OpenAI streaming chat model
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create user message
        String userMessage = "Tell me a story about a brave robot.";

        // Set up synchronization and response collection
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder fullResponse = new StringBuilder();

        System.out.println("Streaming Chat Response:");
        System.out.println("=".repeat(50));

        // Start streaming with response handler
        model.chat(userMessage, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                System.out.print(token);
                fullResponse.append(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Streaming completed!");
                System.out.println("Full response length: " + fullResponse.length() + " characters");
                if (response.finishReason() != null) {
                    System.out.println("Finish reason: " + response.finishReason());
                }
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("Error: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait for completion
        latch.await();
        
        // Verify response was received using AssertJ
        assertThat(fullResponse.toString())
                .as("Streaming response")
                .isNotEmpty()
                .hasSizeGreaterThan(10);
        System.out.println("✅ Streaming test completed successfully");
    }

    /**
     * Test 2.2: Streaming with Multiple Messages
     * <p>
     * Demonstrates streaming with conversation context (system + user messages).
     * Shows how to handle streaming with multiple messages and timeouts.
     */
    @Test
    void streamingWithContext() throws InterruptedException {
        // Create OpenAI streaming chat model
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create system and user messages
        SystemMessage systemMessage = SystemMessage.from("You are a helpful coding assistant.");
        UserMessage userMessage = UserMessage.from("Explain recursion in simple terms.");
        
        // Set up synchronization
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder responseBuilder = new StringBuilder();

        System.out.println("\nStreaming with Context:");
        System.out.println("=".repeat(50));

        // Stream with multiple messages
        model.chat(Arrays.asList(systemMessage, userMessage), 
            new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    System.out.print(token);
                    responseBuilder.append(token);
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("Response completed with: " + response.finishReason());
                    System.out.println("Response length: " + responseBuilder.length() + " characters");
                    latch.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    System.err.println("Error occurred: " + error.getMessage());
                    latch.countDown();
                }
            });

        // Wait for completion with timeout
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        
        assertAll("Streaming with context validation",
            () -> assertTrue(completed, "Streaming should complete within 30 seconds"),
            () -> assertThat(responseBuilder.toString())
                    .as("Response content")
                    .isNotEmpty()
                    .hasSizeGreaterThan(10)
        );
        System.out.println("✅ Context streaming test completed successfully");
    }

    /**
     * Test 2.3: Error Handling in Streaming
     * <p>
     * Demonstrates streaming with deliberate error handling.
     * Tests how errors are properly caught and handled in streaming scenarios.
     */
    @Test
    void streamingErrorHandling() throws InterruptedException {
        // Create a streaming model with invalid configuration to trigger error
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey("invalid-key") // This should cause an authentication error
                .modelName(GPT_4_1_NANO)
                .build();

        // Set up error tracking
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] errorOccurred = {false};
        final String[] errorMessage = {null};

        System.out.println("\nTesting Error Handling:");
        System.out.println("=".repeat(50));

        // Attempt streaming with error handling
        String userMessage = "This should fail due to invalid API key";
        
        model.chat(userMessage, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                System.out.print(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("Unexpected completion - this should not happen with invalid key");
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                System.out.println("Expected error occurred: " + error.getMessage());
                errorOccurred[0] = true;
                errorMessage[0] = error.getMessage();
                latch.countDown();
            }
        });

        // Wait and verify error was handled using hybrid approach
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        
        assertAll("Error handling validation",
            () -> assertTrue(completed, "Error handling should complete within 10 seconds"),
            () -> assertTrue(errorOccurred[0], "Error should have been handled"),
            () -> assertNotNull(errorMessage[0], "Error message should be captured")
        );
        
        // Use AssertJ for string content validation
        assertThat(errorMessage[0])
                .as("Error message content")
                .isNotBlank()
                .containsAnyOf("401", "invalid", "unauthorized", "api key");
        
        System.out.println("Error message captured: " + errorMessage[0]);
        System.out.println("✅ Error handling test completed successfully");
    }

    /**
     * Test 2.4: Advanced Streaming with Response Tracking
     * <p>
     * Demonstrates advanced streaming features including response timing
     * and token counting for educational purposes.
     */
    @Test
    void advancedStreamingFeatures() throws InterruptedException {
        // Create OpenAI streaming chat model
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create user message
        String userMessage = "Write a haiku about programming.";
        
        // Set up advanced tracking
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder fullResponse = new StringBuilder();
        long startTime = System.currentTimeMillis();
        final int[] tokenCount = {0};

        System.out.println("\nAdvanced Streaming Features:");
        System.out.println("=".repeat(50));

        // Start streaming with detailed tracking
        model.chat(userMessage, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                System.out.print(token);
                fullResponse.append(token);
                tokenCount[0]++;
                
                // Show progress every 10 tokens
                if (tokenCount[0] % 10 == 0) {
                    System.out.flush(); // Ensure output is visible
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Advanced Streaming Statistics:");
                System.out.println("- Duration: " + duration + "ms");
                System.out.println("- Token count: " + tokenCount[0]);
                System.out.println("- Characters: " + fullResponse.length());
                System.out.println("- Average tokens/second: " + (tokenCount[0] * 1000.0 / duration));
                
                if (response.tokenUsage() != null) {
                    System.out.println("- Input tokens: " + response.tokenUsage().inputTokenCount());
                    System.out.println("- Output tokens: " + response.tokenUsage().outputTokenCount());
                    System.out.println("- Total tokens: " + response.tokenUsage().totalTokenCount());
                }
                
                System.out.println("- Finish reason: " + response.finishReason());
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("Unexpected error: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait for completion and verify results using hybrid approach
        boolean completed = latch.await(15, TimeUnit.SECONDS);
        
        assertAll("Advanced streaming validation",
            () -> assertTrue(completed, "Advanced streaming should complete within 15 seconds"),
            () -> assertThat(fullResponse.toString())
                    .as("Full response content")
                    .isNotEmpty()
                    .hasSizeGreaterThan(10),
            () -> assertThat(tokenCount[0])
                    .as("Token count")
                    .isPositive()
                    .isGreaterThan(5)
        );
        
        System.out.println("✅ Advanced streaming test completed successfully");
    }
}