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

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 3: Structured Data Extraction
 * <p>
 * This lab demonstrates how to extract structured data from AI responses using LangChain4j.
 * You'll learn how to:
 * - Use JSON response format to get structured data
 * - Parse JSON responses manually with Jackson ObjectMapper
 * - Use AiServices with type-safe interfaces for automatic data extraction
 * - Handle lists and complex nested structures
 * - Work with annotations for structured prompts and variable substitution
 */
class StructuredDataExtractionTests {

    /**
     * Test 3.1: JSON Response Format
     * <p>
     * TODO: Implement structured data extraction using JSON response format
     * 1. Create an OpenAI chat model with JSON response format
     * 2. Create a prompt requesting specific JSON structure
     * 3. Send the prompt and get JSON response
     * 4. Parse JSON manually using Jackson ObjectMapper
     * 5. Verify the parsed data structure
     */
    @Test
    void extractActorFilms() throws JsonProcessingException {
        // TODO: Create OpenAI chat model with JSON response format
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .responseFormat("json_object")
        //         .build();

        // TODO: Create prompt for structured JSON response
        // String prompt = """
        //         Generate the filmography for a random actor in the following JSON format:
        //         {
        //             "actor": "Actor Name",
        //             "movies": ["Movie 1", "Movie 2", "Movie 3", "Movie 4", "Movie 5"]
        //         }
        //         """;

        // TODO: Generate JSON response
        // String response = model.chat(prompt);
        // System.out.println("JSON Response: " + response);

        // TODO: Parse JSON manually using Jackson
        // ObjectMapper objectMapper = new ObjectMapper();
        // ActorFilms actorFilms = objectMapper.readValue(response, ActorFilms.class);

        // TODO: Verify the parsed data
        // assertNotNull(response, "Response should not be null");
        // assertTrue(response.contains("actor"), "Response should contain 'actor' field");
        // assertTrue(response.contains("movies"), "Response should contain 'movies' field");
        // assertNotNull(actorFilms, "Parsed ActorFilms should not be null");
        // assertNotNull(actorFilms.actor(), "Actor name should not be null");
        // assertNotNull(actorFilms.movies(), "Movies list should not be null");
        // assertEquals(5, actorFilms.movies().size(), "Should have exactly 5 movies");
    }

    /**
     * Wrapper record for multiple actor filmographies.
     * This helps with JSON parsing when returning multiple entities.
     */
    record ActorFilmographies(List<ActorFilms> filmographies) {}

    /**
     * Interface for AI service that extracts actor filmographies.
     * This demonstrates type-safe AI service interfaces.
     */
    interface ActorService {
        @SystemMessage("You are a movie database expert.")
        ActorFilms getActorFilmography(@UserMessage String actorName);
        
        @SystemMessage("You are a comprehensive movie database expert. Provide accurate filmographies.")
        ActorFilmographies getMultipleActorFilmographies(@UserMessage String actors);
    }

    /**
     * Test 3.2: AiServices with Structured Data
     * <p>
     * TODO: Implement structured data extraction using AiServices
     * 1. Create an OpenAI chat model
     * 2. Build an AiServices instance with ActorService interface
     * 3. Call the service method to get structured data
     * 4. Verify the returned ActorFilms object
     * 5. Print the structured data
     */
    @Test
    void extractActorFilmsWithAiServices() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create AiServices instance
        // ActorService service = AiServices.builder(ActorService.class)
        //         .chatModel(model)
        //         .build();

        // TODO: Extract structured data
        // ActorFilms actorFilms = service.getActorFilmography(
        //     "Generate filmography for a random famous actor with exactly 5 movies"
        // );

        // TODO: Verify results
        // assertNotNull(actorFilms, "ActorFilms should not be null");
        // assertNotNull(actorFilms.actor(), "Actor name should not be null");
        // assertNotNull(actorFilms.movies(), "Movies list should not be null");
        // assertEquals(5, actorFilms.movies().size(), "Should have exactly 5 movies");

        // TODO: Print results
        // System.out.println("Actor: " + actorFilms.actor());
        // actorFilms.movies().forEach(movie -> System.out.println("- " + movie));
    }

    /**
     * Test 3.3: Multiple Entity Extraction
     * <p>
     * TODO: Implement extraction of multiple structured entities
     * 1. Use the same ActorService from previous test
     * 2. Call getMultipleActorFilmographies with multiple actors
     * 3. Verify you get a list of ActorFilms objects
     * 4. Verify each object has proper structure
     * 5. Print all results
     */
    @Test
    void extractMultipleActorFilmographies() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create AiServices instance
        // ActorService service = AiServices.builder(ActorService.class)
        //         .chatModel(model)
        //         .build();

        // TODO: Extract multiple filmographies
        // ActorFilmographies result = service.getMultipleActorFilmographies(
        //     "Return filmographies for exactly 3 different famous actors with 4 movies each");

        // TODO: Verify results
        // List<ActorFilms> filmographies = result.filmographies();
        // assertNotNull(result, "Result should not be null");
        // assertNotNull(filmographies, "Filmographies list should not be null");
        // assertEquals(3, filmographies.size(), "Should have exactly 3 actor filmographies");

        // TODO: Print results
        // filmographies.forEach(actorFilms -> {
        //     System.out.println("Actor: " + actorFilms.actor());
        //     actorFilms.movies().forEach(movie -> System.out.println("  - " + movie));
        //     System.out.println();
        // });
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
     * TODO: Implement extraction using parameterized prompts with @V annotation
     * 1. Create an OpenAI chat model
     * 2. Build AdvancedActorService with AiServices
     * 3. Test variable substitution with specific actor and movie count
     * 4. Verify the data matches the requested parameters
     * 5. Print comprehensive results
     */
    @Test
    void advancedStructuredDataExtraction() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create advanced AI service
        // AdvancedActorService service = AiServices.builder(AdvancedActorService.class)
        //         .chatModel(model)
        //         .build();

        // TODO: Test with specific actor and movie count
        // String actorName = "Tom Hanks";
        // int movieCount = 6;
        // ActorFilms actorFilms = service.getSpecificActorFilmography(actorName, movieCount);

        // TODO: Verify the extracted data
        // assertNotNull(actorFilms, "ActorFilms should not be null");
        // assertNotNull(actorFilms.actor(), "Actor name should not be null");
        // assertTrue(actorFilms.actor().toLowerCase().contains("hanks"), "Actor should be Tom Hanks");
        // assertEquals(movieCount, actorFilms.movies().size(), "Should have exactly " + movieCount + " movies");

        // TODO: Print results
        // System.out.println("Advanced Extraction Result:");
        // System.out.println("Requested actor: " + actorName);
        // System.out.println("Actual actor: " + actorFilms.actor());
        // System.out.println("Movie count: " + actorFilms.movies().size());
        // actorFilms.movies().forEach(movie -> System.out.println("- " + movie));
    }
}