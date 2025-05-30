package com.kousenit.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 5: Chat Memory
 * <p>
 * This lab demonstrates how to maintain conversation context using LangChain4j's ChatMemory.
 * You'll learn how to:
 * - Understand the stateless nature of AI model interactions
 * - Use ChatMemory to retain conversation state across multiple interactions
 * - Work with different memory types (MessageWindowChatMemory, TokenWindowChatMemory)
 * - Integrate memory with AiServices for persistent conversations
 */
class ChatMemoryTests {

    /**
     * Test 5.1: Demonstrating Stateless Behavior
     * <p>
     * All requests to AI models are stateless by default. This test demonstrates
     * that without memory, the AI doesn't remember previous conversations.
     */
    @Test
    void defaultRequestsAreStateless() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        System.out.println("=== Demonstrating Stateless Behavior ===");
        System.out.println("First interaction:");
        String response1 = model.chat("My name is Inigo Montoya. You killed my father. Prepare to die.");
        System.out.println(response1);

        System.out.println("\nSecond interaction:");
        String response2 = model.chat("Who am I?");
        System.out.println(response2);
        System.out.println("=".repeat(50));

        // Verify the model doesn't remember the previous conversation
        assertAll("Stateless behavior validation",
            () -> assertNotNull(response1, "First response should not be null"),
            () -> assertNotNull(response2, "Second response should not be null"),
            () -> assertFalse(response2.toLowerCase().contains("inigo montoya"),
                             "The model should not remember previous conversations without memory")
        );
        
        // Use AssertJ for string content validation
        assertThat(response1)
                .as("First response content")
                .isNotBlank()
                .hasSizeGreaterThan(10);
                
