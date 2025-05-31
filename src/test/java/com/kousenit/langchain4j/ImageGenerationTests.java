package com.kousenit.langchain4j;

import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        //         .modelName("dall-e-3")
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
        //         .modelName("dall-e-3")
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
     * ImageGenerator interface for structured image generation using AiServices.
     */
    interface ImageGenerator {
        @dev.langchain4j.service.UserMessage("Generate an image: {{prompt}}")
        Image createImage(@V("prompt") String prompt);
        
        @dev.langchain4j.service.UserMessage("Create a {{style}} style image of: {{subject}}")
        Image createStyledImage(@V("style") String style, @V("subject") String subject);
    }

    /**
     * Test 8.3: Image Generation with AiServices
     * <p>
     * TODO: Demonstrates using image generation through the AiServices interface.
     * 
     * Instructions:
     * 1. Create an OpenAI ImageModel for AiServices
     * 2. Build an ImageGenerator service using AiServices
     * 3. Use the service to generate images with different methods
     * 4. Test both simple and styled image generation
     * 5. Verify the generated images
     */
    @Test
    void imageGenerationWithAiServices() {
        // TODO: Create OpenAI ImageModel for AiServices
        // ImageModel model = OpenAiImageModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName("dall-e-3")
        //         .build();

        // TODO: Create ImageGenerator service using AiServices
        // ImageGenerator generator = AiServices.builder(ImageGenerator.class)
        //         .imageModel(model)
        //         .build();

        // TODO: Test simple image generation
        // Image simpleImage = generator.createImage("A peaceful mountain lake surrounded by pine trees");
        // System.out.println("Simple generated image: " + simpleImage.url());
        
        // TODO: Test styled image generation
        // Image styledImage = generator.createStyledImage("impressionist painting", "a garden with blooming flowers");
        // System.out.println("Styled generated image: " + styledImage.url());
        
        // TODO: Verify both images were generated successfully
        // assertNotNull(simpleImage.url());
        // assertNotNull(styledImage.url());
        
        fail("TODO: Implement image generation with AiServices test");
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
        //         .modelName("dall-e-3")
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
}