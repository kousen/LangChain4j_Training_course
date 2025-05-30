package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 4: AI Services Interface
 * <p>
 * This lab demonstrates how to create high-level AI service interfaces using LangChain4j.
 * You'll learn how to:
 * - Define service interfaces with annotations for type-safe AI interactions
 * - Use AiServices to automatically implement interfaces
 * - Combine memory and tools with AI services
 * - Create sophisticated AI-powered service layers
 */
class AiServicesTests {

    /**
     * Interface for filmography service that provides movie information.
     */
    interface FilmographyService {
        @dev.langchain4j.service.SystemMessage("You are a helpful assistant that provides accurate information about actors and their movies.")
        List<String> getMovies(@dev.langchain4j.service.UserMessage String actor);
        
        @dev.langchain4j.service.SystemMessage("You are a movie expert. Provide detailed analysis.")
        String analyzeActor(@dev.langchain4j.service.UserMessage String actorName);
        
        ActorFilms getFullFilmography(String actorName);
    }

    /**
     * Test 4.1: Create and Use a Service Interface
     * <p>
     * TODO: Implement a filmography service using AiServices
     * 1. Create an OpenAI chat model
     * 2. Build FilmographyService using AiServices.builder()
     * 3. Test getting movies for an actor
     * 4. Test getting actor analysis
     * 5. Verify the responses are not null and contain expected content
     */
    @Test
    void useFilmographyService() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create FilmographyService using AiServices
        // FilmographyService service = AiServices.builder(FilmographyService.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Test simple movie list
        // List<String> tomHanksMovies = service.getMovies("Tom Hanks");
        // System.out.println("Tom Hanks movies: " + tomHanksMovies);
        
        // TODO: Test actor analysis
        // String analysis = service.analyzeActor("Meryl Streep");
        // System.out.println("Meryl Streep analysis: " + analysis);

