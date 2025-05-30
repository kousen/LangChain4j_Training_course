package com.kousenit.langchain4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 3: Structured Data Extraction
 * <p>
 * This lab demonstrates how to extract structured data from AI responses using LangChain4j.
 * You'll learn how to:
 * - Extract JSON data manually from AI responses
 * - Use AiServices for automatic structured data extraction
 * - Define service interfaces with annotations for type-safe AI interactions
 * - Work with complex data structures like lists and records
 */
class StructuredDataExtractionTests {

    /**
     * Test 3.1: Single Entity Extraction with Manual JSON Parsing
     * <p>
     * Demonstrates extracting structured data using JSON response format
     * and manual parsing with Jackson ObjectMapper.
     */
    @Test
    void extractActorFilms() throws JsonProcessingException {
        // Create OpenAI chat model with JSON response format
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .responseFormat("json_object")
                .build();

        // Create prompt requesting JSON format
        String prompt = """
                Generate the filmography for a random actor in the following JSON format:
                {
                    "actor": "Actor Name",
                    "movies": ["Movie 1", "Movie 2", "Movie 3", "Movie 4", "Movie 5"]
                }
                """;

        // Get JSON response from the model
        String response = model.chat(prompt);
        System.out.println("JSON Response: " + response);

        // Parse JSON manually using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ActorFilms actorFilms = objectMapper.readValue(response, ActorFilms.class);

        System.out.println("Parsed Actor: " + actorFilms.actor());
        System.out.println("Movie count: " + actorFilms.movies().size());
        actorFilms.movies().forEach(movie -> System.out.println("- " + movie));

        // Verify the parsed data using hybrid approach
        assertAll("JSON parsing validation",
            () -> assertNotNull(response, "Response should not be null"),
            () -> assertTrue(response.contains("actor"), "Response should contain 'actor' field"),
            () -> assertTrue(response.contains("movies"), "Response should contain 'movies' field"),
            () -> assertNotNull(actorFilms, "Parsed ActorFilms should not be null")
        );
        
        // Use AssertJ for complex object validation where it excels
        assertThat(actorFilms)
                .as("Parsed ActorFilms structure")
                .satisfies(af -> {
                    assertThat(af.actor()).as("Actor name").isNotNull().isNotBlank();
                    assertThat(af.movies()).as("Movies list").hasSize(5);
                });
                
        // Verify each movie is not blank
        actorFilms.movies().forEach(movie -> 
            assertThat(movie).as("Individual movie").isNotBlank());
    }

    /**
     * Wrapper record for multiple actor filmographies.
     * This helps with JSON parsing when returning multiple entities.
     */
    record ActorFilmographies(List<ActorFilms> filmographies) {}

    /**
     * Interface for AI service that extracts actor filmography data.
     * Uses LangChain4j annotations for structured data extraction.
     */
    interface ActorService {
        @SystemMessage("You are a movie database expert.")
        ActorFilms getActorFilmography(@UserMessage String actorName);
        
        @SystemMessage("You are a comprehensive movie database expert. Provide accurate filmographies.")
        ActorFilmographies getMultipleActorFilmographies(@UserMessage String actors);
    }

    /**
     * Test 3.2: Using AiServices for Structured Data Extraction
     * <p>
     * Demonstrates automatic parsing of structured data using LangChain4j's AiServices.
     * This approach eliminates the need for manual JSON parsing.
     */
    @Test
    void extractActorFilmsWithAiServices() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create AI service using the interface
        ActorService service = AiServices.builder(ActorService.class)
                .chatModel(model)
                .build();

        // Request filmography for a random actor
        ActorFilms actorFilms = service.getActorFilmography(
            "Generate filmography for a random famous actor with exactly 5 movies"
        );

        // Display results
        System.out.println("AiServices Result:");
        System.out.println("Actor: " + actorFilms.actor());
        System.out.println("Movies (" + actorFilms.movies().size() + "):");
        actorFilms.movies().forEach(movie -> System.out.println("- " + movie));

        // Verify the extracted data using hybrid approach
        assertAll("AiServices extraction validation",
            () -> assertNotNull(actorFilms, "ActorFilms should not be null"),
            () -> assertNotNull(actorFilms.actor(), "Actor name should not be null"),
            () -> assertFalse(actorFilms.actor().trim().isEmpty(), "Actor name should not be empty"),
            () -> assertEquals(5, actorFilms.movies().size(), "Should have exactly 5 movies")
        );
        
