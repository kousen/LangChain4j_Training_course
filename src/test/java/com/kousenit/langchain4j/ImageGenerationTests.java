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
import static org.junit.jupiter.api.Assertions.*;

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
     * TODO: Demonstrates how to generate a simple image using OpenAI's DALL-E model.
     * 
     * Instructions:
     * 1. Create an OpenAI ImageModel using the builder pattern
     * 2. Set the API key from environment variables
     * 3. Use the "dall-e-3" model name
     * 4. Generate an image with a creative prompt
     * 5. Verify the response and extract the image URL
     * 6. Print the generated image URL and any revised prompt
     */
    @Test
    void basicImageGeneration() {
        // TODO: Create OpenAI ImageModel
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(DALL_E_3)
        //         .build();

        // TODO: Define a creative prompt for image generation
        // String prompt = "A majestic dragon soaring over a crystal castle at sunset, fantasy art style";
        
        // TODO: Generate the image
        // Response<Image> response = model.generate(prompt);
        
        // TODO: Extract and verify the generated image
        // assertNotNull(response);
        // assertNotNull(response.content());
        // 
        // Image image = response.content();
        // System.out.println("Generated image URL: " + image.url());
        // System.out.println("Revised prompt: " + image.revisedPrompt());
        // 
        // assertNotNull(image.url());
        
        fail("TODO: Implement basic image generation test");
    }

    /**
     * Test 8.2: Image Generation with Options
     * <p>
     * TODO: Demonstrates how to generate images with specific configuration options.
     * 
     * Instructions:
     * 1. Create an OpenAI ImageModel with specific options
     * 2. Set size to "1024x1024"
     * 3. Set quality to "hd"
     * 4. Set style to "vivid"
     * 5. Generate an image with detailed configuration
     * 6. Verify the enhanced image quality
     */
    @Test
    void imageGenerationWithOptions() throws IOException {
        // TODO: Create OpenAI ImageModel with specific options
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(DALL_E_3)
        //         .size("1024x1024")
        //         .quality("hd")
        //         .style("vivid")
        //         .build();

        // TODO: Define a detailed prompt for high-quality generation
        // String prompt = "A futuristic cityscape at dawn with flying vehicles, neon lights reflecting on wet streets, cyberpunk aesthetic";
        
        // TODO: Generate the image with enhanced settings
        // Response<Image> response = model.generate(prompt);
        // Image image = response.content();
        
        // TODO: Display results and verify
        // System.out.println("High-quality generated image URL: " + image.url());
        // System.out.println("Revised prompt: " + image.revisedPrompt());
        
        // TODO: Optional - demonstrate downloading the image
        // if (image.url() != null) {
        //     System.out.println("Image generated successfully with HD quality!");
        // }
        
        // assertNotNull(image.url());
        
        fail("TODO: Implement image generation with options test");
    }

    /**
     * Test 8.3: Advanced Image Generation Configuration
     * <p>
     * TODO: Demonstrates generating images with different artistic styles and detailed prompts.
     * 
     * Instructions:
     * 1. Create an OpenAI ImageModel with production settings
     * 2. Generate images with different artistic styles
     * 3. Test both artistic and technical prompt styles
     * 4. Verify both images are generated successfully
     * 5. Compare the revised prompts with the original prompts
     */
    @Test
    void advancedImageGeneration() {
        // TODO: Create OpenAI ImageModel with production settings
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(DALL_E_3)
        //         .size("1024x1024")
        //         .quality("standard")
        //         .build();

        // TODO: Test artistic style variation
        // String artisticPrompt = "A serene Japanese garden with cherry blossoms, traditional architecture, and a koi pond, watercolor painting style";
        // Response<Image> artisticResponse = model.generate(artisticPrompt);
        // Image artisticImage = artisticResponse.content();
        
        // System.out.println("Artistic prompt: " + artisticPrompt);
        // System.out.println("Generated artistic image: " + artisticImage.url());
        
        // TODO: Test technical/detailed prompt
        // String technicalPrompt = "A detailed cross-section of a mechanical watch showing gears, springs, and intricate components, technical illustration style";
        // Response<Image> technicalResponse = model.generate(technicalPrompt);
        // Image technicalImage = technicalResponse.content();
        
        // System.out.println("Technical prompt: " + technicalPrompt);
        // System.out.println("Generated technical image: " + technicalImage.url());
        
        // TODO: Verify both images were generated successfully
        // assertNotNull(artisticImage.url());
        // assertNotNull(technicalImage.url());
        
        fail("TODO: Implement advanced image generation test");
    }

    /**
     * Test 8.4: Creative Image Generation Variations
     * <p>
     * TODO: Demonstrates generating multiple variations of images with different prompts.
     * 
     * Instructions:
     * 1. Create an ImageModel with consistent settings
     * 2. Generate images with different artistic styles
     * 3. Test various prompt engineering techniques
     * 4. Compare results between different approaches
     */
    @Test
    void creativeImageVariations() {
        // TODO: Create OpenAI ImageModel
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(DALL_E_3)
        //         .size("1024x1024")
        //         .build();

        // TODO: Define different artistic prompts
        // String[] prompts = {
        //     "A steampunk robot playing chess, detailed mechanical parts, brass and copper tones",
        //     "A minimalist abstract representation of music, flowing lines and geometric shapes",
        //     "A cozy library in a treehouse, warm lighting, books floating magically"
        // };
        
        // TODO: Generate and display multiple image variations
        // for (int i = 0; i < prompts.length; i++) {
        //     Response<Image> response = model.generate(prompts[i]);
        //     Image image = response.content();
        //     
        //     System.out.println("=== Variation " + (i + 1) + " ===");
        //     System.out.println("Original prompt: " + prompts[i]);
        //     System.out.println("Generated image URL: " + image.url());
        //     System.out.println("Revised prompt: " + image.revisedPrompt());
        //     System.out.println();
        //     
        //     assertNotNull(image.url());
        // }
        
        fail("TODO: Implement creative image variations test");
    }

    /**
     * Test 8.5: Base64 Image Generation with gpt-image-1 Model
     * <p>
     * TODO: Demonstrates using the new OpenAI "gpt-image-1" model that returns base64-encoded images.
     * 
     * Instructions:
     * 1. Create an OpenAI ImageModel using the new "gpt-image-1" model (no constant available yet)
     * 2. Generate an image with a creative prompt about a warrior cat and dragon
     * 3. Extract base64 data using image.base64Data() or parse from data URL
     * 4. Decode the base64 string using Java's Base64.getDecoder()
     * 5. Write the decoded bytes to a file in src/main/resources/
     * 6. Verify the file was created and contains valid image data
     * 7. Display educational output about the process
     */
    @Test
    void base64ImageGeneration() throws IOException {
        // TODO: Create OpenAI ImageModel with gpt-image-1 model
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName("gpt-image-1")  // No constant available yet for this model
        //         .build();

        // TODO: Define a creative prompt for image generation
        // String prompt = "A warrior cat rides a dragon into battle";
        
        // TODO: Generate the image
        // Response<Image> response = model.generate(prompt);
        // Image image = response.content();
        
        // TODO: Handle base64 data extraction with fallback
        // String base64Data = null;
        // if (image.base64Data() != null) {
        //     base64Data = image.base64Data();
        // } else if (image.url() != null && image.url().toString().startsWith("data:")) {
        //     // Fallback: parse from data URL format "data:image/png;base64,<data>"
        //     base64Data = image.url().toString().split(",")[1];
        // }
        
        // TODO: Verify we have base64 data
        // assertNotNull(base64Data, "Should have base64 image data");
        // assertFalse(base64Data.trim().isEmpty(), "Base64 data should not be empty");
        
        // TODO: Decode base64 to bytes and save to file
        // byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        // 
        // // Ensure directory exists
        // Path resourcesDir = Path.of("src/main/resources");
        // if (!Files.exists(resourcesDir)) {
        //     Files.createDirectories(resourcesDir);
        // }
        // 
        // Path outputPath = resourcesDir.resolve("generated_image_base64.png");
        // Files.write(outputPath, imageBytes);
        
        // TODO: Verify file creation and display results
        // assertTrue(Files.exists(outputPath), "Generated image file should exist");
        // assertTrue(Files.size(outputPath) > 0, "Generated image file should not be empty");
        // 
        // System.out.println("=== Base64 Image Generation Test ===");
        // System.out.println("Model: gpt-image-1 (OpenAI Responses API)");
        // System.out.println("Prompt: " + prompt);
        // System.out.println("Base64 data length: " + base64Data.length() + " characters");
        // System.out.println("Image file size: " + Files.size(outputPath) + " bytes");
        // System.out.println("Saved to: " + outputPath.toAbsolutePath());
        // System.out.println("Base64 decoding successful!");
        
        fail("TODO: Implement base64 image generation test");
    }
}