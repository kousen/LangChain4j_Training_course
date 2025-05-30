package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
 */
class AiToolsTests {

    /**
     * DateTimeTool provides date and time related functionality for AI assistants.
     * This demonstrates the basic pattern for creating AI tools.
     */
    static class DateTimeTool {
        private static final Logger logger = LoggerFactory.getLogger(DateTimeTool.class);

        @Tool("Get the current date and time")
        String getCurrentDateTime() {
            logger.info("Getting current date and time");
            return LocalDateTime.now().toString();
        }

        @Tool("Get the date that is a specified number of years from now")
        String getDateYearsFromNow(int years) {
            logger.info("Calculating date {} years from now", years);
            return LocalDate.now().plusYears(years).toString();
        }

        @Tool("Set an alarm for a specific time")
        String setAlarm(String time) {
            logger.info("Setting alarm for {}", time);
            // In a real implementation, this would actually set an alarm
            return "Alarm set for " + time;
        }
    }

    /**
     * WeatherTool demonstrates tools with parameters.
     * Shows how AI can call tools with specific arguments.
     */
    static class WeatherTool {
        @Tool("Get the current weather for a specific city")
        String getCurrentWeather(String city, String units) {
            // In a real implementation, this would call a weather API
            return String.format("The current weather in %s is 22°%s and sunny", 
                    city, units.equals("metric") ? "C" : "F");
        }
    }

    /**
     * CalculatorTool demonstrates multiple related tools in one class.
     */
    static class CalculatorTool {
        @Tool("Add two numbers")
        double add(double a, double b) {
            return a + b;
        }
        
        @Tool("Multiply two numbers")
        double multiply(double a, double b) {
            return a * b;
        }
        
        @Tool("Divide two numbers")
        double divide(double a, double b) {
            if (b == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            return a / b;
        }
    }

    /**
     * Assistant interface for AI services with tool integration.
     */
    interface Assistant {
        String chat(String message);
    }

    /**
     * Test 6.1: Basic Tool Usage with DateTimeTool
     * <p>
     * Demonstrates how to create and use a simple tool with AiServices.
     */
    @Test
    void useBasicDateTimeTool() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create assistant with DateTimeTool
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new DateTimeTool())
                .build();

        System.out.println("=== Basic Tool Usage Test ===");
        
        // Test current date/time functionality
        String response1 = assistant.chat("What is the current date and time?");
        System.out.println("Current time response: " + response1);

        // Test future date calculation
        String response2 = assistant.chat("What year will it be in 5 years?");
        System.out.println("Future year response: " + response2);

        // Test alarm setting
        String response3 = assistant.chat("Set an alarm for 8:00 AM tomorrow");
        System.out.println("Alarm response: " + response3);
        
        System.out.println("=" * 50);

        // Verify all responses are not null and contain meaningful content
        assertAll("Basic tool usage validation",
            () -> assertNotNull(response1, "Current time response should not be null"),
            () -> assertNotNull(response2, "Future year response should not be null"),
            () -> assertNotNull(response3, "Alarm response should not be null"),
            () -> assertFalse(response1.trim().isEmpty(), "Current time response should not be empty"),
            () -> assertFalse(response2.trim().isEmpty(), "Future year response should not be empty"),
            () -> assertFalse(response3.trim().isEmpty(), "Alarm response should not be empty")
        );

        // Verify specific content using AssertJ
        assertThat(response2)
                .as("Future year calculation")
                .containsAnyOf("2029", "2030", "year")
                .hasSizeGreaterThan(10);
                