        // Verify each movie is not empty using AssertJ's fluent API
        actorFilms.movies().forEach(movie -> 
            assertThat(movie).as("Individual movie").isNotBlank());
    }

    /**
     * Test 3.3: Extracting Multiple Actor Filmographies
     * <p>
     * Demonstrates extracting a list of structured data objects.
     * Shows how AI can return multiple entities in a single response.
     */
    @Test
    void extractMultipleActorFilmographies() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create AI service
        ActorService service = AiServices.builder(ActorService.class)
                .chatModel(model)
                .build();

        // Request filmographies for multiple actors
        ActorFilmographies result = service.getMultipleActorFilmographies(
            """
            Return a JSON object with a 'filmographies' field containing
            an array of exactly 3 different famous actors. Each actor
            should have exactly 4 movies. Format each actor as an object
            with 'actor' and 'movies' fields."""
        );

        List<ActorFilms> filmographies = result.filmographies();

        // Display results
        System.out.println("Multiple Actor Filmographies:");
        IntStream.range(0, filmographies.size()).forEach(i -> {
            ActorFilms actorFilms = filmographies.get(i);
            System.out.println((i + 1) + ". " + actorFilms.actor() + ":");
            actorFilms.movies().forEach(movie -> System.out.println("   - " + movie));
        });

        // Verify the extracted data using hybrid approach
        assertAll("Multiple filmographies validation",
            () -> assertNotNull(result, "Result should not be null"),
            () -> assertNotNull(filmographies, "Filmographies list should not be null"),
            () -> assertEquals(3, filmographies.size(), "Should have exactly 3 actor filmographies")
        );
        
        // Use AssertJ for complex validation of each filmography
        filmographies.forEach(actorFilms -> {
            assertThat(actorFilms)
                    .as("Individual ActorFilms")
                    .isNotNull()
                    .satisfies(af -> {
                        assertThat(af.actor()).as("Actor name").isNotNull().isNotBlank();
                        assertThat(af.movies()).as("Actor's movies").hasSize(4);
                    });
            
            // Verify each movie is not blank
            actorFilms.movies().forEach(movie -> 
                assertThat(movie).as("Individual movie").isNotBlank());
        });
    }

    /**
     * Advanced interface for more complex actor data extraction.
     * Demonstrates using @V annotation for variable substitution in prompts.
     */
    interface AdvancedActorService {
        @SystemMessage("You are an expert movie database assistant specializing in actor filmographies.")
        @UserMessage("Generate filmography for {{actorName}} with exactly {{movieCount}} of their most famous movies")
        ActorFilms getSpecificActorFilmography(
            @V("actorName") String actorName,
            @V("movieCount") int movieCount
        );
    }

    /**
     * Test 3.4: Advanced Structured Data Extraction with Variable Substitution
     * <p>
     * Demonstrates using parameterized prompts with @V annotation for variable substitution.
     * Shows how to create more flexible AI services with dynamic prompt generation.
     */
    @Test
    void advancedStructuredDataExtraction() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create advanced AI service
        AdvancedActorService service = AiServices.builder(AdvancedActorService.class)
                .chatModel(model)
                .build();

        // Test with specific actor and movie count
        String actorName = "Tom Hanks";
        int movieCount = 6;
        
        ActorFilms actorFilms = service.getSpecificActorFilmography(actorName, movieCount);

        // Display results
        System.out.println("Advanced Extraction Result:");
        System.out.println("Requested actor: " + actorName);
        System.out.println("Requested movie count: " + movieCount);
        System.out.println("Actual actor: " + actorFilms.actor());
        System.out.println("Actual movie count: " + actorFilms.movies().size());
        System.out.println("Movies:");
        actorFilms.movies().forEach(movie -> System.out.println("- " + movie));

        // Verify the extracted data using hybrid approach
        assertAll("Advanced extraction validation",
            () -> assertNotNull(actorFilms, "ActorFilms should not be null"),
            () -> assertNotNull(actorFilms.actor(), "Actor name should not be null"),
            () -> assertEquals(movieCount, actorFilms.movies().size(), 
                             "Should have exactly " + movieCount + " movies")
        );
        
        // Use AssertJ for string content validation where it excels
        assertThat(actorFilms.actor())
                .as("Actor name validation")
                .containsIgnoringCase("hanks");
                
        // Verify each movie is not empty
        actorFilms.movies().forEach(movie -> 
            assertThat(movie).as("Individual movie").isNotBlank());
    }
}