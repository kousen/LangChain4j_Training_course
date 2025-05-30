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

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 5: Chat Memory
 * <p>
 * This lab demonstrates how to maintain conversation context using LangChain4j's ChatMemory.
 * You'll learn how to:
 * - Understand the stateless nature of AI model interactions
 * - Use ChatMemory to retain conversation state across multiple interactions
 * - Work with different memory types (MessageWindowChatMemory)
 * - Integrate memory with AiServices for persistent conversations
 */
class ChatMemoryTests {

    /**
     * Test 5.1: Demonstrating Stateless Behavior
     * <p>
     * TODO: Demonstrate that AI models are stateless by default
     * 1. Create an OpenAI chat model
     * 2. Send a message introducing yourself with a memorable name
     * 3. Ask "Who am I?" in a separate call
     * 4. Verify the model doesn't remember the previous conversation
     */
    @Test
    void defaultRequestsAreStateless() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: First interaction - introduce yourself
        // System.out.println("=== Demonstrating Stateless Behavior ===");
        // System.out.println("First interaction:");
        // String response1 = model.chat("My name is Inigo Montoya. You killed my father. Prepare to die.");
        // System.out.println(response1);

        // TODO: Second interaction - ask who you are
        // System.out.println("\nSecond interaction:");
        // String response2 = model.chat("Who am I?");
        // System.out.println(response2);
        // System.out.println("=".repeat(50));

        // TODO: Verify the model doesn't remember the previous conversation
        // assertAll("Stateless behavior validation",
        //     () -> assertNotNull(response1, "First response should not be null"),
        //     () -> assertNotNull(response2, "Second response should not be null"),
        //     () -> assertFalse(response2.toLowerCase().contains("inigo montoya"),
        //                      "The model should not remember previous conversations without memory")
        // );
        