        assertThat(response3)
                .as("Alarm setting response")
                .containsIgnoringCase("alarm")
                .containsAnyOf("8:00", "8 AM", "tomorrow");
    }

    /**
     * Test 6.2: Tools with Parameters  
     * <p>
     * Demonstrates how tools can accept parameters for dynamic functionality.
     */
    @Test
    void useToolsWithParameters() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create assistant with WeatherTool
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new WeatherTool())
                .build();

        System.out.println("=== Tools with Parameters Test ===");
        
        // Test weather query with specific city and units
        String response1 = assistant.chat("What's the weather like in Paris? Use metric units.");
        System.out.println("Paris weather (metric): " + response1);

        // Test weather query with different city and units
        String response2 = assistant.chat("How about the weather in New York with Fahrenheit?");
        System.out.println("New York weather (Fahrenheit): " + response2);
        
        System.out.println("=" * 50);

        // Verify responses contain expected information
        assertAll("Weather tool parameter validation",
            () -> assertNotNull(response1, "Paris weather response should not be null"),
            () -> assertNotNull(response2, "New York weather response should not be null")
        );

        // Verify location-specific responses using AssertJ
        assertThat(response1)
                .as("Paris weather response")
                .containsIgnoringCase("paris")
                .containsAnyOf("22°C", "22°", "celsius", "metric");
                
        assertThat(response2)
                .as("New York weather response")  
                .containsAnyOf("new york", "york")
                .containsAnyOf("22°F", "22°", "fahrenheit");
    }

    /**
     * Test 6.3: Multiple Tools Integration
     * <p>
     * Demonstrates how to combine multiple tools for comprehensive AI functionality.
     */
    @Test
    void useMultipleTools() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create assistant with multiple tools
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new DateTimeTool(), new CalculatorTool(), new WeatherTool())
                .build();

        System.out.println("=== Multiple Tools Integration Test ===");
        
        // Test complex query requiring multiple tools
        String response1 = assistant.chat("What's 15 multiplied by 8, and what year will it be in 3 years?");
        System.out.println("Math and date response: " + response1);

        // Test another multi-tool query
        String response2 = assistant.chat("Calculate 100 divided by 4, then tell me the current time");
        System.out.println("Division and time response: " + response2);

        // Test weather and calculation combination
        String response3 = assistant.chat("What's 25 + 17? Also, what's the weather in London with metric units?");
        System.out.println("Math and weather response: " + response3);
        
        System.out.println("=" * 50);

        // Verify all multi-tool responses
        assertAll("Multiple tools integration validation",
            () -> assertNotNull(response1, "Math and date response should not be null"),
            () -> assertNotNull(response2, "Division and time response should not be null"),
            () -> assertNotNull(response3, "Math and weather response should not be null")
        );

        // Verify specific tool usage using AssertJ
        assertThat(response1)
                .as("Math and date combination")
                .containsAnyOf("120", "15", "8", "multiply")
                .containsAnyOf("2027", "2028", "year", "years");
                
        assertThat(response2)
                .as("Division and time combination")
                .containsAnyOf("25", "100", "4", "divide")
                .hasSizeGreaterThan(20);
                
        assertThat(response3)
                .as("Math and weather combination")
                .containsAnyOf("42", "25", "17", "add")
                .containsAnyOf("london", "weather", "22°C");
    }

    /**
     * Test 6.4: Advanced Tool Scenarios
     * <p>
     * Demonstrates advanced tool usage patterns and error handling.
     */
    @Test
    void advancedToolScenarios() {
        // Create OpenAI chat model with specific configuration
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.1) // Lower temperature for more consistent tool usage
                .build();

        // Create assistant with all tools
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new DateTimeTool(), new CalculatorTool(), new WeatherTool())
                .build();

        System.out.println("=== Advanced Tool Scenarios Test ===");
        
        // Test sequential tool usage
        String response1 = assistant.chat("First multiply 12 by 7, then set an alarm for that time in military format");
        System.out.println("Sequential tools response: " + response1);

        // Test conditional tool usage
        String response2 = assistant.chat("If today is a weekday, tell me the weather in Tokyo. Otherwise, calculate 50 divided by 2.");
        System.out.println("Conditional tools response: " + response2);

        // Test tool chaining
        String response3 = assistant.chat("Calculate what year it will be in 10 years, then set an alarm for midnight of that year");
        System.out.println("Tool chaining response: " + response3);
        
        System.out.println("=" * 50);

        // Verify advanced scenarios work
        assertAll("Advanced tool scenarios validation",
            () -> assertNotNull(response1, "Sequential tools response should not be null"),
            () -> assertNotNull(response2, "Conditional tools response should not be null"),
            () -> assertNotNull(response3, "Tool chaining response should not be null"),
            () -> assertFalse(response1.trim().isEmpty(), "Sequential tools response should not be empty"),
            () -> assertFalse(response2.trim().isEmpty(), "Conditional tools response should not be empty"),
            () -> assertFalse(response3.trim().isEmpty(), "Tool chaining response should not be empty")
        );

        // Verify complex interactions using AssertJ
        assertThat(response1)
                .as("Sequential tool usage")
                .containsAnyOf("84", "12", "7", "multiply", "alarm")
                .hasSizeGreaterThan(15);
                
        assertThat(response3)
                .as("Tool chaining")
                .containsAnyOf("2034", "2035", "10", "year", "alarm", "midnight")
                .hasSizeGreaterThan(20);
    }

    /**
     * Test 6.5: Tool Error Handling
     * <p>
     * Demonstrates how tools handle errors and edge cases gracefully.
     */
    @Test
    void toolErrorHandling() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create assistant with calculator tool (which has division by zero protection)
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new CalculatorTool())
                .build();

        System.out.println("=== Tool Error Handling Test ===");
        
        // Test division by zero handling
        String response1 = assistant.chat("What is 10 divided by 0?");
        System.out.println("Division by zero response: " + response1);

        // Test normal calculation for comparison
        String response2 = assistant.chat("What is 10 divided by 2?");
        System.out.println("Normal division response: " + response2);
        
        System.out.println("=" * 50);

        // Verify error handling responses
        assertAll("Tool error handling validation",
            () -> assertNotNull(response1, "Division by zero response should not be null"),
            () -> assertNotNull(response2, "Normal division response should not be null"),
            () -> assertFalse(response1.trim().isEmpty(), "Division by zero response should not be empty"),
            () -> assertFalse(response2.trim().isEmpty(), "Normal division response should not be empty")
        );

        // Verify error handling behavior using AssertJ
        assertThat(response1)
                .as("Division by zero handling")
                .containsAnyOf("error", "cannot", "zero", "undefined", "impossible")
                .hasSizeGreaterThan(10);
                
        assertThat(response2)
                .as("Normal division result")
                .containsAnyOf("5", "5.0", "five")
                .hasSizeGreaterThan(1);
    }
}