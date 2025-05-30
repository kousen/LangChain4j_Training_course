package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 6: AI Tools
 * <p>
 * This lab demonstrates how to extend AI capabilities using custom tools with the @Tool annotation.
 * You'll learn how to:
 * - Create tool classes with @Tool annotation for AI function calling
 * - Integrate tools with AiServices for enhanced AI capabilities
 * - Build tools that accept parameters for dynamic functionality
 * - Combine multiple tools for comprehensive AI assistants
 * <p>
 * TODO: Follow the exercises in labs.md Lab 6 to implement tool-enabled AI services.
 * Note: Tool classes are provided in src/main/java/com/kousenit/langchain4j/:
 * - DateTimeTool.java (reference implementation)
 * - WeatherTool.java (reference implementation) 
 * - CalculatorTool.java (reference implementation)
 * You can use these directly or study them to create your own versions.
 */
class AiToolsTests {

    /*
     * TODO 6.1: Study the Tool Classes
     * <p>
     * The tool classes are already provided in src/main/java/com/kousenit/langchain4j/:
     * 
     * 1. DateTimeTool.java:
     *    - getCurrentDateTime() - returns current date/time
     *    - getDateYearsFromNow(int years) - calculates future date  
     *    - setAlarm(String time) - simulates setting an alarm
     *    - getDateDaysFromNow(int days) - calculates future date by days
     *    - getCurrentYear() - returns current year
     * 
     * 2. WeatherTool.java:
     *    - getCurrentWeather(String city, String units) - simulates weather API call
     *    - getWeatherForecast(String city, int days) - multi-day forecast
     * 
     * 3. CalculatorTool.java:
     *    - add, subtract, multiply, divide - basic math operations
     *    - power, sqrt, percentage - advanced operations
     *    - Includes proper error handling for division by zero and negative square roots
     * 
     * These classes demonstrate the @Tool annotation pattern.
     * You can use them directly in your tests: new DateTimeTool(), new WeatherTool(), etc.
     */

    /**
     * Assistant interface for AI services with tool integration.
     * You can use this interface for all your tool tests.
     */
    interface Assistant {
        String chat(String message);
    }

    /**
     * TODO 6.2: Basic Tool Usage Test
     * <p>
     * Implement a test that:
     * 1. Creates an OpenAI chat model
     * 2. Creates an Assistant with DateTimeTool using AiServices.builder()
     * 3. Tests asking for current time, future date calculation, and alarm setting
     * 4. Verifies responses are not null and contain expected content
     * 
     * Hint: Use .tools(new DateTimeTool()) when building the AiServices
     * The DateTimeTool class is already available - just instantiate it!
     */
    @Test
    void useBasicDateTimeTool() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()...
        
        // TODO: Create assistant with DateTimeTool
        // Assistant assistant = AiServices.builder(Assistant.class)...
        
        // TODO: Test current date/time request
        // String response1 = assistant.chat("What is the current date and time?");
        
        // TODO: Test future date calculation  
        // String response2 = assistant.chat("What year will it be in 5 years?");
        
        // TODO: Test alarm setting
        // String response3 = assistant.chat("Set an alarm for 8:00 AM tomorrow");
        
        // TODO: Add assertions to verify responses
        // Use assertAll() for grouped assertions and assertThat() for specific content
        
