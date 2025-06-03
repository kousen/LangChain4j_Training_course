package com.kousenit.langchain4j;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 4: Prompt Templates
 * <p>
 * This lab demonstrates how to use prompt templates with LangChain4j.
 * You'll learn how to:
 * - Create and use simple prompt templates with variable substitution
 * - Load templates from resource files  
 * - Use templates with AiServices annotations
 * - Work with different data types in template variables
 */
class PromptTemplateTests {

    /**
     * Test 4.1: Simple Prompt Template
     * <p>
     * TODO: Implement a basic prompt template with variable substitution
     * 1. Create an OpenAI chat model
     * 2. Create a PromptTemplate with {{variable}} placeholders
     * 3. Create a Map with variable values
     * 4. Apply the template to generate a Prompt
     * 5. Send the prompt to the model and verify the response
     */
    @Test
    void simplePromptTemplate() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create prompt template with variables
        // PromptTemplate template = PromptTemplate.from("Tell me {{count}} movies whose soundtrack was composed by {{composer}}");
        
        // TODO: Create variables map
        // Map<String, Object> variables = new HashMap<>();
        // variables.put("count", "5");
        // variables.put("composer", "John Williams");
        
        // TODO: Apply template and generate response
        // Prompt prompt = template.apply(variables);
        // String response = model.chat(prompt.text());

        // TODO: Verify and print response
        // System.out.println("Template Response: " + response);
        // assertNotNull(response);
        // assertFalse(response.trim().isEmpty());
    }

    /**
     * Test 4.2: Template from Resource File
     * <p>
     * TODO: Implement template loading from a resource file
     * 1. Create a template file at src/main/resources/movie_prompt.mustache
     * 2. Load the template content from the resource
     * 3. Create a PromptTemplate from the loaded content
     * 4. Apply variables and generate response
     * 5. Verify the response
     */
    @Test
    void promptTemplateFromResource() throws IOException {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Load template from resource file
        // String templateContent = new String(
        //     getClass().getClassLoader()
        //         .getResourceAsStream("movie_prompt.mustache")
        //         .readAllBytes()
        // );
        
        // TODO: Create template from loaded content
        // PromptTemplate template = PromptTemplate.from(templateContent);
        
        // TODO: Create variables with different values
        // Map<String, Object> variables = new HashMap<>();
        // variables.put("count", "10");
        // variables.put("composer", "Michael Giacchino");
        
        // TODO: Apply and generate response
        // Prompt prompt = template.apply(variables);
        // String response = model.chat(prompt.text());

        // TODO: Verify response
        // System.out.println("Resource Template Response: " + response);
        // assertNotNull(response);
        // assertTrue(response.toLowerCase().contains("michael") || response.toLowerCase().contains("giacchino"));
    }

    /**
     * Interface for movie service that uses prompt templates in annotations.
     */
    interface MovieService {
        @dev.langchain4j.service.UserMessage("Tell me {{count}} movies whose soundtrack was composed by {{composer}}")
        String getMoviesByComposer(@V("count") int count, @V("composer") String composer);
        
        @dev.langchain4j.service.UserMessage("List {{count}} {{genre}} movies from the {{decade}}s")
        String getMoviesByGenreAndDecade(@V("count") int count, @V("genre") String genre, @V("decade") String decade);
    }

    /**
     * Test 4.3: Advanced Template with AiServices
     * <p>
     * TODO: Implement template usage with AiServices annotations
     * 1. Create an OpenAI chat model
     * 2. Build a MovieService using AiServices
     * 3. Call the service method with template variables
     * 4. Verify the response contains expected content
     * 5. Test multiple template methods
     */
    @Test
    void templateWithAiServices() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create MovieService using AiServices
        // MovieService service = AiServices.builder(MovieService.class)
        //         .chatModel(model)
        //         .build();

        // TODO: Test first template method
        // String hanZimmerMovies = service.getMoviesByComposer(7, "Hans Zimmer");
        // System.out.println("Hans Zimmer Movies: " + hanZimmerMovies);
        // assertNotNull(hanZimmerMovies);
        // assertTrue(hanZimmerMovies.toLowerCase().contains("hans") || hanZimmerMovies.toLowerCase().contains("zimmer"));

        // TODO: Test second template method
        // String scifiMovies = service.getMoviesByGenreAndDecade(5, "science fiction", "1980");
        // System.out.println("80s Sci-Fi Movies: " + scifiMovies);
        // assertNotNull(scifiMovies);
        // assertFalse(scifiMovies.trim().isEmpty());
    }

    /**
     * Interface for advanced prompt templates with different data types.
     */
    interface RestaurantService {
        @dev.langchain4j.service.SystemMessage("You are a helpful restaurant recommendation assistant.")
        @dev.langchain4j.service.UserMessage("Recommend {{count}} {{cuisine}} restaurants in {{city}} with a budget of ${{budget}} per person")
        String getRestaurantRecommendations(
            @V("count") int count, 
            @V("cuisine") String cuisine, 
            @V("city") String city, 
            @V("budget") double budget
        );
        
        @dev.langchain4j.service.UserMessage("Create a {{mealType}} menu for {{people}} people with dietary restrictions: {{restrictions}}")
        String createMenu(@V("mealType") String mealType, @V("people") int people, @V("restrictions") String restrictions);
    }

    /**
     * Test 4.4: Complex Template Variables
     * <p>
     * TODO: Implement templates with multiple variable types and system messages
     * 1. Create an OpenAI chat model
     * 2. Build RestaurantService using AiServices
     * 3. Test templates with different data types (int, double, String)
     * 4. Test system message combined with user message templates
     * 5. Verify responses are contextually appropriate
     */
    @Test
    void complexTemplateVariables() {
        // TODO: Create OpenAI chat model
        // ChatModel model = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Create RestaurantService using AiServices
        // RestaurantService service = AiServices.builder(RestaurantService.class)
        //         .chatModel(model)
        //         .build();

        // TODO: Test restaurant recommendations with mixed data types
        // String recommendations = service.getRestaurantRecommendations(3, "Italian", "San Francisco", 75.50);
        // System.out.println("Restaurant Recommendations: " + recommendations);
        // assertNotNull(recommendations);
        // assertTrue(recommendations.toLowerCase().contains("italian") || recommendations.toLowerCase().contains("san francisco"));

        // TODO: Test menu creation
        // String menu = service.createMenu("dinner", 8, "vegetarian, gluten-free");
        // System.out.println("Custom Menu: " + menu);
        // assertNotNull(menu);
        // assertTrue(menu.toLowerCase().contains("vegetarian") || menu.toLowerCase().contains("gluten"));
    }
}