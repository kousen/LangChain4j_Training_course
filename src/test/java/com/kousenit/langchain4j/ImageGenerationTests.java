package com.kousenit.langchain4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.junit.jupiter.api.Test;

/**
 * Lab 8: Image Generation
 * <p>
 * Demonstrates image generation with OpenAI's GPT Image family. DALL-E 3 was
 * deprecated on May 12, 2026; gpt-image-2 is the current course model and
 * returns base64-encoded image data instead of URLs.
 */
class ImageGenerationTests {

    private static final String GPT_IMAGE_2 = "gpt-image-2";

    /**
     * Test 8.1: Basic Image Generation
     * <p>
     * Generate a single image with default settings.
     */
    @Test
    void basicImageGeneration() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_IMAGE_2)
                .build();

        String prompt = "A majestic dragon soaring over a crystal castle at sunset, fantasy art style";

        System.out.println("=== Basic Image Generation Test ===");
        System.out.println("Prompt: " + prompt);

        Response<Image> response = model.generate(prompt);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.content(), "Response content should not be null");

        Image image = response.content();
        String base64 = image.base64Data();
        assertNotNull(base64, "Base64 image data should not be null");

        System.out.println("Base64 data length: " + base64.length() + " characters");
        System.out.println("=".repeat(50));

        assertThat(base64).as("Base64 image data").isNotBlank().hasSizeGreaterThan(100);
    }

    /**
     * Test 8.2: Image Generation with Options
     * <p>
     * Configure size and quality. Valid quality values for GPT Image models are
     * "low", "medium", "high", and "auto" (different from DALL-E 3's
     * "standard" / "hd").
     */
    @Test
    void imageGenerationWithOptions() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_IMAGE_2)
                .size("1024x1024")
                .quality("high")
                .build();

        String prompt = """
                A futuristic cityscape at dawn with flying vehicles,
                neon lights reflecting on wet streets, cyberpunk aesthetic
                """;

        System.out.println("=== Image Generation with Options Test ===");
        System.out.println("Configuration: 1024x1024, high quality");

        Response<Image> response = model.generate(prompt);
        Image image = response.content();
        String base64 = image.base64Data();

        assertNotNull(base64, "Base64 image data should not be null");
        System.out.println("High-quality image base64 length: " + base64.length());
        System.out.println("=".repeat(50));

        assertThat(base64).as("HD image base64").isNotBlank().hasSizeGreaterThan(1000);
    }

    /**
     * Test 8.3: Artistic Style Variations
     * <p>
     * Generate the same scene in different artistic styles by varying only
     * the prompt — GPT Image models have no `style` builder parameter (DALL-E 3
     * had `style("vivid")` / `style("natural")`); style is now expressed in
     * the prompt itself.
     */
    @Test
    void artisticStyleVariations() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_IMAGE_2)
                .size("1024x1024")
                .build();

        System.out.println("=== Artistic Style Variations Test ===");

        String watercolorPrompt = """
                A serene Japanese garden with cherry blossoms, traditional
                architecture, and a koi pond, watercolor painting style
                """;
        Response<Image> watercolor = model.generate(watercolorPrompt);
        assertNotNull(watercolor.content().base64Data(), "Watercolor image should have base64 data");
        System.out.println(
                "Watercolor base64 length: " + watercolor.content().base64Data().length());

        String technicalPrompt = """
                A detailed cross-section of a mechanical watch showing
                gears, springs, and intricate components, technical
                illustration style
                """;
        Response<Image> technical = model.generate(technicalPrompt);
        assertNotNull(technical.content().base64Data(), "Technical image should have base64 data");
        System.out.println(
                "Technical base64 length: " + technical.content().base64Data().length());

        System.out.println("=".repeat(50));
    }

    /**
     * Test 8.4: Multiple Variations
     * <p>
     * Iterate through several prompts to see how prompt phrasing affects
     * the model's output.
     */
    @Test
    void creativeImageVariations() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_IMAGE_2)
                .size("1024x1024")
                .build();

        String[] prompts = {
            "A steampunk robot playing chess, detailed mechanical parts, brass and copper tones",
            "A minimalist abstract representation of music, flowing lines and geometric shapes",
            "A cozy library in a treehouse, warm lighting, books floating magically"
        };

        System.out.println("=== Creative Image Variations Test ===");

        for (int i = 0; i < prompts.length; i++) {
            Response<Image> response = model.generate(prompts[i]);
            String base64 = response.content().base64Data();

            assertNotNull(base64, "Variation " + (i + 1) + " base64 should not be null");
            System.out.println("Variation " + (i + 1) + " base64 length: " + base64.length());
            assertThat(base64)
                    .as("Variation " + (i + 1) + " base64")
                    .isNotBlank()
                    .hasSizeGreaterThan(1000);
        }

        System.out.println("All variations generated successfully!");
        System.out.println("=".repeat(50));
    }

    /**
     * Test 8.5: Save Generated Image to File
     * <p>
     * Decode the base64 response and write a PNG to disk so the result is
     * easy to inspect.
     */
    @Test
    void saveGeneratedImageToFile() throws IOException {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_IMAGE_2)
                .build();

        String prompt = "A warrior cat rides a dragon into battle";

        System.out.println("=== Save Generated Image Test ===");
        System.out.println("Prompt: " + prompt);

        Response<Image> response = model.generate(prompt);
        String base64 = response.content().base64Data();
        assertNotNull(base64, "Base64 image data should not be null");

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        Path outputDir = Path.of("build", "generated-images");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        Path outputPath = outputDir.resolve("generated_image.png");
        Files.write(outputPath, imageBytes);

        System.out.println("Image saved as: " + outputPath);
        System.out.println("File size: " + imageBytes.length + " bytes");
        System.out.println("=".repeat(50));

        assertThat(Files.exists(outputPath))
                .as("Generated image file should exist")
                .isTrue();
        assertThat(Files.size(outputPath))
                .as("Generated image file should have content")
                .isGreaterThan(0);
    }
}