        // TODO: Use AssertJ for additional string validation
        // assertThat(response1).as("First response content").isNotBlank();
        // assertThat(response2).as("Second response content").isNotBlank();
    }

    /**
     * Test 5.2: Adding Memory to Retain Conversation State
     * <p>
     * TODO: Use ChatMemory to maintain conversation state across interactions
     * 1. Create an OpenAI chat model
     * 2. Create a MessageWindowChatMemory with capacity for 10 messages
     * 3. Manually add messages to memory and get responses
     * 4. Verify the model remembers the previous conversation when using memory
     */
    @Test
    void requestsWithMemory() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create chat memory with a window of 10 messages
        // ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // TODO: First interaction - establish identity
        // System.out.println("=== Demonstrating Memory Retention ===");
        // System.out.println("First interaction with memory:");
        // UserMessage firstMessage = UserMessage.from("My name is Inigo Montoya. You killed my father. Prepare to die.");
        // memory.add(firstMessage);
        
        // ChatResponse response1 = model.chat(memory.messages());
        // memory.add(response1.aiMessage());
        // System.out.println(response1.aiMessage().text());

        // TODO: Second interaction - test memory
        // System.out.println("\nSecond interaction with memory:");
        // UserMessage secondMessage = UserMessage.from("Who am I?");
        // memory.add(secondMessage);
        
        // ChatResponse response2 = model.chat(memory.messages());
        // memory.add(response2.aiMessage());
        // System.out.println(response2.aiMessage().text());
        
        // System.out.println("\nMemory contents:");
        // System.out.println("Total messages in memory: " + memory.messages().size());
        // System.out.println("=".repeat(50));

        // TODO: Verify the model correctly identifies the user using memory
        // String response2Text = response2.aiMessage().text();
        // assertAll("Memory retention validation",
        //     () -> assertNotNull(response1.aiMessage().text(), "First response should not be null"),
        //     () -> assertNotNull(response2Text, "Second response should not be null"),
        //     () -> assertTrue(response2Text.toLowerCase().contains("inigo montoya") || 
        //                    response2Text.toLowerCase().contains("inigo"),
        //                    "The model should remember the user's identity when using memory"),
        //     () -> assertTrue(memory.messages().size() >= 4, "Memory should contain at least 4 messages")
        // );
        
        // TODO: Verify response quality using AssertJ
        // assertThat(response2Text).as("Memory-enhanced response").isNotBlank().hasSizeGreaterThan(10);
    }

    /**
     * Test 5.3: Using Different Memory Window Sizes
     * <p>
     * TODO: Compare different memory configurations
     * 1. Create an OpenAI chat model
     * 2. Create two different MessageWindowChatMemory instances with different sizes
     * 3. Add the same conversation history to both
     * 4. Test how they respond differently based on their capacity
     */
    @Test
    void differentMemoryTypes() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create memory instances with different sizes
        // ChatMemory largeMemory = MessageWindowChatMemory.withMaxMessages(10);
        // ChatMemory smallMemory = MessageWindowChatMemory.withMaxMessages(3);

        // TODO: Add some conversation history to both memories
        // UserMessage intro = UserMessage.from("Hello, I'm learning about AI and machine learning.");
        // AiMessage aiResponse = AiMessage.from("That's great! I'm here to help you learn about AI and ML concepts.");
        
        // largeMemory.add(intro);
        // largeMemory.add(aiResponse);
        // smallMemory.add(intro);
        // smallMemory.add(aiResponse);

        // TODO: Test with both memory types
        // System.out.println("=== Different Memory Window Sizes ===");
        // UserMessage newMessage = UserMessage.from("What did I just tell you about myself?");
        
        // System.out.println("Testing Large Memory Window (10 messages):");
        // largeMemory.add(newMessage);
        // ChatResponse largeResponse = model.chat(largeMemory.messages());
        // largeMemory.add(largeResponse.aiMessage());
        // System.out.println("Large memory response: " + largeResponse.aiMessage().text());
        // System.out.println("Messages in large memory: " + largeMemory.messages().size());

        // System.out.println("\nTesting Small Memory Window (3 messages):");
        // smallMemory.add(newMessage);
        // ChatResponse smallResponse = model.chat(smallMemory.messages());
        // smallMemory.add(smallResponse.aiMessage());
        // System.out.println("Small memory response: " + smallResponse.aiMessage().text());
        // System.out.println("Messages in small memory: " + smallMemory.messages().size());
        // System.out.println("=".repeat(50));
        
        // TODO: Verify both memory instances work but have different capacities
        // assertAll("Different memory window validation",
        //     () -> assertNotNull(largeResponse.aiMessage().text(), "Large memory response should not be null"),
        //     () -> assertNotNull(smallResponse.aiMessage().text(), "Small memory response should not be null"),
        //     () -> assertTrue(largeMemory.messages().size() <= 10, "Large memory should respect max limit"),
        //     () -> assertTrue(smallMemory.messages().size() <= 3, "Small memory should respect smaller limit")
        // );
        
        // TODO: Verify both responses demonstrate memory using AssertJ
        // assertThat(largeResponse.aiMessage().text()).as("Large memory response")
        //         .isNotBlank().containsAnyOf("learning", "ai", "machine learning", "ml");
        // assertThat(smallResponse.aiMessage().text()).as("Small memory response") 
        //         .isNotBlank().containsAnyOf("learning", "ai", "machine learning", "ml");
    }

    /**
     * Interface for AI service with memory integration.
     */
    interface AssistantWithMemory {
        String chat(String message);
    }

    /**
     * Test 5.4: Memory with AiServices
     * <p>
     * TODO: Use memory with AiServices for seamless persistent conversations
     * 1. Create an OpenAI chat model
     * 2. Create a ChatMemory instance
     * 3. Build AssistantWithMemory using AiServices with memory integration
     * 4. Have a multi-turn conversation
     * 5. Verify memory works across all interactions
     */
    @Test
    void aiServicesWithMemory() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create chat memory
        // ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // TODO: Create AI service with memory integration
        // AssistantWithMemory assistant = AiServices.builder(AssistantWithMemory.class)
        //         .chatLanguageModel(model)
        //         .chatMemory(memory)
        //         .build();

        // TODO: Have a conversation that tests memory
        // System.out.println("=== AiServices with Memory ===");
        // String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer working on AI applications.");
        // System.out.println("Response 1: " + response1);

        // String response2 = assistant.chat("What's my name and what do I do for work?");
        // System.out.println("Response 2: " + response2);
        
        // String response3 = assistant.chat("What type of applications am I working on?");
        // System.out.println("Response 3: " + response3);
        
        // System.out.println("Memory size: " + memory.messages().size() + " messages");
        // System.out.println("=".repeat(50));

        // TODO: Verify memory is working across all interactions
        // assertAll("AiServices memory integration validation",
        //     () -> assertNotNull(response1, "First response should not be null"),
        //     () -> assertNotNull(response2, "Second response should not be null"), 
        //     () -> assertNotNull(response3, "Third response should not be null"),
        //     () -> assertTrue(response2.toLowerCase().contains("alice"), 
        //                    "Should remember the user's name"),
        //     () -> assertTrue(response2.toLowerCase().contains("software") || 
        //                    response2.toLowerCase().contains("developer"),
        //                    "Should remember the user's profession"),
        //     () -> assertTrue(memory.messages().size() >= 6, "Memory should contain conversation history")
        // );
        
        // TODO: Verify response quality and memory retention using AssertJ
        // assertThat(response2).as("Name and profession recall").isNotBlank().containsIgnoringCase("alice");
        // assertThat(response3).as("Application type recall").isNotBlank()
        //         .containsAnyOf("ai", "artificial intelligence", "application");
    }

    /**
     * Test 5.5: Advanced Memory Management
     * <p>
     * TODO: Demonstrate advanced memory management patterns
     * 1. Create a small memory window to test eviction
     * 2. Fill memory beyond its capacity
     * 3. Verify oldest messages are evicted
     * 4. Test conversation continuity despite limited memory
     */
    @Test
    void advancedMemoryManagement() {
        // TODO: Create model and small memory for testing eviction
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // ChatMemory memory = MessageWindowChatMemory.withMaxMessages(4); // Small window for testing

        // TODO: Fill memory beyond capacity
        // System.out.println("=== Advanced Memory Management ===");
        // memory.add(UserMessage.from("Message 1: Hello"));
        // memory.add(AiMessage.from("Response 1: Hi there!"));
        // memory.add(UserMessage.from("Message 2: How are you?"));
        // memory.add(AiMessage.from("Response 2: I'm doing well!"));
        // memory.add(UserMessage.from("Message 3: What's the weather like?"));
        // memory.add(AiMessage.from("Response 3: I don't have access to weather data."));
        
        // System.out.println("Memory after adding 6 messages (max 4): " + memory.messages().size());
        
        // TODO: Test that oldest messages are evicted
        // boolean hasFirstMessage = memory.messages().stream()
        //         .anyMatch(msg -> msg.toString().contains("Message 1"));
        
        // System.out.println("Does memory still contain first message? " + hasFirstMessage);
        
        // TODO: Test conversation continuity despite limited memory
        // memory.add(UserMessage.from("What was the last question I asked?"));
        // ChatResponse response = model.chat(memory.messages());
        // memory.add(response.aiMessage());
        
        // System.out.println("Response about last question: " + response.aiMessage().text());
        // System.out.println("Final memory size: " + memory.messages().size());
        // System.out.println("=".repeat(50));
        
        // TODO: Verify memory management behavior
        // assertAll("Advanced memory management validation",
        //     () -> assertEquals(4, memory.messages().size(), "Memory should respect max message limit"),
        //     () -> assertFalse(hasFirstMessage, "Oldest messages should be evicted"),
        //     () -> assertNotNull(response.aiMessage().text(), "Should get response despite memory limits")
        // );
        
        // TODO: Verify memory contains recent conversation using AssertJ
        // String allMessages = memory.messages().stream()
        //         .map(msg -> msg.toString())
        //         .reduce("", (a, b) -> a + " " + b);
                
        // assertThat(allMessages).as("Recent memory content").contains("weather").contains("last question");
    }
}