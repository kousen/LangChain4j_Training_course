package com.kousenit.langchain4j;

import static org.junit.jupiter.api.Assertions.fail;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.service.V;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Lab 7: Multimodal Capabilities
 *
 * <p>This lab demonstrates image and audio handling with LangChain4j 1.15: GPT-5.1 vision for
 * local and remote image analysis, OpenAI's dedicated transcription model for audio, and structured
 * image analysis through AiServices.
 */
class MultimodalCapabilitiesTests {

    /**
     * Test 7.1: Local Image Analysis
     *
     * <p>TODO: Analyze {@code bowl_of_fruit.jpg} from the classpath with {@code GPT_5_1}.
     *
     * <p>Implementation outline: create an {@code OpenAiChatModel}, load the image bytes, encode them
     * as base64, create {@code ImageContent} plus {@code TextContent}, send a {@code UserMessage}, and
     * assert that the response describes the image.
     */
    @Test
    void localImageAnalysis() throws IOException {
        // TODO: Create OpenAI chat model with GPT_5_1.

        // TODO: Load bowl_of_fruit.jpg from resources and base64 encode it.

        // TODO: Create ImageContent and TextContent, then call model.chat(...).

        // TODO: Assert the response is descriptive and image-related.

        fail("TODO: Implement local image analysis test");
    }

    /**
     * Test 7.2: Remote Image Analysis
     *
     * <p>TODO: Analyze a remote image URL with {@code GPT_5_1}.
     */
    @Test
    void remoteImageAnalysis() {
        // Use this public image URL:
        String imageUrl =
                "https://upload.wikimedia.org/wikipedia/commons/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        // TODO: Create ImageContent.from(imageUrl) and a text prompt.

        // TODO: Send the multimodal message to the model.

        // TODO: Assert the response contains landscape-related content.

        fail("TODO: Implement remote image analysis test");
    }

    /**
     * Test 7.3: Audio Transcription
     *
     * <p>TODO: Transcribe {@code tftjs.mp3} with {@code OpenAiAudioTranscriptionModel} and {@code
     * gpt-4o-transcribe}.
     *
     * <p>This replaces the older Gemini-audio path and only requires {@code OPENAI_API_KEY}.
     */
    @Test
    void audioTranscription() throws IOException {
        // TODO: Create OpenAiAudioTranscriptionModel with modelName("gpt-4o-transcribe").

        // TODO: Load tftjs.mp3 as bytes from resources.

        // TODO: Build Audio with binaryData(audioBytes) and mimeType("audio/mp3").

        // TODO: Build AudioTranscriptionRequest, call transcribe(...), and assert response.text().

        fail("TODO: Implement audio transcription test with OpenAI transcription model");
    }

    /**
     * DetailedAnalyst interface for comprehensive image analysis.
     *
     * <p>TODO: Add a {@code @UserMessage} annotation that asks for objects, colors, composition,
     * mood, and visible text.
     */
    interface DetailedAnalyst {
        ImageAnalysisResult analyzeComprehensively(@V("image") ImageContent image);
    }

    /** Record to hold comprehensive image analysis results. */
    record ImageAnalysisResult(
            String description,
            List<String> objects,
            List<String> colors,
            String composition,
            String mood,
            String textContent) {}

    /**
     * Test 7.4: Structured Image Analysis
     *
     * <p>TODO: Use AiServices to map a vision response into {@link ImageAnalysisResult}.
     */
    @Test
    void structuredImageAnalysis() throws IOException {
        // TODO: Create GPT_5_1 chat model and DetailedAnalyst service.

        // TODO: Load bowl_of_fruit.jpg and create ImageContent.

        // TODO: Call analyzeComprehensively and assert all record fields are populated.

        fail("TODO: Implement structured image analysis test");
    }
}
