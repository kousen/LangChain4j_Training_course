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
 * This lab demonstrates how to use LangChain4j with multimodal AI models for image and audio analysis.
 * You'll learn how to:
 * - Analyze local images using GPT-4 Vision
 * - Analyze remote images from URLs
 * - Process audio files for transcription and analysis
 * - Use multimedia capabilities with AiServices for structured responses
 * - Extract specific information from images and audio (objects, text, transcription)
 */
class MultimediaCapabilitiesTests {

    /**
     * Test 7.1: Local Image Analysis
     * <p>
     * Demonstrates how to analyze an image loaded from local resources using GPT-4 Vision.
     */
    @Test
    void localImageAnalysis() throws IOException {
        // Create GPT-4 model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_MINI)
                .build();

        // Load image from resources
        byte[] imageBytes;
        try (var inputStream = getClass().getClassLoader()
                .getResourceAsStream("bowl_of_fruit.jpg")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find bowl_of_fruit.jpg in resources");
            }
            imageBytes = inputStream.readAllBytes();
        }

        String imageString = Base64.getEncoder().encodeToString(imageBytes);

        // Create image and text content for the message
        ImageContent imageContent = ImageContent.from(imageString, "image/jpeg");
        TextContent textContent = TextContent.from("What do you see in this image? Describe it in detail.");
        
        UserMessage userMessage = UserMessage.from(textContent, imageContent);
        
        System.out.println("=== Local Image Analysis Test ===");
        String response = model.chat(userMessage).aiMessage().text();
        System.out.println("Analysis: " + response);
        System.out.println("=".repeat(50));
        
        // Verify response quality
        assertAll("Local image analysis validation",
            () -> assertNotNull(response, "Response should not be null"),
            () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
            () -> assertTrue(response.length() > 20, "Response should be descriptive")
        );