        fail("TODO: Implement basic tool usage test");
    }

    /**
     * TODO 6.3: Tools with Parameters Test
     * <p>
     * Implement a test that:
     * 1. Creates an Assistant with WeatherTool
     * 2. Tests weather queries for different cities and units
     * 3. Verifies the tool correctly uses the provided parameters
     */
    @Test
    void useToolsWithParameters() {
        // TODO: Create OpenAI chat model
        
        // TODO: Create assistant with WeatherTool
        
        // TODO: Test weather query with metric units
        // "What's the weather like in Paris? Use metric units."
        
        // TODO: Test weather query with Fahrenheit
        // "How about the weather in New York with Fahrenheit?"
        
        // TODO: Verify responses contain city names and correct temperature units
        
        fail("TODO: Implement tools with parameters test");
    }

    /**
     * TODO 6.4: Multiple Tools Integration Test
     * <p>
     * Implement a test that:
     * 1. Creates an Assistant with ALL tools (DateTimeTool, CalculatorTool, WeatherTool)
     * 2. Tests complex queries that require multiple tools
     * 3. Verifies the AI can coordinate between different tools
     */
    @Test
    void useMultipleTools() {
        // TODO: Create OpenAI chat model
        
        // TODO: Create assistant with multiple tools
        // .tools(new DateTimeTool(), new CalculatorTool(), new WeatherTool())
        
        // TODO: Test query requiring math AND date calculation
        // "What's 15 multiplied by 8, and what year will it be in 3 years?"
        
        // TODO: Test query requiring division AND time
        // "Calculate 100 divided by 4, then tell me the current time"
        
        // TODO: Test query combining math AND weather
        // "What's 25 + 17? Also, what's the weather in London with metric units?"
        
        // TODO: Verify responses contain results from multiple tools
        
        fail("TODO: Implement multiple tools integration test");
    }

    /**
     * TODO 6.5: Advanced Tool Scenarios Test
     * <p>
     * Implement a test that explores advanced tool usage:
     * 1. Sequential tool usage (use result from one tool in another)
     * 2. Conditional tool usage (AI decides which tool to use)
     * 3. Tool chaining (multiple steps with tools)
     */
    @Test
    void advancedToolScenarios() {
        // TODO: Create OpenAI chat model with lower temperature for consistency
        // .temperature(0.1)
        
        // TODO: Create assistant with all tools
        
        // TODO: Test sequential tool usage
        // "First multiply 12 by 7, then set an alarm for that time in military format"
        
        // TODO: Test conditional tool usage
        // "If today is a weekday, tell me the weather in Tokyo. Otherwise, calculate 50 divided by 2."
        
        // TODO: Test tool chaining
        // "Calculate what year it will be in 10 years, then set an alarm for midnight of that year"
        
        // TODO: Verify complex interactions work as expected
        
        fail("TODO: Implement advanced tool scenarios test");
    }

    /**
     * TODO 6.6: Tool Error Handling Test (Optional Advanced Exercise)
     * <p>
     * Implement a test that verifies tools handle errors gracefully:
     * 1. Test division by zero with CalculatorTool
     * 2. Compare with normal calculations
     * 3. Verify the AI handles tool errors appropriately
     */
    @Test
    void toolErrorHandling() {
        // TODO: Create OpenAI chat model
        
        // TODO: Create assistant with CalculatorTool
        
        // TODO: Test division by zero
        // "What is 10 divided by 0?"
        
        // TODO: Test normal division for comparison
        // "What is 10 divided by 2?"
        
        // TODO: Verify error responses are handled gracefully
        
        fail("TODO: Implement tool error handling test");
    }

    /*
     * IMPLEMENTATION HINTS:
     * 
     * 1. Tool Method Signatures:
     *    - Use @Tool("description") annotation
     *    - Method can be public or package-private
     *    - Parameters should be simple types (String, int, double, boolean)
     *    - Return values should be String for text responses
     * 
     * 2. AiServices Configuration:
     *    Assistant assistant = AiServices.builder(Assistant.class)
     *            .chatModel(model)
     *            .tools(tool1, tool2, tool3)  // Add multiple tools
     *            .build();
     * 
     * 3. Testing Strategy:
     *    - Use assertAll() for grouping related assertions
     *    - Use assertThat() from AssertJ for string content verification
     *    - Test that responses contain expected keywords or values
     *    - Don't test exact wording (AI responses can vary)
     * 
     * 4. Sample Tool Implementation:
     *    @Tool("Add two numbers")
     *    public double add(double a, double b) {
     *        return a + b;
     *    }
     * 
     * 5. Error Handling in Tools:
     *    if (condition) {
     *        throw new IllegalArgumentException("Error message");
     *    }
     * 
     * Remember: The AI model will automatically discover and call your tools
     * based on the user's questions. You don't need to explicitly invoke them!
     */
}