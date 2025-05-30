package com.kousenit.langchain4j;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
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
 * Lab 4: AI Services Interface
 * <p>
 * This lab demonstrates how to create high-level AI services using LangChain4j's AiServices.
 * You'll learn how to:
 * - Define service interfaces with @SystemMessage and @UserMessage annotations
 * - Create type-safe AI-powered services
 * - Integrate memory and tools with AI services
 * - Use variable substitution with @V annotation for dynamic prompts
 */
class AiServicesTests {

    /**
     * Test 4.1: Create a Basic Service Interface
     * <p>
     * Demonstrates creating a simple AI service interface for movie-related queries.
     */
    @Test
    void useFilmographyService() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Define service interface
        interface FilmographyService {
            @SystemMessage("You are a helpful assistant that provides accurate information about actors and their movies.")
            List<String> getMovies(@UserMessage String actor);
            
            @SystemMessage("You are a movie expert. Provide detailed analysis.")
            String analyzeActor(@UserMessage String actorName);
            
            ActorFilms getFullFilmography(String actorName);
        }

        // Create AI service using the interface
        FilmographyService service = AiServices.builder(FilmographyService.class)
                .chatModel(model)
                .build();

        // Test simple movie list
        List<String> tomHanksMovies = service.getMovies("Tom Hanks");
        System.out.println("Tom Hanks movies: " + tomHanksMovies);
        
        // Test actor analysis
        String analysis = service.analyzeActor("Meryl Streep");
        System.out.println("Meryl Streep analysis: " + analysis);

        // Test structured data return
        ActorFilms fullFilmography = service.getFullFilmography("Generate filmography for Leonardo DiCaprio with 5 movies");
        System.out.println("Full filmography: " + fullFilmography.actor() + " - " + fullFilmography.movies().size() + " movies");