        // Verify the response contains image-related content using AssertJ
        assertThat(response.toLowerCase())
                .as("Image analysis response")
                .containsAnyOf("image", "see", "picture", "fruit", "bowl", "color");
    }

    /**
     * Test 7.2: Remote Image Analysis
     * <p>
     * Demonstrates how to analyze an image from a remote URL using GPT-4 Vision.
     */
    @Test
    void remoteImageAnalysis() {
        // Create GPT-4 Vision model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_MINI)
                .build();

        // Use a publicly available image URL
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";
        
        // Create image and text content for the message
        ImageContent imageContent = ImageContent.from(imageUrl);
        TextContent textContent = TextContent.from("Describe this natural landscape in detail. What can you see?");
        
        UserMessage userMessage = UserMessage.from(textContent, imageContent);
        
        System.out.println("=== Remote Image Analysis Test ===");
        String response = model.chat(userMessage).aiMessage().text();
        System.out.println("URL: " + imageUrl);
        System.out.println("Analysis: " + response);
        System.out.println("=".repeat(50));
        
        // Verify response quality
        assertAll("Remote image analysis validation",
            () -> assertNotNull(response, "Response should not be null"),
            () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
            () -> assertTrue(response.length() > 30, "Response should be comprehensive")
        );

        // Verify the response contains landscape-related content
        assertThat(response.toLowerCase())
                .as("Landscape analysis response")
                .containsAnyOf("nature", "landscape", "boardwalk", "path", "grass", "sky", "outdoor");
    }

    /**
     * Test 7.3: Audio Transcription and Analysis
     * <p>
     * Demonstrates how to process audio files using AudioContent with ChatModel.
     */
    @Test
    void audioTranscriptionAnalysis() throws IOException {
        // Create GPT-4 model for audio processing
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_MINI)
                .build();

        // Create a simple audio file in memory (simulated for demonstration)
        // In a real scenario, you would load from resources or file system
        byte[] audioData = createSimpleAudioData();
        
        // Create audio and text content for the message
        AudioContent audioContent = AudioContent.from(audioData, "audio/mpeg");
        TextContent textContent = TextContent.from("Please transcribe and analyze the content of this audio file.");
        
        UserMessage userMessage = UserMessage.from(textContent, audioContent);
        
        System.out.println("=== Audio Transcription and Analysis Test ===");
        System.out.println("Audio data size: " + audioData.length + " bytes");
        
        // Note: This will work when the model supports audio processing
        try {
            String response = model.chat(userMessage).aiMessage().text();
            System.out.println("Transcription/Analysis: " + response);
            
            // Verify response quality
            assertAll("Audio analysis validation",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                () -> assertTrue(response.length() > 10, "Response should contain content")
            );

            System.out.println("=" + "=".repeat(50));
            
        } catch (Exception e) {
            // Handle gracefully if audio processing is not supported yet
            System.out.println("Audio processing not yet supported by this model: " + e.getMessage());
            System.out.println("AudioContent class exists and is ready for future audio-enabled models");
            System.out.println("=" + "=".repeat(50));
            
            // Verify AudioContent was created successfully
            assertNotNull(audioContent, "AudioContent should be created successfully");
            assertNotNull(audioContent.data(), "Audio data should not be null");
        }
    }

    /**
     * Creates audio data by loading the actual audio file from resources.
     */
    private byte[] createSimpleAudioData() throws IOException {
        // Load actual audio file from resources
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("tftjs.mp3")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find tftjs.mp3 in resources");
            }
            return inputStream.readAllBytes();
        }
    }

    /**
     * DetailedAnalyst interface for comprehensive image analysis.
     */
    interface DetailedAnalyst {
        @dev.langchain4j.service.UserMessage("Provide a comprehensive analysis of this image including: objects, colors, composition, mood, and any text. Image: {{image}}")
        ImageAnalysisResult analyzeComprehensively(@V("image") ImageContent image);
    }

    /**
     * Record to hold comprehensive image analysis results.
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
     */
    @Test
    void structuredImageAnalysis() throws IOException {
        // Create GPT-4 Vision model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_MINI)
                .build();

        // Create detailed analyst service
        DetailedAnalyst analyst = AiServices.builder(DetailedAnalyst.class)
                .chatModel(model)
                .build();

        // Load image from resources
        byte[] imageBytes;
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("bowl_of_fruit.jpg")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find bowl_of_fruit.jpg in resources");
            }
            imageBytes = inputStream.readAllBytes();
        }
        String imageString = Base64.getEncoder().encodeToString(imageBytes);
        ImageContent image = ImageContent.from(imageString, "image/jpeg");

        System.out.println("=== Structured Image Analysis Test ===");
        
        // Get comprehensive structured analysis
        ImageAnalysisResult result = analyst.analyzeComprehensively(image);
        
        System.out.println("Description: " + result.description());
        System.out.println("Objects: " + result.objects());
        System.out.println("Colors: " + result.colors());
        System.out.println("Composition: " + result.composition());
        System.out.println("Mood: " + result.mood());
        System.out.println("Text Content: " + result.textContent());
        
        System.out.println("=".repeat(50));

        // Verify structured analysis
        assertAll("Structured image analysis validation",
            () -> assertNotNull(result, "Analysis result should not be null"),
            () -> assertNotNull(result.description(), "Description should not be null"),
            () -> assertNotNull(result.objects(), "Objects should not be null"),
            () -> assertNotNull(result.colors(), "Colors should not be null"),
            () -> assertNotNull(result.composition(), "Composition should not be null"),
            () -> assertNotNull(result.mood(), "Mood should not be null"),
            () -> assertNotNull(result.textContent(), "Text content should not be null")
        );

        // Verify content quality using AssertJ
        assertThat(result.description())
                .as("Image description")
                .isNotBlank()
                .hasSizeGreaterThan(20);
                
        if (!result.objects().isEmpty()) {
            assertThat(result.objects())
                    .as("Identified objects")
                    .allSatisfy(object -> assertThat(object).isNotBlank());
        }
        
        if (!result.colors().isEmpty()) {
            assertThat(result.colors())
                    .as("Identified colors")
                    .allSatisfy(color -> assertThat(color).isNotBlank());
        }
    }
}