package com.kousenit.langchain4j;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Lab 8: Image Generation
 *
 * <p>This lab demonstrates image generation with OpenAI's GPT Image family. DALL-E 3 was
 * deprecated on May 12, 2026; the course uses {@code gpt-image-2}, which returns base64 image data
 * rather than hosted image URLs.
 */
class ImageGenerationTests {

    private static final String GPT_IMAGE_2 = "gpt-image-2";

    /**
     * Test 8.1: Basic Image Generation
     *
     * <p>TODO: Generate a single image with default settings.
     *
     * <p>Instructions: 1. Create an {@code OpenAiImageModel} with {@code GPT_IMAGE_2}. 2. Generate an
     * image from a creative prompt. 3. Read the returned {@code Image}. 4. Verify {@code
     * image.base64Data()} is present and non-empty. 5. Print the base64 length for inspection.
     */
    @Test
    void basicImageGeneration() {
        // TODO: Create OpenAI ImageModel
        // ImageModel model = OpenAiImageModel.builder()
        //     .apiKey(System.getenv("OPENAI_API_KEY"))
        //     .modelName(GPT_IMAGE_2)
        //     .build();

        // TODO: Generate an image and assert that image.base64Data() is non-empty.

        fail("TODO: Implement basic image generation test with gpt-image-2");
    }

    /**
     * Test 8.2: Image Generation with Options
     *
     * <p>TODO: Configure image size and quality.
     *
     * <p>Valid GPT Image quality values are {@code low}, {@code medium}, {@code high}, and {@code
     * auto}. Do not use DALL-E 3's old {@code standard}/{@code hd} values.
     */
    @Test
    void imageGenerationWithOptions() {
        // TODO: Create OpenAI ImageModel with size("1024x1024") and quality("high").

        // TODO: Generate an image and verify the base64 response is present.

        fail("TODO: Implement image generation with options test");
    }

    /**
     * Test 8.3: Artistic Style Variations
     *
     * <p>TODO: Generate the same type of scene in two different artistic styles.
     *
     * <p>GPT Image models do not have a {@code style(...)} builder option. Express style directly in
     * the prompt, such as "watercolor painting style" or "technical illustration style".
     */
    @Test
    void artisticStyleVariations() {
        // TODO: Create OpenAI ImageModel with GPT_IMAGE_2.

        // TODO: Generate one watercolor-style image and one technical-illustration image.

        // TODO: Verify both responses contain base64 image data.

        fail("TODO: Implement artistic style variations test");
    }

    /**
     * Test 8.4: Multiple Variations
     *
     * <p>TODO: Iterate through several prompts to compare how prompt wording affects output.
     */
    @Test
    void creativeImageVariations() {
        // TODO: Define an array of prompts.

        // TODO: Generate an image for each prompt.

        // TODO: Verify each generated image has base64 data.

        fail("TODO: Implement creative image variations test");
    }

    /**
     * Test 8.5: Save Generated Image to File
     *
     * <p>TODO: Decode a base64 image response and write it to {@code
     * build/generated-images/generated_image.png}.
     */
    @Test
    void saveGeneratedImageToFile() throws IOException {
        // TODO: Generate an image with GPT_IMAGE_2.

        // TODO: Decode image.base64Data() with Base64.getDecoder().decode(...).

        // TODO: Create build/generated-images if necessary, then write generated_image.png.

        // TODO: Verify the file exists and has a non-zero size.

        fail("TODO: Implement save generated image to file test");
    }
}