        // TODO: Verify results
        // assertNotNull(tomHanksMovies);
        // assertFalse(tomHanksMovies.isEmpty());
        // assertNotNull(analysis);
        // assertFalse(analysis.trim().isEmpty());
    }

    /**
     * Interface for personal assistant that can chat.
     */
    interface PersonalAssistant {
        String chat(String message);
    }

    /**
     * Test 4.2: Service with Memory and Tools
     * <p>
     * TODO: Implement an advanced service that combines memory and tools
     * 1. Create an OpenAI chat model
     * 2. Create a ChatMemory instance
     * 3. Build PersonalAssistant with AiServices including memory and DateTimeTool
     * 4. Have a conversation that uses both memory and tools
     * 5. Verify the assistant remembers previous context and can use tools
     */
    @Test
    void personalAssistantWithMemoryAndTools() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create chat memory
        // ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // TODO: Create PersonalAssistant with memory and tools
        // PersonalAssistant assistant = AiServices.builder(PersonalAssistant.class)
        //         .chatLanguageModel(model)
        //         .chatMemory(memory)
        //         .tools(new DateTimeTool())
        //         .build();

        // TODO: Have a conversation that uses both memory and tools
        // String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer.");
        // System.out.println("Response 1: " + response1);

        // String response2 = assistant.chat("What's my name and what year will it be in 3 years?");
        // System.out.println("Response 2: " + response2);

        // TODO: Verify memory and tool usage
        // assertTrue(response2.toLowerCase().contains("alice"));
        // assertNotNull(response2);
        // assertFalse(response2.trim().isEmpty());
    }

    /**
     * Interface for document analysis service.
     */
    interface DocumentAnalyzer {
        @dev.langchain4j.service.SystemMessage("You are an expert document analyzer. Provide concise, accurate analysis.")
        @dev.langchain4j.service.UserMessage("Analyze this document content and provide key insights: {{content}}")
        String analyzeDocument(@V("content") String documentContent);
        
        @dev.langchain4j.service.UserMessage("Extract the main themes from: {{content}}")
        List<String> extractThemes(@V("content") String documentContent);
        
        @dev.langchain4j.service.UserMessage("Rate the sentiment of this content from 1-10: {{content}}")
        int analyzeSentiment(@V("content") String documentContent);
    }

    /**
     * Test 4.3: Advanced Service Configuration
     * <p>
     * TODO: Implement a sophisticated document analysis service
     * 1. Create an OpenAI chat model with custom temperature
     * 2. Build DocumentAnalyzer using AiServices
     * 3. Test document analysis with sample content
     * 4. Test theme extraction
     * 5. Test sentiment analysis
     * 6. Verify all return types work correctly (String, List<String>, int)
     */
    @Test
    void advancedServiceConfiguration() {
        // TODO: Create OpenAI chat model with custom configuration
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .temperature(0.3)  // Lower temperature for more consistent analysis
        //         .build();

        // TODO: Create DocumentAnalyzer using AiServices
        // DocumentAnalyzer analyzer = AiServices.builder(DocumentAnalyzer.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Prepare sample content for analysis
        // String sampleContent = """
        //         The quarterly earnings report shows strong growth in the technology sector,
        //         with cloud computing services leading the way. Customer satisfaction remains high,
        //         though there are concerns about increasing competition and market saturation.
        //         """;

        // TODO: Test all service methods
        // String analysis = analyzer.analyzeDocument(sampleContent);
        // List<String> themes = analyzer.extractThemes(sampleContent);
        // int sentiment = analyzer.analyzeSentiment(sampleContent);

        // TODO: Print results
        // System.out.println("Analysis: " + analysis);
        // System.out.println("Themes: " + themes);
        // System.out.println("Sentiment: " + sentiment);

        // TODO: Verify results
        // assertNotNull(analysis);
        // assertNotNull(themes);
        // assertFalse(themes.isEmpty());
        // assertTrue(sentiment >= 1 && sentiment <= 10);
    }

    /**
     * Interface for creative writing service with multiple data types.
     */
    interface CreativeWritingService {
        @dev.langchain4j.service.SystemMessage("You are a creative writing assistant specializing in {{genre}} fiction.")
        @dev.langchain4j.service.UserMessage("Write a {{wordCount}}-word story about {{topic}} in the {{genre}} genre")
        String writeStory(@V("genre") String genre, @V("topic") String topic, @V("wordCount") int wordCount);
        
        @dev.langchain4j.service.UserMessage("Generate {{count}} creative character names for a {{setting}} story")
        List<String> generateCharacterNames(@V("setting") String setting, @V("count") int count);
        
        @dev.langchain4j.service.UserMessage("Rate the creativity level of this story from 1-10: {{story}}")
        int rateCreativity(@V("story") String story);
    }

    /**
     * Test 4.4: Service with Variable Substitution
     * <p>
     * TODO: Implement a creative writing service with template variables
     * 1. Create an OpenAI chat model
     * 2. Build CreativeWritingService using AiServices
     * 3. Test story generation with different parameters
     * 4. Test character name generation
     * 5. Test creativity rating
     * 6. Verify all template variables are properly substituted
     */
    @Test
    void creativeWritingServiceWithVariables() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_O_MINI)
        //         .build();

        // TODO: Create CreativeWritingService
        // CreativeWritingService service = AiServices.builder(CreativeWritingService.class)
        //         .chatLanguageModel(model)
        //         .build();

        // TODO: Test story generation
        // String story = service.writeStory("science fiction", "time travel", 200);
        // System.out.println("Generated Story: " + story);

        // TODO: Test character name generation
        // List<String> characterNames = service.generateCharacterNames("medieval fantasy", 5);
        // System.out.println("Character Names: " + characterNames);

        // TODO: Test creativity rating
        // int creativity = service.rateCreativity(story);
        // System.out.println("Creativity Rating: " + creativity);

        // TODO: Verify results
        // assertNotNull(story);
        // assertFalse(story.trim().isEmpty());
        // assertNotNull(characterNames);
        // assertEquals(5, characterNames.size());
        // assertTrue(creativity >= 1 && creativity <= 10);
    }
}