        assertThat(response2)
                .as("Second response content") 
                .isNotBlank()
                .hasSizeGreaterThan(5);
    }

    /**
     * Test 5.2: Adding Memory to Retain Conversation State
     * <p>
     * Use LangChain4j's ChatMemory to maintain conversation state across interactions.
     * This demonstrates how to manually manage conversation history.
     */
    @Test
    void requestsWithMemory() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create chat memory with a window of 10 messages
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        System.out.println("=== Demonstrating Memory Retention ===");
        System.out.println("First interaction with memory:");
        
        // First interaction - establish identity
        UserMessage firstMessage = UserMessage.from("My name is Inigo Montoya. You killed my father. Prepare to die.");
        memory.add(firstMessage);
        
        ChatResponse response1 = model.chat(memory.messages());
        memory.add(response1.aiMessage());
        System.out.println(response1.aiMessage().text());

        System.out.println("\nSecond interaction with memory:");
        
        // Second interaction - test memory
        UserMessage secondMessage = UserMessage.from("Who am I?");
        memory.add(secondMessage);
        
        ChatResponse response2 = model.chat(memory.messages());
        memory.add(response2.aiMessage());
        System.out.println(response2.aiMessage().text());
        
        System.out.println("\nMemory contents:");
        System.out.println("Total messages in memory: " + memory.messages().size());
        System.out.println("=".repeat(50));

        // Verify the model correctly identifies the user using memory
        String response2Text = response2.aiMessage().text();
        
        assertAll("Memory retention validation",
            () -> assertNotNull(response1.aiMessage().text(), "First response should not be null"),
            () -> assertNotNull(response2Text, "Second response should not be null"),
            () -> assertTrue(response2Text.toLowerCase().contains("inigo montoya") || 
                           response2Text.toLowerCase().contains("inigo"),
                           "The model should remember the user's identity when using memory"),
            () -> assertTrue(memory.messages().size() >= 4, "Memory should contain at least 4 messages")
        );
        
        // Verify response quality using AssertJ
        assertThat(response2Text)
                .as("Memory-enhanced response")
                .isNotBlank()
                .hasSizeGreaterThan(10);
    }

    /**
     * Test 5.3: Using Different Memory Types
     * <p>
     * LangChain4j provides different memory implementations for different use cases.
     * This demonstrates MessageWindowChatMemory vs TokenWindowChatMemory.
     */
    @Test
    void differentMemoryTypes() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        System.out.println("=== Different Memory Types ===");

        // Message-based memory - limits based on message count
        ChatMemory messageMemory = MessageWindowChatMemory.withMaxMessages(5);
        
        // Second memory instance for comparison
        ChatMemory secondMemory = MessageWindowChatMemory.withMaxMessages(3);

        // Add some conversation history to both memories
        UserMessage intro = UserMessage.from("Hello, I'm learning about AI and machine learning.");
        AiMessage aiResponse = AiMessage.from("That's great! I'm here to help you learn about AI and ML concepts.");
        
        // Add to first memory
        messageMemory.add(intro);
        messageMemory.add(aiResponse);
        
        // Add to second memory  
        secondMemory.add(intro);
        secondMemory.add(aiResponse);

        // Test with message memory
        System.out.println("Testing Message Window Memory:");
        UserMessage newMessage = UserMessage.from("What did I just tell you about myself?");
        messageMemory.add(newMessage);
        
        ChatResponse messageResponse = model.chat(messageMemory.messages());
        messageMemory.add(messageResponse.aiMessage());
        System.out.println("Message memory response: " + messageResponse.aiMessage().text());
        System.out.println("Messages in memory: " + messageMemory.messages().size());

        // Test with second memory (smaller window)
        System.out.println("\nTesting Smaller Memory Window:");
        secondMemory.add(newMessage);
        
        ChatResponse secondResponse = model.chat(secondMemory.messages());
        secondMemory.add(secondResponse.aiMessage());
        System.out.println("Second memory response: " + secondResponse.aiMessage().text());
        System.out.println("Messages in second memory: " + secondMemory.messages().size());
        System.out.println("=".repeat(50));
        
        // Verify both memory instances work
        assertAll("Different memory window validation",
            () -> assertNotNull(messageResponse.aiMessage().text(), "First memory response should not be null"),
            () -> assertNotNull(secondResponse.aiMessage().text(), "Second memory response should not be null"),
            () -> assertTrue(messageMemory.messages().size() <= 5, "First memory should respect max messages limit"),
            () -> assertTrue(secondMemory.messages().size() <= 3, "Second memory should respect smaller limit")
        );
        
        // Verify both responses demonstrate memory using AssertJ
        assertThat(messageResponse.aiMessage().text())
                .as("First memory response")
                .isNotBlank()
                .containsAnyOf("learning", "ai", "machine learning", "ml");
                
        assertThat(secondResponse.aiMessage().text())
                .as("Second memory response")
                .isNotBlank()
                .containsAnyOf("learning", "ai", "machine learning", "ml");
    }

    /**
     * Interface for AI service with memory integration.
     * This demonstrates how to create persistent conversational AI.
     */
    interface AssistantWithMemory {
        String chat(String message);
    }

    /**
     * Test 5.4: Memory with AiServices
     * <p>
     * You can use memory with AiServices for seamless persistent conversations.
     * This is the most convenient way to add memory to your AI applications.
     */
    @Test
    void aiServicesWithMemory() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create chat memory
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // Create AI service with memory integration
        AssistantWithMemory assistant = AiServices.builder(AssistantWithMemory.class)
                .chatModel(model)
                .chatMemory(memory)
                .build();

        System.out.println("=== AiServices with Memory ===");
        
        // First interaction - establish context
        String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer working on AI applications.");
        System.out.println("Response 1: " + response1);

        // Second interaction - test memory retention
        String response2 = assistant.chat("What's my name and what do I do for work?");
        System.out.println("Response 2: " + response2);
        
        // Third interaction - test continued memory
        String response3 = assistant.chat("What type of applications am I working on?");
        System.out.println("Response 3: " + response3);
        
        System.out.println("Memory size: " + memory.messages().size() + " messages");
        System.out.println("=".repeat(50));

        // Verify memory is working across all interactions
        assertAll("AiServices memory integration validation",
            () -> assertNotNull(response1, "First response should not be null"),
            () -> assertNotNull(response2, "Second response should not be null"), 
            () -> assertNotNull(response3, "Third response should not be null"),
            () -> assertTrue(response2.toLowerCase().contains("alice"), 
                           "Should remember the user's name"),
            () -> assertTrue(response2.toLowerCase().contains("software") || 
                           response2.toLowerCase().contains("developer"),
                           "Should remember the user's profession"),
            () -> assertTrue(memory.messages().size() >= 6, "Memory should contain conversation history")
        );
        
        // Verify response quality and memory retention using AssertJ
        assertThat(response2)
                .as("Name and profession recall")
                .isNotBlank()
                .containsIgnoringCase("alice");
                
        assertThat(response3)
                .as("Application type recall")
                .isNotBlank()
                .containsAnyOf("ai", "artificial intelligence", "application");
    }

    /**
     * Test 5.5: Advanced Memory Management
     * <p>
     * Demonstrates advanced memory management patterns including memory inspection,
     * selective memory clearing, and memory size optimization.
     */
    @Test
    void advancedMemoryManagement() {
        // Create model and memory
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(4); // Small window for testing

        System.out.println("=== Advanced Memory Management ===");
        
        // Fill up memory beyond its capacity
        memory.add(UserMessage.from("Message 1: Hello"));
        memory.add(AiMessage.from("Response 1: Hi there!"));
        memory.add(UserMessage.from("Message 2: How are you?"));
        memory.add(AiMessage.from("Response 2: I'm doing well!"));
        memory.add(UserMessage.from("Message 3: What's the weather like?"));
        memory.add(AiMessage.from("Response 3: I don't have access to weather data."));
        
        System.out.println("Memory after adding 6 messages (max 4): " + memory.messages().size());
        
        // Test that oldest messages are evicted
        boolean hasFirstMessage = memory.messages().stream()
                .anyMatch(msg -> msg.toString().contains("Message 1"));
        
        System.out.println("Does memory still contain first message? " + hasFirstMessage);
        
        // Test conversation continuity despite limited memory
        memory.add(UserMessage.from("What was the last question I asked?"));
        ChatResponse response = model.chat(memory.messages());
        memory.add(response.aiMessage());
        
        System.out.println("Response about last question: " + response.aiMessage().text());
        System.out.println("Final memory size: " + memory.messages().size());
        System.out.println("=".repeat(50));
        
        // Verify memory management behavior
        assertAll("Advanced memory management validation",
            () -> assertEquals(4, memory.messages().size(), "Memory should respect max message limit"),
            () -> assertFalse(hasFirstMessage, "Oldest messages should be evicted"),
            () -> assertNotNull(response.aiMessage().text(), "Should get response despite memory limits")
        );
        
        // Verify memory contains recent conversation using AssertJ
        String allMessages = memory.messages().stream()
                .map(msg -> msg.toString())
                .reduce("", (a, b) -> a + " " + b);
                
        assertThat(allMessages)
                .as("Recent memory content")
                .contains("weather")
                .contains("last question");
    }
}