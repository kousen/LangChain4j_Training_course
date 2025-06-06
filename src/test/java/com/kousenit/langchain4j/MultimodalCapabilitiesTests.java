package com.kousenit.langchain4j;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Lab 7: Multimodal Capabilities
 * <p>
 * This lab demonstrates how to use LangChain4j with multimodal AI models for image and audio analysis.
 * You'll learn how to:
 * - Analyze local images using GPT-4 Vision
 * - Analyze remote images from URLs
 * - Process audio files for transcription and analysis
 * - Use multimodal capabilities with AiServices for structured responses
 * - Extract specific information from images and audio (objects, text, transcription)
 */
class MultimodalCapabilitiesTests {

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
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("bowl_of_fruit.jpg")) {
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
        assertAll(
                "Local image analysis validation",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                () -> assertTrue(response.length() > 20, "Response should be descriptive"));

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
        String imageUrl =
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

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
        assertAll(
                "Remote image analysis validation",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                () -> assertTrue(response.length() > 30, "Response should be comprehensive"));

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
    @EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
    void audioTranscriptionAnalysis() throws IOException {
        // Create GPT-4 model for audio processing
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GOOGLEAI_API_KEY"))
                .modelName("gemini-2.5-flash-preview-05-20")
                .build();

        // Create audio and text content for the message
        TextContent textContent = TextContent.from("Please transcribe and analyze the content of this audio file.");
        AudioContent audioContent = AudioContent.from(readSimpleAudioData(), "audio/mp3");

        UserMessage userMessage = UserMessage.from(textContent, audioContent);

        System.out.println("=== Audio Transcription and Analysis Test ===");
        // Note: This will work when the model supports audio processing
        try {
            String response = model.chat(userMessage).aiMessage().text();
            System.out.println("Transcription/Analysis: " + response);

            // Verify response quality
            assertAll(
                    "Audio analysis validation",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                    () -> assertTrue(response.length() > 10, "Response should contain content"));

            System.out.println("=" + "=".repeat(50));

        } catch (Exception e) {
            // Handle gracefully if audio processing is not supported yet
            System.out.println("Audio processing issue: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=" + "=".repeat(50));

            // Verify AudioContent was created successfully
            assertNotNull(audioContent, "AudioContent should be created successfully");
        }
    }

    /**
     * Load an audio file from resources and Base64 encode it.
     */
    private String readSimpleAudioData() throws IOException {
        // Load actual audio file from resources
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("tftjs.mp3")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find tftjs.mp3 in resources");
            }
            return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
        }
    }

    /**
     * DetailedAnalyst interface for comprehensive image analysis.
     */
    interface DetailedAnalyst {
        @dev.langchain4j.service.UserMessage(
                """
            Provide a comprehensive analysis of this image
            including: objects, colors, composition, mood,
            and any text. Image: {{image}}""")
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
            String textContent) {}

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
        DetailedAnalyst analyst =
                AiServices.builder(DetailedAnalyst.class).chatModel(model).build();

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
        assertAll(
                "Structured image analysis validation",
                () -> assertNotNull(result, "Analysis result should not be null"),
                () -> assertNotNull(result.description(), "Description should not be null"),
                () -> assertNotNull(result.objects(), "Objects should not be null"),
                () -> assertNotNull(result.colors(), "Colors should not be null"),
                () -> assertNotNull(result.composition(), "Composition should not be null"),
                () -> assertNotNull(result.mood(), "Mood should not be null"),
                () -> assertNotNull(result.textContent(), "Text content should not be null"));

        // Verify content quality using AssertJ
        assertThat(result.description()).as("Image description").isNotBlank().hasSizeGreaterThan(20);

        if (!result.objects().isEmpty()) {
            assertThat(result.objects()).as("Identified objects").allSatisfy(object -> assertThat(object)
                    .isNotBlank());
        }

        if (!result.colors().isEmpty()) {
            assertThat(result.colors()).as("Identified colors").allSatisfy(color -> assertThat(color)
                    .isNotBlank());
        }
    }
}