        // Verify results
        assertNotNull(tomHanksMovies, "Movie list should not be null");
        assertFalse(tomHanksMovies.isEmpty(), "Movie list should not be empty");
        assertNotNull(analysis, "Analysis should not be null");
        assertFalse(analysis.trim().isEmpty(), "Analysis should not be empty");
        assertNotNull(fullFilmography, "Full filmography should not be null");
        assertNotNull(fullFilmography.actor(), "Actor name should not be null");
        assertNotNull(fullFilmography.movies(), "Movies list should not be null");
    }

    /**
     * Test 4.2: Service with Memory and Tools
     * <p>
     * Demonstrates creating an AI service that combines memory and tools for persistent conversations.
     */
    @Test
    void personalAssistantWithMemoryAndTools() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create chat memory to maintain conversation context
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // Define personal assistant interface
        interface PersonalAssistant {
            String chat(String message);
        }

        // Create AI service with memory and tools
        PersonalAssistant assistant = AiServices.builder(PersonalAssistant.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(new DateTimeTool())
                .build();

        // Have a conversation that uses both memory and tools
        String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer.");
        System.out.println("Response 1: " + response1);

        String response2 = assistant.chat("What's my name and what year will it be in 3 years?");
        System.out.println("Response 2: " + response2);

        // Verify memory and tool usage
        assertNotNull(response1, "First response should not be null");
        assertNotNull(response2, "Second response should not be null");
        assertTrue(response2.toLowerCase().contains("alice"), 
                   "Response should remember the user's name from previous message");
        // The response should contain a future year, indicating tool usage
        assertTrue(response2.matches(".*202[7-9].*") || response2.matches(".*203[0-9].*"), 
                   "Response should contain a future year from DateTimeTool");
    }

    /**
     * Test 4.3: Advanced Service with Variable Substitution
     * <p>
     * Demonstrates using @V annotation for dynamic prompt variable substitution.
     */
    @Test
    void advancedServiceWithVariableSubstitution() {
        // Create OpenAI chat model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Define advanced service interface with variable substitution
        interface DocumentAnalyzer {
            @SystemMessage("You are an expert document analyzer. Provide concise, accurate analysis.")
            @UserMessage("Analyze this document content and provide key insights: {{content}}")
            String analyzeDocument(@V("content") String documentContent);
            
            @UserMessage("Extract the main themes from: {{content}}")
            List<String> extractThemes(@V("content") String documentContent);
            
            @UserMessage("Rate the sentiment of this content from 1-10: {{content}}")
            int analyzeSentiment(@V("content") String documentContent);
        }

        // Create AI service
        DocumentAnalyzer analyzer = AiServices.builder(DocumentAnalyzer.class)
                .chatModel(model)
                .build();

        // Test document with various data types
        String sampleContent = """
            The quarterly earnings report shows strong growth in the technology sector,
            with cloud computing services leading the way. Customer satisfaction remains high,
            though there are concerns about increasing competition and market saturation.
            """;

        String analysis = analyzer.analyzeDocument(sampleContent);
        List<String> themes = analyzer.extractThemes(sampleContent);
        int sentiment = analyzer.analyzeSentiment(sampleContent);

        System.out.println("Analysis: " + analysis);
        System.out.println("Themes: " + themes);
        System.out.println("Sentiment: " + sentiment);

        // Verify results
        assertNotNull(analysis, "Analysis should not be null");
        assertFalse(analysis.trim().isEmpty(), "Analysis should not be empty");
        assertNotNull(themes, "Themes should not be null");
        assertFalse(themes.isEmpty(), "Themes list should not be empty");
        assertTrue(sentiment >= 1 && sentiment <= 10, "Sentiment should be between 1 and 10");
    }

    /**
     * Test 4.4: Service Configuration with Temperature and Complex Types
     * <p>
     * Demonstrates advanced service configuration and handling complex return types.
     */
    @Test
    void advancedServiceConfiguration() {
        // Create OpenAI chat model with specific configuration
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.3)  // Lower temperature for more consistent analysis
                .build();

        // Define advanced movie analysis service
        interface MovieAnalysisService {
            @SystemMessage("You are a film critic and movie database expert.")
            @UserMessage("Analyze the movie {{movieTitle}} and provide a rating from 1-10")
            int getMovieRating(@V("movieTitle") String movieTitle);
            
            @UserMessage("List the main genres for the movie {{movieTitle}}")
            List<String> getMovieGenres(@V("movieTitle") String movieTitle);
            
            @UserMessage("Provide a brief analysis of {{movieTitle}} including rating (1-10), genres, and a short review")
            MovieAnalysis getCompleteAnalysis(@V("movieTitle") String movieTitle);
        }

        // Record for complex movie analysis data
        record MovieAnalysis(String title, int rating, List<String> genres, String review) {}

        // Create AI service
        MovieAnalysisService service = AiServices.builder(MovieAnalysisService.class)
                .chatModel(model)
                .build();

        // Test different data extraction methods
        String movieTitle = "The Matrix";
        
        int rating = service.getMovieRating(movieTitle);
        List<String> genres = service.getMovieGenres(movieTitle);
        MovieAnalysis analysis = service.getCompleteAnalysis(movieTitle);

        System.out.println("Movie Rating: " + rating);
        System.out.println("Genres: " + genres);
        System.out.println("\nComplete Analysis:");
        System.out.println("Title: " + analysis.title());
        System.out.println("Rating: " + analysis.rating());
        System.out.println("Genres: " + analysis.genres());
        System.out.println("Review: " + analysis.review());

        // Verify results
        assertTrue(rating >= 1 && rating <= 10, "Rating should be between 1 and 10");
        assertNotNull(genres, "Genres should not be null");
        assertFalse(genres.isEmpty(), "Genres should not be empty");
        assertNotNull(analysis, "Complete analysis should not be null");
        assertNotNull(analysis.title(), "Analysis title should not be null");
        assertTrue(analysis.rating() >= 1 && analysis.rating() <= 10, "Analysis rating should be between 1 and 10");
        assertNotNull(analysis.genres(), "Analysis genres should not be null");
        assertNotNull(analysis.review(), "Analysis review should not be null");
        assertFalse(analysis.review().trim().isEmpty(), "Analysis review should not be empty");
    }
}