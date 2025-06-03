package com.kousenit.langchain4j;

import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 7: Multimedia Capabilities
 * <p>
 * This lab demonstrates how to use LangChain4j with multimodal AI models for both image and audio analysis.
 * You'll learn how to:
 * - Analyze local images using GPT-4
 * - Analyze remote images from URLs
 * - Process audio content with AudioContent
 * - Extract structured data from multimedia analysis results
 */
class MultimediaCapabilitiesTests {

    /**
     * Test 7.1: Local Image Analysis
     * <p>
     * Demonstrates how to analyze an image loaded from local resources using GPT-4 Vision.
     * 
     * TODO: Implement this test method:
     * 1. Create a GPT-4 model using OpenAiChatModel.builder()
     * 2. Load the image from resources (bowl_of_fruit.jpg) with proper null checking
     * 3. Convert the image bytes to Base64 string
     * 4. Create ImageContent and TextContent for the message
     * 5. Send the message to the model and get response
     * 6. Add proper assertions to verify the response
     */
    @Test
    void localImageAnalysis() throws IOException {
        // TODO: Create GPT-4 model
        
        // TODO: Load image from resources with null check
        
        // TODO: Convert image to Base64 string
        
        // TODO: Create ImageContent and TextContent
        
        // TODO: Create UserMessage and send to model
        
        // TODO: Add assertions to verify response
        
        fail("TODO: Implement localImageAnalysis test");
    }

    /**
     * Test 7.2: Remote Image Analysis
     * <p>
     * Demonstrates how to analyze an image from a remote URL using GPT-4 Vision.
     * 
     * TODO: Implement this test method:
     * 1. Create a GPT-4 model using OpenAiChatModel.builder()
     * 2. Use the provided image URL for a nature boardwalk
     * 3. Create ImageContent from the URL and TextContent for the prompt
     * 4. Send the message to the model and get response
     * 5. Add proper assertions to verify the response contains landscape-related content
     */
    @Test
    void remoteImageAnalysis() {
        // TODO: Create GPT-4 Vision model
        
        // Use this publicly available image URL:
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";
        
        // TODO: Create ImageContent from URL and TextContent for prompt
        
        // TODO: Create UserMessage and send to model
        
        // TODO: Add assertions to verify response contains landscape content
        
        fail("TODO: Implement remoteImageAnalysis test");
    }

    /**
     * Test 7.3: Audio Transcription and Analysis
     * <p>
     * Demonstrates audio processing using AudioContent with Google's Gemini model.
     * 
     * TODO: Implement this test method:
     * 1. Add @EnabledIfEnvironmentVariable annotation to check for GOOGLEAI_API_KEY
     * 2. Create a Google Gemini model using GoogleAiGeminiChatModel.builder()
     * 3. Use model name "gemini-2.5-flash-preview-05-20" which supports audio
     * 4. Load audio data from tftjs.mp3 file and Base64 encode it using readSimpleAudioData()
     * 5. Create AudioContent with "audio/mp3" MIME type and TextContent for the message
     * 6. Send the message to the model with proper error handling
     * 7. Verify the audio transcription response
     */
    @Test
    // TODO: Add @EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
    void audioTranscriptionAnalysis() throws IOException {
        // TODO: Create Google Gemini model for audio processing
        // Hint: Import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
        // ChatModel model = GoogleAiGeminiChatModel.builder()
        //         .apiKey(System.getenv("GOOGLEAI_API_KEY"))
        //         .modelName("gemini-2.5-flash-preview-05-20")
        //         .build();

        // TODO: Create audio and text content for the message
        // TextContent textContent = TextContent.from("Please transcribe and analyze the content of this audio file.");
        // AudioContent audioContent = AudioContent.from(readSimpleAudioData(), "audio/mp3");
        
        // UserMessage userMessage = UserMessage.from(textContent, audioContent);
        
        // TODO: Process the audio with Gemini
        // System.out.println("=== Audio Transcription and Analysis Test ===");
        
        // try {
        //     String response = model.chat(userMessage).aiMessage().text();
        //     System.out.println("Transcription/Analysis: " + response);
        //     
        //     // Verify response quality
        //     assertAll("Audio analysis validation",
        //         () -> assertNotNull(response, "Response should not be null"),
        //         () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
        //         () -> assertTrue(response.length() > 10, "Response should contain content")
        //     );
        //
        //     System.out.println("=" + "=".repeat(50));
        //     
        // } catch (Exception e) {
        //     // Handle gracefully if audio processing is not supported
        //     System.out.println("Audio processing issue: " + e.getMessage());
        //     e.printStackTrace();
        //     System.out.println("=" + "=".repeat(50));
        //     
        //     // Verify AudioContent was created successfully
        //     assertNotNull(audioContent, "AudioContent should be created successfully");
        // }
        
        fail("TODO: Implement audioTranscriptionAnalysis test with Google Gemini");
    }

    /**
     * Load an audio file from resources and Base64 encode it.
     * 
     * TODO: This helper method is provided for the audio test.
     * You can uncomment it when implementing the audio test.
     */
    // private String readSimpleAudioData() throws IOException {
    //     // Load actual audio file from resources
    //     try (var inputStream = getClass().getClassLoader()
    //             .getResourceAsStream("tftjs.mp3")) {
    //         if (inputStream == null) {
    //             throw new RuntimeException("Could not find tftjs.mp3 in resources");
    //         }
    //         return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
    //     }
    // }

    /**
     * DetailedAnalyst interface for comprehensive image analysis.
     * 
     * TODO: Complete the interface method with proper annotation for comprehensive analysis
     */
    interface DetailedAnalyst {
        // TODO: Add @UserMessage annotation for comprehensive analysis
        ImageAnalysisResult analyzeComprehensively(@V("image") ImageContent image);
    }

    /**
     * Record to hold comprehensive image analysis results.
     * 
     * This record is already complete - no TODO needed.
     */
    record ImageAnalysisResult(
        String description,
        List<String> objects,
        List<String> colors,
        String composition,
        String mood,
        String textContent
    ) {}

    /**
     * Test 7.4: Structured Image Analysis
     * <p>
     * Demonstrates extracting structured data from image analysis results.
     * 
     * TODO: Implement this test method:
     * 1. Create GPT-4 model and DetailedAnalyst service
     * 2. Load image from resources with proper null checking
     * 3. Get comprehensive structured analysis using the service
     * 4. Add assertions to verify all fields in the result record
     */
    @Test
    void structuredImageAnalysis() throws IOException {
        // TODO: Create GPT-4 Vision model
        
        // TODO: Create DetailedAnalyst service using AiServices.builder()
        
        // TODO: Load image from resources with null check
        
        // TODO: Get comprehensive analysis result
        
        // TODO: Add comprehensive assertions for the structured result
        
        fail("TODO: Implement structuredImageAnalysis test");
    }
}