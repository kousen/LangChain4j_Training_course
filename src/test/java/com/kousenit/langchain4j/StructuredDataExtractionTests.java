package com.kousenit.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 3: Structured Data Extraction
 * <p>
 * This lab demonstrates how to extract structured data from AI responses using LangChain4j.
 * You'll learn how to:
 * - Use JSON response format to get structured data
 * - Parse JSON responses manually
 * - Use AiServices with type-safe interfaces for automatic data extraction
 * - Handle lists and complex nested structures
 * - Work with annotations for structured prompts
 */
class StructuredDataExtractionTests {

    /**
     * Test 3.1: JSON Response Format
     * <p>
     * TODO: Implement structured data extraction using JSON response format
     * 1. Create an OpenAI chat model with JSON response format
     * 2. Create a prompt requesting specific JSON structure
     * 3. Send the prompt and get JSON response
     * 4. Verify the response contains expected JSON fields
     * 5. Print the JSON for manual inspection
     */
    @Test
    void extractActorFilmsAsJson() {
        // TODO: Create OpenAI chat model with JSON response format
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
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
        // String response = model.generate(prompt);
        // System.out.println("JSON Response: " + response);

        // TODO: Verify JSON structure
        // assertNotNull(response);
        // assertTrue(response.contains("actor"));
        // assertTrue(response.contains("movies"));
        // assertTrue(response.startsWith("{"));
        // assertTrue(response.endsWith("}"));
    }

    /**
     * Interface for AI service that extracts actor filmographies.
     * This demonstrates type-safe AI service interfaces.
     */
    interface ActorService {
        @dev.langchain4j.service.SystemMessage("You are a movie database expert.")
        ActorFilms getActorFilmography(@dev.langchain4j.service.UserMessage String actorName);
        
        List<ActorFilms> getMultipleActorFilmographies(@dev.langchain4j.service.UserMessage String actors);
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
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create AiServices instance
        // ActorService service = AiServices.builder(ActorService.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Extract structured data
        // ActorFilms actorFilms = service.getActorFilmography("Generate filmography for a random famous actor with exactly 5 movies");

        // TODO: Verify results
        // assertNotNull(actorFilms);
        // assertNotNull(actorFilms.actor());
        // assertNotNull(actorFilms.movies());
        // assertEquals(5, actorFilms.movies().size());

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
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create AiServices instance
        // ActorService service = AiServices.builder(ActorService.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Extract multiple filmographies
        // List<ActorFilms> filmographies = service.getMultipleActorFilmographies(
        //     "Generate filmographies for Tom Hanks and Meryl Streep, with 3 movies each");

        // TODO: Verify results
        // assertNotNull(filmographies);
        // assertFalse(filmographies.isEmpty());
        // assertTrue(filmographies.size() >= 2);

        // TODO: Print results
        // filmographies.forEach(actorFilms -> {
        //     System.out.println("Actor: " + actorFilms.actor());
        //     actorFilms.movies().forEach(movie -> System.out.println("  - " + movie));
        //     System.out.println();
        // });
    }

    /**
     * More complex service interface demonstrating various data types.
     */
    interface MovieAnalysisService {
        @dev.langchain4j.service.SystemMessage("You are a film critic and movie database expert.")
        @dev.langchain4j.service.UserMessage("Analyze the movie {{movieTitle}} and provide a rating from 1-10")
        int getMovieRating(@V("movieTitle") String movieTitle);
        
        @dev.langchain4j.service.UserMessage("List the main genres for the movie {{movieTitle}}")
        List<String> getMovieGenres(@V("movieTitle") String movieTitle);
        
        @dev.langchain4j.service.UserMessage("Provide a brief analysis of {{movieTitle}} including rating (1-10), genres, and a short review")
        MovieAnalysis getCompleteAnalysis(@V("movieTitle") String movieTitle);
    }

    /**
     * Record for complex movie analysis data.
     */
    record MovieAnalysis(String title, int rating, List<String> genres, String review) {}

    /**
     * Test 3.4: Complex Structured Data with Multiple Types
     * <p>
     * TODO: Implement extraction of complex structured data with different types
     * 1. Create an OpenAI chat model
     * 2. Build MovieAnalysisService with AiServices
     * 3. Test different return types: int, List<String>, and complex record
     * 4. Verify all data types are handled correctly
     * 5. Print comprehensive results
     */
    @Test
    void extractComplexMovieData() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create MovieAnalysisService
        // MovieAnalysisService service = AiServices.builder(MovieAnalysisService.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Test different data extraction methods
        // String movieTitle = "The Matrix";
        
        // int rating = service.getMovieRating(movieTitle);
        // List<String> genres = service.getMovieGenres(movieTitle);
        // MovieAnalysis analysis = service.getCompleteAnalysis(movieTitle);

        // TODO: Verify results
        // assertTrue(rating >= 1 && rating <= 10);
        // assertNotNull(genres);
        // assertFalse(genres.isEmpty());
        // assertNotNull(analysis);
        // assertNotNull(analysis.title());
        // assertTrue(analysis.rating() >= 1 && analysis.rating() <= 10);
        // assertNotNull(analysis.genres());
        // assertNotNull(analysis.review());

        // TODO: Print results
        // System.out.println("Movie Rating: " + rating);
        // System.out.println("Genres: " + genres);
        // System.out.println("\nComplete Analysis:");
        // System.out.println("Title: " + analysis.title());
        // System.out.println("Rating: " + analysis.rating());
        // System.out.println("Genres: " + analysis.genres());
        // System.out.println("Review: " + analysis.review());
    }
}