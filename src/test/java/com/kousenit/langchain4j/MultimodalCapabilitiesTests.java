package com.kousenit.langchain4j;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_5_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import dev.langchain4j.model.audio.AudioTranscriptionResponse;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiAudioTranscriptionModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Lab 7: Multimodal Capabilities
 * <p>
 * Demonstrates image and audio handling with LangChain4j 1.14:
 * <ul>
 *   <li>Local image analysis with GPT-5.1 vision</li>
 *   <li>Remote (URL) image analysis</li>
 *   <li>Audio transcription via OpenAI's dedicated transcription model
 *       (added in LangChain4j 1.10) — replaces the older approach of
 *       routing audio through a multimodal chat model</li>
 *   <li>Structured image analysis through AiServices</li>
 * </ul>
 */
class MultimodalCapabilitiesTests {

    /**
     * Test 7.1: Local Image Analysis
     */
    @Test
    void localImageAnalysis() throws IOException {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_5_1)
                .build();

        byte[] imageBytes;
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("bowl_of_fruit.jpg")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find bowl_of_fruit.jpg in resources");
            }
            imageBytes = inputStream.readAllBytes();
        }

        String imageString = Base64.getEncoder().encodeToString(imageBytes);

        ImageContent imageContent = ImageContent.from(imageString, "image/jpeg");
        TextContent textContent = TextContent.from("What do you see in this image? Describe it in detail.");

        UserMessage userMessage = UserMessage.from(textContent, imageContent);

        System.out.println("=== Local Image Analysis Test ===");
        String response = model.chat(userMessage).aiMessage().text();
        System.out.println("Analysis: " + response);
        System.out.println("=".repeat(50));

        assertAll(
                "Local image analysis validation",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                () -> assertTrue(response.length() > 20, "Response should be descriptive"));

        assertThat(response.toLowerCase())
                .as("Image analysis response")
                .containsAnyOf("image", "see", "picture", "fruit", "bowl", "color");
    }

    /**
     * Test 7.2: Remote Image Analysis
     */
    @Test
    void remoteImageAnalysis() {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_5_1)
                .build();

        String imageUrl =
                "https://upload.wikimedia.org/wikipedia/commons/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        ImageContent imageContent = ImageContent.from(imageUrl);
        TextContent textContent = TextContent.from("Describe this natural landscape in detail. What can you see?");

        UserMessage userMessage = UserMessage.from(textContent, imageContent);

        System.out.println("=== Remote Image Analysis Test ===");
        String response = model.chat(userMessage).aiMessage().text();
        System.out.println("URL: " + imageUrl);
        System.out.println("Analysis: " + response);
        System.out.println("=".repeat(50));

        assertAll(
                "Remote image analysis validation",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
                () -> assertTrue(response.length() > 30, "Response should be comprehensive"));

        assertThat(response.toLowerCase())
                .as("Landscape analysis response")
                .containsAnyOf("nature", "landscape", "boardwalk", "path", "grass", "sky", "outdoor");
    }

    /**
     * Test 7.3: Audio Transcription
     * <p>
     * Uses OpenAI's dedicated transcription model (added in LangChain4j
     * 1.10). Earlier versions of this lab routed audio through Gemini's
     * multimodal chat endpoint; that's still possible, but the dedicated
     * transcription model is the simpler and more direct approach.
     */
    @Test
    void audioTranscription() throws IOException {
        OpenAiAudioTranscriptionModel transcriptionModel = OpenAiAudioTranscriptionModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-transcribe")
                .build();

        byte[] audioBytes;
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("tftjs.mp3")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find tftjs.mp3 in resources");
            }
            audioBytes = inputStream.readAllBytes();
        }

        Audio audio =
                Audio.builder().binaryData(audioBytes).mimeType("audio/mp3").build();

        AudioTranscriptionRequest request =
                AudioTranscriptionRequest.builder().audio(audio).build();

        System.out.println("=== Audio Transcription Test ===");
        AudioTranscriptionResponse response = transcriptionModel.transcribe(request);
        String transcript = response.text();
        System.out.println("Transcript: " + transcript);
        System.out.println("=".repeat(50));

        assertAll(
                "Audio transcription validation",
                () -> assertNotNull(transcript, "Transcript should not be null"),
                () -> assertFalse(transcript.trim().isEmpty(), "Transcript should not be empty"),
                () -> assertTrue(transcript.length() > 10, "Transcript should contain content"));
    }

    /**
     * DetailedAnalyst interface for comprehensive image analysis.
     */
    interface DetailedAnalyst {
        @dev.langchain4j.service.UserMessage("""
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
     */
    @Test
    void structuredImageAnalysis() throws IOException {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_5_1)
                .build();

        DetailedAnalyst analyst =
                AiServices.builder(DetailedAnalyst.class).chatModel(model).build();

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

        ImageAnalysisResult result = analyst.analyzeComprehensively(image);

        System.out.println("Description: " + result.description());
        System.out.println("Objects: " + result.objects());
        System.out.println("Colors: " + result.colors());
        System.out.println("Composition: " + result.composition());
        System.out.println("Mood: " + result.mood());
        System.out.println("Text Content: " + result.textContent());
        System.out.println("=".repeat(50));

        assertAll(
                "Structured image analysis validation",
                () -> assertNotNull(result, "Analysis result should not be null"),
                () -> assertNotNull(result.description(), "Description should not be null"),
                () -> assertNotNull(result.objects(), "Objects should not be null"),
                () -> assertNotNull(result.colors(), "Colors should not be null"),
                () -> assertNotNull(result.composition(), "Composition should not be null"),
                () -> assertNotNull(result.mood(), "Mood should not be null"),
                () -> assertNotNull(result.textContent(), "Text content should not be null"));

        assertThat(result.description()).as("Image description").isNotBlank().hasSizeGreaterThan(20);

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
