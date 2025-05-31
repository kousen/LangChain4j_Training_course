package com.kousenit.langchain4j;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static dev.langchain4j.model.openai.OpenAiImageModelName.DALL_E_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Lab 8: Image Generation
 * <p>
 * This lab demonstrates how to use LangChain4j with OpenAI's DALL-E for image generation.
 * You'll learn how to:
 * - Generate images using OpenAI's DALL-E model
 * - Configure image generation options (size, quality, style)
 * - Use image generation with AiServices for structured approaches
 * - Handle and process generated images
 */
class ImageGenerationTests {

    /**
     * Test 8.1: Basic Image Generation
     * <p>
     * Demonstrates how to generate a simple image using OpenAI's DALL-E model.
     */
    @Test
    void basicImageGeneration() {
        // Create OpenAI ImageModel
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(DALL_E_3)
                .build();

        // Define a creative prompt for image generation
        String prompt = "A majestic dragon soaring over a crystal castle at sunset, fantasy art style";
        
        System.out.println("=== Basic Image Generation Test ===");
        System.out.println("Prompt: " + prompt);
        
        // Generate the image
        Response<Image> response = model.generate(prompt);
        
        // Extract and verify the generated image
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.content(), "Response content should not be null");
        
        Image image = response.content();
        System.out.println("Generated image URL: " + image.url());
        System.out.println("Revised prompt: " + image.revisedPrompt());
        System.out.println("=" + "=".repeat(50));
        
        // Verify the image was generated successfully
        assertNotNull(image.url(), "Image URL should not be null");
        assertThat(image.url().toString())
                .as("Generated image URL")
                .isNotBlank()
                .startsWith("https://");
                
        // Verify revised prompt is provided
        if (image.revisedPrompt() != null) {
            assertThat(image.revisedPrompt())
                    .as("Revised prompt")
                    .isNotBlank();
        }
    }

    /**
     * Test 8.2: Image Generation with Options
     * <p>
     * Demonstrates how to generate images with specific configuration options.
     */
    @Test
    void imageGenerationWithOptions() {
        // Create OpenAI ImageModel with specific options
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(DALL_E_3)
                .size("1024x1024")
                .quality("hd")
                .style("vivid")
                .build();

        // Define a detailed prompt for high-quality generation
        String prompt = """
            A futuristic cityscape at dawn with flying vehicles,
            neon lights reflecting on wet streets, cyberpunk aesthetic""";
        
        System.out.println("=== Image Generation with Options Test ===");
        System.out.println("Prompt: " + prompt);
        System.out.println("Configuration: 1024x1024, HD quality, vivid style");
        
        // Generate the image with enhanced settings
        Response<Image> response = model.generate(prompt);
        Image image = response.content();
        
        // Display results and verify
        System.out.println("High-quality generated image URL: " + image.url());
        System.out.println("Revised prompt: " + image.revisedPrompt());
        
        // Optional - demonstrate successful generation
        if (image.url() != null) {
            System.out.println("Image generated successfully with HD quality!");
        }
        System.out.println("=" + "=".repeat(50));
        
        // Verify the image generation
        assertNotNull(image.url(), "HD image URL should not be null");
        assertThat(image.url().toString())
                .as("HD generated image URL")
                .isNotBlank()
                .startsWith("https://");
    }


    /**
     * Test 8.3: Advanced Image Generation Configuration
     * <p>
     * Demonstrates generating images with different artistic styles and detailed prompts.
     */
    @Test
    void advancedImageGeneration() {
        // Create OpenAI ImageModel with production settings
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(DALL_E_3)
                .size("1024x1024")
                .quality("standard")
                .build();

        System.out.println("=== Advanced Image Generation Test ===");
        
        // Test artistic style variation
        String artisticPrompt = """
            A serene Japanese garden with cherry blossoms,
            traditional architecture, and a koi pond,
            watercolor painting style""";
        Response<Image> artisticResponse = model.generate(artisticPrompt);
        Image artisticImage = artisticResponse.content();
        
        System.out.println("Artistic prompt: " + artisticPrompt);
        System.out.println("Generated artistic image: " + artisticImage.url());
        if (artisticImage.revisedPrompt() != null) {
            System.out.println("Revised artistic prompt: " + artisticImage.revisedPrompt());
        }
        System.out.println();
        
        // Test technical/detailed prompt
        String technicalPrompt = """
            A detailed cross-section of a mechanical watch
            showing gears, springs, and intricate components,
            technical illustration style""";
        Response<Image> technicalResponse = model.generate(technicalPrompt);
        Image technicalImage = technicalResponse.content();
        
        System.out.println("Technical prompt: " + technicalPrompt);
        System.out.println("Generated technical image: " + technicalImage.url());
        if (technicalImage.revisedPrompt() != null) {
            System.out.println("Revised technical prompt: " + technicalImage.revisedPrompt());
        }
        System.out.println("=" + "=".repeat(50));
        
        // Verify both images were generated successfully
        assertNotNull(artisticImage.url(), "Artistic image URL should not be null");
        assertNotNull(technicalImage.url(), "Technical image URL should not be null");
        
        assertThat(artisticImage.url().toString())
                .as("Artistic image URL")
                .isNotBlank()
                .startsWith("https://");
                
        assertThat(technicalImage.url().toString())
                .as("Technical image URL")
                .isNotBlank()
                .startsWith("https://");
    }

    /**
     * Test 8.4: Creative Image Generation Variations
     * <p>
     * Demonstrates generating multiple variations of images with different prompts.
     */
    @Test
    void creativeImageVariations() {
        // Create OpenAI ImageModel
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(DALL_E_3)
                .size("1024x1024")
                .build();

        // Define different artistic prompts
        String[] prompts = {
            "A steampunk robot playing chess, detailed mechanical parts, brass and copper tones",
            "A minimalist abstract representation of music, flowing lines and geometric shapes",
            "A cozy library in a treehouse, warm lighting, books floating magically"
        };
        
        System.out.println("=== Creative Image Variations Test ===");
        
        // Generate and display multiple image variations
        for (int i = 0; i < prompts.length; i++) {
            Response<Image> response = model.generate(prompts[i]);
            Image image = response.content();

            System.out.println("=== Variation " + (i + 1) + " ===");
            System.out.println("Original prompt: " + prompts[i]);
            System.out.println("Generated image URL: " + image.url());

            if (image.revisedPrompt() != null) {
                System.out.println("Revised prompt: " + image.revisedPrompt());
            }
            System.out.println();
            
            // Verify each image generation
            assertNotNull(image.url(), "Image " + (i + 1) + " URL should not be null");
            assertThat(image.url().toString())
                    .as("Variation " + (i + 1) + " image URL")
                    .isNotBlank()
                    .startsWith("https://");
        }

        System.out.println("All variations generated successfully!");
        System.out.println("=" + "=".repeat(50));
    }

    /**
     * Test 8.5: Base64 Image Generation with GPT-Image-1 Model
     * <p>
     * Demonstrates using the new "gpt-image-1" model which returns base64-encoded images
     * instead of URLs, and saves the decoded image to a file.
     */
    @Test
    void base64ImageGeneration() throws IOException {
        // Create OpenAI ImageModel using the new gpt-image-1 model
        // Note: No constant available yet for this new model
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-image-1")
                .build();

        // Define a creative prompt
        String prompt = """
            A warrior cat rides a dragon into battle""";
        
        System.out.println("=== Base64 Image Generation Test ===");
        System.out.println("Prompt: " + prompt);
        System.out.println("Model: gpt-image-1 (returns base64-encoded images)");
        
        // Generate the image
        Response<Image> response = model.generate(prompt);
        Image image = response.content();
        
        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertNotNull(image, "Image should not be null");
        
        // The gpt-image-1 model returns base64-encoded images instead of URLs
        // Check if we have base64 data instead of a URL
        String base64Data = null;
        if (image.base64Data() != null) {
            base64Data = image.base64Data();
        } else if (image.url() != null && image.url().toString().startsWith("data:")) {
            // Some implementations might return data URLs
            base64Data = image.url().toString().split(",")[1];
        }
        
        assertNotNull(base64Data, "Base64 image data should not be null");
        assertThat(base64Data)
                .as("Base64 image data")
                .isNotBlank()
                .hasSizeGreaterThan(100); // Should be substantial data
        
        System.out.println("Base64 data length: " + base64Data.length() + " characters");
        
        // Decode the base64 to bytes
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        
        // Create output directory if it doesn't exist
        Path outputDir = Path.of("src/main/resources");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        // Write to file (PNG format)
        Path outputPath = outputDir.resolve("generated_image.png");
        Files.write(outputPath, imageBytes);
        
        System.out.println("Image saved as: " + outputPath);
        System.out.println("File size: " + imageBytes.length + " bytes");
        
        // Verify the file was created and has content
        assertThat(Files.exists(outputPath))
                .as("Generated image file should exist")
                .isTrue();
                
        assertThat(Files.size(outputPath))
                .as("Generated image file should have content")
                .isGreaterThan(0);
        
        System.out.println("=" + "=".repeat(50));
        
        // Note: The generated image file can be opened with any image viewer
        // or used in web applications. The gpt-image-1 model provides more 
        // control over the image data compared to URL-based responses.
    }
}