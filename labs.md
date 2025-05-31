# LangChain4j Course Labs

This series of labs will guide you through building LangChain4j applications that use various capabilities of large language models. By the end of these exercises, you'll have hands-on experience with text generation, structured data extraction, prompt templates, chat memory, vision capabilities, and more.

> **Note:** This project uses LangChain4j 1.0.1. LangChain4j 1.0 includes significant API changes, including the new `ChatModel` interface (replacing `ChatLanguageModel`) and streamlined builder patterns.

## Table of Contents

- [Setup](#setup)
- [Lab 1: Basic Chat Interactions](#lab-1-basic-chat-interactions)
- [Lab 2: Streaming Responses](#lab-2-streaming-responses)
- [Lab 3: Structured Data Extraction](#lab-3-structured-data-extraction)
- [Lab 4: AI Services Interface](#lab-4-ai-services-interface)
- [Lab 5: Chat Memory](#lab-5-chat-memory)
- [Lab 6: AI Tools](#lab-6-ai-tools)
- [Lab 7: Vision Capabilities](#lab-7-vision-capabilities)
- [Lab 8: Image Generation](#lab-8-image-generation)
- [Lab 9: Audio Capabilities](#lab-9-audio-capabilities)
- [Lab 10: Retrieval-Augmented Generation (RAG)](#lab-10-retrieval-augmented-generation-rag)
- [Lab 11: Redis Vector Store for RAG](#lab-11-redis-vector-store-for-rag)
- [Conclusion](#conclusion)

## Setup

1. Make sure you have the following prerequisites:
   - Java 17+
   - An IDE (IntelliJ IDEA, Eclipse, VS Code)
   - API keys for OpenAI and/or Anthropic (Claude)

2. Set the required environment variables:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key
   export ANTHROPIC_API_KEY=your_anthropic_api_key  # Optional, for Claude exercises
   ```

3. Check that the project builds successfully:
   ```bash
   ./gradlew build
   ```

## Lab 1: Basic Chat Interactions

### 1.1 A Simple Query

In the test class (`OpenAiChatTests.java`), create a test method that sends a simple query to the OpenAI model using LangChain4j's `ChatModel`:

```java
@Test
void simpleQuery() {
    // Create OpenAI chat model using builder pattern
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    // Send a user message and get the response
    String response = model.chat("Why is the sky blue?");

    System.out.println(response);
    assertNotNull(response);
    assertFalse(response.isEmpty());
}
```

### 1.2 System Message

Modify the previous test to include a system message that changes the model's behavior:

```java
@Test
void simpleQueryWithSystemMessage() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    // Create system and user messages
    SystemMessage systemMessage = SystemMessage.from("You are a helpful assistant that responds like a pirate.");
    UserMessage userMessage = UserMessage.from("Why is the sky blue?");

    ChatResponse response = model.chat(systemMessage, userMessage);

    System.out.println(response.aiMessage().text());
    assertNotNull(response.aiMessage().text());
}
```

### 1.3 Accessing Response Metadata

Create a test that retrieves and displays the full `ChatResponse` object with metadata:

```java
@Test
void simpleQueryWithMetadata() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    UserMessage userMessage = UserMessage.from("Why is the sky blue?");
    ChatResponse response = model.chat(userMessage);

    assertNotNull(response);
    System.out.println("Content: " + response.aiMessage().text());
    System.out.println("Token Usage: " + response.tokenUsage());
    System.out.println("Finish Reason: " + response.finishReason());
}
```

Note how the `ChatResponse` object provides useful information about token usage and completion status.

[↑ Back to table of contents](#table-of-contents)

## Lab 2: Streaming Responses

### 2.1 Streaming with Reactive Streams

Create a test that streams the response using LangChain4j's streaming capabilities:

```java
@Test
void streamingChat() throws InterruptedException {
    StreamingChatModel model = OpenAiStreamingChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    String userMessage = "Tell me a story about a brave robot.";
    
    CountDownLatch latch = new CountDownLatch(1);
    StringBuilder fullResponse = new StringBuilder();

    model.chat(userMessage, new StreamingChatResponseHandler() {
        @Override
        public void onPartialResponse(String token) {
            System.out.print(token);
            fullResponse.append(token);
        }

        @Override
        public void onCompleteResponse(ChatResponse response) {
            System.out.println("\n\nStreaming completed!");
            System.out.println("Full response: " + fullResponse.toString());
            latch.countDown();
        }

        @Override
        public void onError(Throwable error) {
            System.err.println("Error: " + error.getMessage());
            latch.countDown();
        }
    });

    latch.await();
}
```

### 2.2 Streaming with Multiple Messages

Create a test that demonstrates streaming with conversation context:

```java
@Test
void streamingWithContext() throws InterruptedException {
    StreamingChatModel model = OpenAiStreamingChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    SystemMessage systemMessage = SystemMessage.from("You are a helpful coding assistant.");
    UserMessage userMessage = UserMessage.from("Explain recursion in simple terms.");
    
    CountDownLatch latch = new CountDownLatch(1);

    model.chat(Arrays.asList(systemMessage, userMessage), 
        new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                System.out.print(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("\n\nResponse completed with: " + response.finishReason());
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("Error occurred: " + error.getMessage());
                latch.countDown();
            }
        });

    latch.await(30, TimeUnit.SECONDS);
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 3: Structured Data Extraction

### 3.1 Create the Data Class

Create a record to represent structured data:

```java
public record ActorFilms(String actor, List<String> movies) {}
```

### 3.2 Single Entity Extraction

Create a test that extracts a single entity using LangChain4j's structured output capabilities:

```java
import com.fasterxml.jackson.databind.ObjectMapper;

@Test
void extractActorFilms() throws JsonProcessingException {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .responseFormat("json_object")
            .build();

    String prompt = """
            Generate the filmography for a random actor in the following JSON format:
            {
                "actor": "Actor Name",
                "movies": ["Movie 1", "Movie 2", "Movie 3", "Movie 4", "Movie 5"]
            }
            """;

    String response = model.chat(prompt);
    System.out.println("JSON Response: " + response);

    // Parse JSON manually using Jackson
    ObjectMapper objectMapper = new ObjectMapper();
    ActorFilms actorFilms = objectMapper.readValue(response, ActorFilms.class);

    // Verify the parsed data
    assertNotNull(response);
    assertTrue(response.contains("actor"));
    assertTrue(response.contains("movies"));
    assertNotNull(actorFilms);
    assertNotNull(actorFilms.actor());
    assertNotNull(actorFilms.movies());
    assertEquals(5, actorFilms.movies().size());
}
```

### 3.3 Using AiServices for Structured Data

LangChain4j provides `AiServices` for automatic parsing into Java objects:

```java
// Wrapper record for multiple actor filmographies
record ActorFilmographies(List<ActorFilms> filmographies) {}

interface ActorService {
    @SystemMessage("You are a movie database expert.")
    ActorFilms getActorFilmography(@UserMessage String actorName);
    
    @SystemMessage("You are a comprehensive movie database expert. Provide accurate filmographies.")
    ActorFilmographies getMultipleActorFilmographies(@UserMessage String actors);
}

@Test
void extractActorFilmsWithAiServices() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    ActorService service = AiServices.builder(ActorService.class)
            .chatModel(model)
            .build();

    ActorFilms actorFilms = service.getActorFilmography("Generate filmography for a random famous actor with exactly 5 movies");

    assertNotNull(actorFilms);
    assertNotNull(actorFilms.actor());
    assertNotNull(actorFilms.movies());
    assertEquals(5, actorFilms.movies().size());

    System.out.println("Actor: " + actorFilms.actor());
    actorFilms.movies().forEach(movie -> System.out.println("- " + movie));
}
```

### 3.4 Multiple Actor Filmographies

Test extracting multiple structured entities using the wrapper record pattern:

```java
@Test
void extractMultipleActorFilmographies() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    ActorService service = AiServices.builder(ActorService.class)
            .chatModel(model)
            .build();

    ActorFilmographies result = service.getMultipleActorFilmographies(
        "Return a JSON object with a 'filmographies' field containing an array of exactly 3 different famous actors. Each actor should have exactly 4 movies."
    );

    List<ActorFilms> filmographies = result.filmographies();
    
    assertNotNull(result);
    assertNotNull(filmographies);
    assertEquals(3, filmographies.size());
    
    // Verify each filmography has exactly 4 movies
    filmographies.forEach(actorFilms -> {
        assertNotNull(actorFilms.actor());
        assertNotNull(actorFilms.movies());
        assertEquals(4, actorFilms.movies().size());
    });
}
```

### 3.5 Advanced Variable Substitution

Use the `@V` annotation for dynamic prompt parameters:

```java
interface AdvancedActorService {
    @SystemMessage("You are an expert movie database assistant specializing in actor filmographies.")
    @UserMessage("Generate filmography for {{actorName}} with exactly {{movieCount}} of their most famous movies")
    ActorFilms getSpecificActorFilmography(
        @V("actorName") String actorName, 
        @V("movieCount") int movieCount
    );
}

@Test
void advancedStructuredDataExtraction() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    AdvancedActorService service = AiServices.builder(AdvancedActorService.class)
            .chatModel(model)
            .build();

    ActorFilms actorFilms = service.getSpecificActorFilmography("Tom Hanks", 6);

    assertNotNull(actorFilms);
    assertTrue(actorFilms.actor().toLowerCase().contains("hanks"));
    assertEquals(6, actorFilms.movies().size());
}
```

**Important Notes for Lab 3:**
- Add Jackson dependency for JSON parsing: `com.fasterxml.jackson.core:jackson-databind`
- Use wrapper records like `ActorFilmographies` for better parsing of collections
- The `@SystemMessage`, `@UserMessage`, and `@V` annotations are from `dev.langchain4j.service`
- Use `.chatModel()` method (not `.chatLanguageModel()`) in LangChain4j 1.0.1

[↑ Back to table of contents](#table-of-contents)

## Lab 4: AI Services Interface

### 4.1 Create a Service Interface

Define a high-level service interface for your AI application:

```java
interface FilmographyService {
    
    @SystemMessage("You are a helpful assistant that provides accurate information about actors and their movies.")
    List<String> getMovies(@UserMessage String actor);
    
    @SystemMessage("You are a movie expert. Provide detailed analysis.")
    String analyzeActor(@UserMessage String actorName);
    
    ActorFilms getFullFilmography(String actorName);
}
```

### 4.2 Implement the Service

Create a test that uses the `AiServices` to implement the interface:

```java
@Test
void useFilmographyService() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    FilmographyService service = AiServices.builder(FilmographyService.class)
            .chatModel(model)
            .build();

    // Test simple movie list
    List<String> tomHanksMovies = service.getMovies("Tom Hanks");
    System.out.println("Tom Hanks movies: " + tomHanksMovies);
    
    // Test actor analysis
    String analysis = service.analyzeActor("Meryl Streep");
    System.out.println("Meryl Streep analysis: " + analysis);

    assertNotNull(tomHanksMovies);
    assertFalse(tomHanksMovies.isEmpty());
    assertNotNull(analysis);
}
```

### 4.3 Service with Memory and Tools

Create an advanced service that combines memory and tools:

```java
interface PersonalAssistant {
    String chat(String message);
}

@Test
void personalAssistantWithMemoryAndTools() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    PersonalAssistant assistant = AiServices.builder(PersonalAssistant.class)
            .chatModel(model)
            .chatMemory(memory)
            .tools(new DateTimeTool())
            .build();

    // Have a conversation that uses both memory and tools
    String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer.");
    System.out.println("Response 1: " + response1);

    String response2 = assistant.chat("What's my name and what year will it be in 3 years?");
    System.out.println("Response 2: " + response2);

    // Verify memory and tool usage
    assertTrue(response2.toLowerCase().contains("alice"));
    assertNotNull(response2);
}
```

### 4.4 Advanced Service Configuration

Create a more sophisticated service with custom configuration:

```java
interface DocumentAnalyzer {
    @SystemMessage("You are an expert document analyzer. Provide concise, accurate analysis.")
    @UserMessage("Analyze this document content and provide key insights: {{content}}")
    String analyzeDocument(@V("content") String documentContent);
    
    @UserMessage("Extract the main themes from: {{content}}")
    List<String> extractThemes(@V("content") String documentContent);
    
    @UserMessage("Rate the sentiment of this content from 1-10: {{content}}")
    int analyzeSentiment(@V("content") String documentContent);
}

@Test
void advancedServiceConfiguration() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.3)  // Lower temperature for more consistent analysis
            .build();

    DocumentAnalyzer analyzer = AiServices.builder(DocumentAnalyzer.class)
            .chatModel(model)
            .build();

    String sampleContent = """
        The quarterly earnings report shows strong growth in the technology sector,
        with cloud computing services leading the way. Customer satisfaction remains high,
        though there are concerns about increasing competition and market saturation.
        """;

    String analysis = analyzer.analyzeDocument(sampleContent);
    List<String> themes = analyzer.extractThemes(sampleContent);
    int sentiment = analyzer.analyzeSentiment(sampleContent);

    System.out.println("Analysis: " + analysis);
    System.out.println("Themes: " + themes);
    System.out.println("Sentiment: " + sentiment);

    assertNotNull(analysis);
    assertNotNull(themes);
    assertFalse(themes.isEmpty());
    assertTrue(sentiment >= 1 && sentiment <= 10);
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 5: Chat Memory

### 5.1 Demonstrating Stateless Behavior

All requests to AI models are stateless by default. Create a test that demonstrates this:

```java
@Test
void defaultRequestsAreStateless() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    System.out.println("First interaction:");
    String response1 = model.chat("My name is Inigo Montoya. You killed my father. Prepare to die.");
    System.out.println(response1);

    System.out.println("\nSecond interaction:");
    String response2 = model.chat("Who am I?");
    System.out.println(response2);

    // Verify the model doesn't remember the previous conversation
    assertFalse(response2.toLowerCase().contains("inigo montoya"),
            "The model should not remember previous conversations without memory");
}
```

### 5.2 Adding Memory to Retain Conversation State

Use LangChain4j's `ChatMemory` to maintain conversation state:

```java
@Test
void requestsWithMemory() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    System.out.println("First interaction with memory:");
    UserMessage firstMessage = UserMessage.from("My name is Inigo Montoya. You killed my father. Prepare to die.");
    memory.add(firstMessage);
    
    ChatResponse response1 = model.chat(memory.messages());
    memory.add(response1.content());
    System.out.println(response1.aiMessage().text());

    System.out.println("\nSecond interaction with memory:");
    UserMessage secondMessage = UserMessage.from("Who am I?");
    memory.add(secondMessage);
    
    ChatResponse response2 = model.chat(memory.messages());
    memory.add(response2.content());
    System.out.println(response2.aiMessage().text());

    // Verify the model correctly identifies the user
    assertTrue(response2.aiMessage().text().toLowerCase().contains("inigo montoya"),
            "The model should remember the user's identity when using memory");
}
```

### 5.3 Using Different Memory Types

LangChain4j provides different memory implementations:

```java
@Test
void differentMemoryTypes() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    // Token-based memory - limits based on token count
    ChatMemory tokenMemory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer(GPT_4_1_NANO));
    
    // Message-based memory - limits based on message count
    ChatMemory messageMemory = MessageWindowChatMemory.withMaxMessages(5);

    // Add some conversation history
    tokenMemory.add(UserMessage.from("Hello, I'm learning about AI."));
    tokenMemory.add(AiMessage.from("That's great! I'm here to help you learn."));

    // Test with token memory
    UserMessage newMessage = UserMessage.from("What did I just tell you about myself?");
    tokenMemory.add(newMessage);
    
    ChatResponse response = model.chat(tokenMemory.messages());
    System.out.println("Token memory response: " + response.aiMessage().text());
    
    assertNotNull(response.aiMessage().text());
}
```

### 5.4 Memory with AiServices

You can also use memory with `AiServices` for persistent conversations:

```java
interface AssistantWithMemory {
    String chat(String message);
}

@Test
void aiServicesWithMemory() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    AssistantWithMemory assistant = AiServices.builder(AssistantWithMemory.class)
            .chatModel(model)
            .chatMemory(memory)
            .build();

    String response1 = assistant.chat("Hi, my name is Alice and I'm a software developer.");
    System.out.println("Response 1: " + response1);

    String response2 = assistant.chat("What's my name and what do I do for work?");
    System.out.println("Response 2: " + response2);

    // Verify memory is working
    assertTrue(response2.toLowerCase().contains("alice"));
    assertTrue(response2.toLowerCase().contains("software") || response2.toLowerCase().contains("developer"));
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 6: AI Tools

### 6.1 Understanding Tool Classes

Tool classes are already provided in `src/main/java/com/kousenit/langchain4j/`:

**DateTimeTool.java** - Date and time functionality:
```java
@Tool("Get the current date and time")
public String getCurrentDateTime() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
}

@Tool("Get the date that is a specified number of years from now")
public String getDateYearsFromNow(int years) {
    return LocalDate.now().plusYears(years).toString();
}

@Tool("Set an alarm for a specific time")
public String setAlarm(String time) {
    return "Alarm set for " + time + ". You will be notified at the specified time.";
}
```

**WeatherTool.java** - Weather simulation:
```java
@Tool("Get the current weather for a specific city")
public String getCurrentWeather(String city, String units) {
    String tempUnit = units.equals("metric") ? "C" : "F";
    int temperature = units.equals("metric") ? 22 : 72;
    return String.format("The current weather in %s is %d°%s and sunny with light clouds.",
                       city, temperature, tempUnit);
}
```

**CalculatorTool.java** - Mathematical operations:
```java
@Tool("Add two numbers")
public double add(double a, double b) {
    return a + b;
}

@Tool("Divide the first number by the second number")
public double divide(double a, double b) {
    if (b == 0) {
        throw new IllegalArgumentException("Cannot divide by zero");
    }
    return a / b;
}
```

These classes demonstrate the `@Tool` annotation pattern for creating AI-callable functions.

### 6.2 Use Tools with AiServices

Create a test that uses the tools with LangChain4j's `AiServices`:

```java
interface Assistant {
    String chat(String message);
}

@Test
void useToolsWithAiServices() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .tools(new DateTimeTool())
            .build();

    String response1 = assistant.chat("What day is tomorrow?");
    System.out.println("Response 1: " + response1);

    String response2 = assistant.chat("What year will it be in 5 years?");
    System.out.println("Response 2: " + response2);

    String response3 = assistant.chat("Set an alarm for 8:00 AM tomorrow");
    System.out.println("Response 3: " + response3);

    assertNotNull(response1);
    assertNotNull(response2);
    assertNotNull(response3);
}
```

### 6.3 Tools with Parameters

Use the provided `WeatherTool` to demonstrate how tools can accept parameters:

@Test
void useWeatherTool() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .tools(new WeatherTool())
            .build();

    String response = assistant.chat("What's the weather like in Paris? Use metric units.");
    System.out.println("Weather response: " + response);

    assertNotNull(response);
    assertTrue(response.contains("Paris") || response.contains("22°C"));
}
```

### 6.4 Multiple Tools

Use multiple provided tools together (`DateTimeTool`, `CalculatorTool`, and `WeatherTool`):

@Test
void useMultipleTools() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .tools(new DateTimeTool(), new CalculatorTool(), new WeatherTool())
            .build();

    String response = assistant.chat("What's 15 multiplied by 8, and what year will it be in 3 years?");
    System.out.println("Multi-tool response: " + response);

    assertNotNull(response);
    // Should contain both calculation result and year
    assertTrue(response.contains("120") || response.contains("calculation"));
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 7: Vision Capabilities

Vision capabilities allow AI models to analyze and understand images. This lab demonstrates how to use GPT-4 with vision to analyze both local and remote images using LangChain4j.

**Prerequisites:** 
- An image file `bowl_of_fruit.jpg` in `src/main/resources/`
- OpenAI API key with access to GPT-4 vision models

**Lab Structure:**
This lab includes 3 progressive vision tests:
1. **Local Image Analysis** - Analyze images from local resources
2. **Remote Image Analysis** - Analyze images from URLs  
3. **Structured Image Analysis** - Extract structured data from images

### 7.1 Local Image Analysis

Create a test that analyzes a local image file:

```java
@Test
void localImageAnalysis() throws IOException {
    // Create GPT-4 model with vision capabilities
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_MINI)
            .build();

    // Load image from resources with null check
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

    // Verify the response contains image-related content
    assertThat(response.toLowerCase())
            .as("Image analysis response")
            .containsAnyOf("image", "see", "picture", "fruit", "bowl", "color");
}
```

### 7.2 Remote Image Analysis

Create a test that analyzes an image from a remote URL:

```java
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
```

### 7.3 Structured Image Analysis

Demonstrate extracting structured data from image analysis results:

```java
/**
 * DetailedAnalyst interface for comprehensive image analysis.
 */
interface DetailedAnalyst {
    @UserMessage("Provide a comprehensive analysis of this image including: objects, colors, composition, mood, and any text. Image: {{image}}")
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
```

**Important Notes for Lab 7:**
- Uses GPT-4-1-Mini model which supports vision capabilities
- Includes proper null checks for resource loading to avoid NullPointerException
- Uses Base64 encoding for local images and direct URLs for remote images  
- Demonstrates both simple string responses and structured data extraction
- Uses AssertJ and JUnit 5 assertAll() for comprehensive testing
- Rate limiting: Consider adding delays between API calls if running multiple vision tests

[↑ Back to table of contents](#table-of-contents)

## Lab 8: Image Generation

### 8.1 Basic Image Generation

Create a test that generates an image using OpenAI's DALL-E:

```java
@Test
void generateImage() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("dall-e-3")
            .build();

    String prompt = "A warrior cat rides a dragon into battle, digital art style";
    
    Response<Image> response = model.generate(prompt);
    
    assertNotNull(response);
    assertNotNull(response.content());
    
    Image image = response.content();
    System.out.println("Generated image URL: " + image.url());
    System.out.println("Revised prompt: " + image.revisedPrompt());
}
```

### 8.2 Image Generation with Options

Create a test with more specific generation options:

```java
@Test
void generateImageWithOptions() throws IOException {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("dall-e-3")
            .size("1024x1024")
            .quality("hd")
            .style("vivid")
            .build();

    String prompt = "A futuristic cityscape at sunset with flying cars, cyberpunk style";
    
    Response<Image> response = model.generate(prompt);
    Image image = response.content();
    
    System.out.println("Generated image URL: " + image.url());
    
    // Optionally download and save the image
    if (image.url() != null) {
        // You could use Java's HTTP client to download the image
        System.out.println("Image generated successfully!");
    }
    
    assertNotNull(image.url());
}
```

### 8.3 Image Generation with AiServices

You can also use image generation with `AiServices`:

```java
interface ImageGenerator {
    @UserMessage("Generate an image: {{prompt}}")
    Image createImage(@V("prompt") String prompt);
}

@Test
void imageGenerationWithAiServices() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("dall-e-3")
            .build();

    ImageGenerator generator = AiServices.builder(ImageGenerator.class)
            .imageModel(model)
            .build();

    Image image = generator.createImage("A peaceful mountain lake at sunrise");
    
    System.out.println("Generated image: " + image.url());
    assertNotNull(image.url());
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 9: Audio Capabilities

### 9.1 Text-to-Speech

Note: LangChain4j 1.0 may not include direct audio models. This section demonstrates the pattern for when audio support is available:

```java
@Test
void textToSpeech() {
    // This would typically use an external service like OpenAI's TTS API
    // or integrate with Java sound libraries
    
    String text = "Welcome to LangChain4j, a powerful framework for building AI applications in Java.";
    
    // Example implementation would go here
    // For now, we'll simulate the functionality
    System.out.println("Text to convert to speech: " + text);
    
    // In a real implementation:
    // 1. Call TTS service
    // 2. Receive audio bytes
    // 3. Save to file or play directly
    
    assertTrue(text.length() > 0);
}
```

### 9.2 Speech-to-Text

```java
@Test
void speechToText() {
    // This would typically use an external service like OpenAI's Whisper API
    // or integrate with Java speech recognition libraries
    
    // Simulate loading an audio file
    String audioFileName = "test_audio.wav";
    
    // Example implementation would go here
    System.out.println("Processing audio file: " + audioFileName);
    
    // In a real implementation:
    // 1. Load audio file
    // 2. Send to transcription service
    // 3. Receive transcribed text
    
    String transcription = "This is a sample transcription";
    System.out.println("Transcription: " + transcription);
    
    assertNotNull(transcription);
}
```

### 9.3 Audio with External Services

For production audio capabilities, you would integrate with external services:

```java
interface AudioService {
    @UserMessage("Transcribe this audio and summarize the main points")
    String transcribeAndSummarize(byte[] audioData);
}

@Test
void audioServiceIntegration() {
    // This demonstrates the pattern for audio integration
    // In practice, you'd implement actual audio processing
    
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    // Simulate audio data
    byte[] audioData = "simulated audio data".getBytes();
    
    // In a real implementation, you would:
    // 1. Use a transcription service to convert audio to text
    // 2. Pass the transcribed text to LangChain4j for processing
    
    String simulatedTranscription = "This is a meeting about quarterly sales figures and growth projections.";
    String summary = model.chat("Summarize the main points: " + simulatedTranscription);
    
    System.out.println("Audio summary: " + summary);
    assertNotNull(summary);
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 10: Retrieval-Augmented Generation (RAG)

### 10.1 Basic Document Loading and Embedding

Create a test that demonstrates document loading and embedding:

```java
@Test
void basicDocumentEmbedding() {
    // Create embedding model
    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    
    // Create in-memory embedding store
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    // Create some sample documents
    List<Document> documents = Arrays.asList(
        Document.from("LangChain4j is a Java library for building AI applications."),
        Document.from("It provides integration with various language models like OpenAI and Anthropic."),
        Document.from("LangChain4j supports RAG, tools, memory, and streaming responses."),
        Document.from("The library uses a builder pattern for configuration.")
    );

    // Split documents into segments
    DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
    List<TextSegment> segments = splitter.splitAll(documents);

    // Embed and store segments
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    System.out.println("Embedded " + segments.size() + " document segments");
    
    // Test similarity search
    String query = "What is LangChain4j?";
    Embedding queryEmbedding = embeddingModel.embed(query).content();
    
    List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, 2);
    
    System.out.println("Found " + matches.size() + " relevant segments:");
    matches.forEach(match -> 
        System.out.println("- " + match.embedded().text() + " (score: " + match.score() + ")")
    );

    assertFalse(matches.isEmpty());
}
```

### 10.2 RAG with ContentRetriever

Create a more sophisticated RAG implementation:

```java
@Test
void ragWithContentRetriever() {
    // Set up models
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    // Load and process documents
    List<Document> documents = Arrays.asList(
        Document.from("Java is a programming language and computing platform first released by Sun Microsystems in 1995."),
        Document.from("Java is object-oriented, class-based, and designed to have as few implementation dependencies as possible."),
        Document.from("Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM)."),
        Document.from("Java is one of the most popular programming languages in use, particularly for client-server web applications.")
    );

    DocumentSplitter splitter = DocumentSplitters.recursive(200, 50);
    List<TextSegment> segments = splitter.splitAll(documents);
    
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    // Create content retriever
    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(2)
            .minScore(0.5)
            .build();

    // Create RAG-enabled assistant
    interface RagAssistant {
        String answer(String question);
    }

    RagAssistant assistant = AiServices.builder(RagAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    // Test RAG
    String question = "When was Java first released?";
    String answer = assistant.answer(question);
    
    System.out.println("Question: " + question);
    System.out.println("Answer: " + answer);

    assertNotNull(answer);
    assertTrue(answer.contains("1995"));
}
```

### 10.3 RAG with File Documents

Create a test that loads documents from files:

```java
@Test
void ragWithFileDocuments() throws IOException {
    // Create a sample text file for testing
    Path tempFile = Files.createTempFile("sample", ".txt");
    Files.writeString(tempFile, """
        LangChain4j is a powerful Java library for building applications with Large Language Models (LLMs).
        
        Key features include:
        - Integration with multiple AI providers (OpenAI, Anthropic, etc.)
        - Support for chat memory and conversation context
        - Tool/function calling capabilities
        - Retrieval-Augmented Generation (RAG)
        - Streaming responses
        - Image and audio processing
        
        The library follows modern Java practices and uses builder patterns for configuration.
        It provides both low-level and high-level APIs for different use cases.
        """);

    try {
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Load document from file
        Document document = FileSystemDocumentLoader.loadDocument(tempFile);
        
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        List<TextSegment> segments = splitter.split(document);
        
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        interface DocumentAssistant {
            String answer(String question);
        }

        DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();

        String answer = assistant.answer("What are the key features of LangChain4j?");
        
        System.out.println("Answer based on document: " + answer);
        
        assertNotNull(answer);
        assertTrue(answer.toLowerCase().contains("langchain4j") || 
                  answer.toLowerCase().contains("feature"));

    } finally {
        Files.deleteIfExists(tempFile);
    }
}
```

### 10.4 Advanced RAG with Metadata Filtering

Create a more advanced RAG system that uses metadata for filtering:

```java
@Test
void ragWithMetadataFiltering() {
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    // Create documents with metadata
    List<Document> javaDocs = Arrays.asList(
        Document.from("Java was created by James Gosling at Sun Microsystems.")
                .toBuilder().metadata("language", "java").metadata("topic", "history").build(),
        Document.from("Java uses automatic memory management with garbage collection.")
                .toBuilder().metadata("language", "java").metadata("topic", "memory").build()
    );

    List<Document> pythonDocs = Arrays.asList(
        Document.from("Python was created by Guido van Rossum in 1991.")
                .toBuilder().metadata("language", "python").metadata("topic", "history").build(),
        Document.from("Python uses reference counting for memory management.")
                .toBuilder().metadata("language", "python").metadata("topic", "memory").build()
    );

    List<Document> allDocs = new ArrayList<>();
    allDocs.addAll(javaDocs);
    allDocs.addAll(pythonDocs);

    DocumentSplitter splitter = DocumentSplitters.recursive(200, 50);
    List<TextSegment> segments = splitter.splitAll(allDocs);
    
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    // Create retriever with metadata filtering
    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(2)
            // Note: Metadata filtering implementation depends on the specific embedding store
            .build();

    interface LanguageAssistant {
        @SystemMessage("Answer questions based only on the provided context about programming languages.")
        String answerAboutLanguage(String question);
    }

    LanguageAssistant assistant = AiServices.builder(LanguageAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    String answer = assistant.answerAboutLanguage("Who created Java and when?");
    
    System.out.println("Answer: " + answer);
    assertNotNull(answer);
    assertTrue(answer.toLowerCase().contains("james gosling") || answer.toLowerCase().contains("sun"));
}
```

[↑ Back to table of contents](#table-of-contents)

## Lab 11: Redis Vector Store for RAG

### 11.1 Prerequisites

To use Redis as a vector store, you need a running Redis instance with vector search capabilities:

```bash
docker run -p 6379:6379 redis/redis-stack:latest
```

### 11.2 Basic Redis Vector Store Setup

Create a test that demonstrates Redis vector store usage:

```java
@Test
void redisVectorStoreBasic() {
    // Skip test if Redis is not available
    assumeTrue(isRedisAvailable(), "Redis is not available");

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    
    // Create Redis embedding store
    EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .host("localhost")
            .port(6379)
            .dimension(384) // AllMiniLM dimension size
            .indexName("langchain4j-test")
            .build();

    // Sample documents about programming languages
    List<Document> documents = Arrays.asList(
        Document.from("Python is a high-level programming language known for its simplicity and readability."),
        Document.from("Java is a popular object-oriented programming language that runs on the JVM."),
        Document.from("JavaScript is the language of the web, used for both frontend and backend development."),
        Document.from("Rust is a systems programming language focused on safety and performance.")
    );

    DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
    List<TextSegment> segments = splitter.splitAll(documents);
    
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    System.out.println("Added " + segments.size() + " segments to Redis");

    // Test search
    String query = "What language is good for web development?";
    Embedding queryEmbedding = embeddingModel.embed(query).content();
    
    List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, 2);
    
    System.out.println("Search results for: " + query);
    matches.forEach(match -> 
        System.out.println("- " + match.embedded().text() + " (score: " + match.score() + ")")
    );

    assertFalse(matches.isEmpty());
}

private boolean isRedisAvailable() {
    try (Jedis jedis = new Jedis("localhost", 6379)) {
        jedis.ping();
        return true;
    } catch (Exception e) {
        return false;
    }
}
```

### 11.3 RAG with Redis Persistence

Create a comprehensive RAG system using Redis:

```java
@Test
void ragWithRedisPersistence() {
    assumeTrue(isRedisAvailable(), "Redis is not available");

    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    
    EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .host("localhost")
            .port(6379)
            .dimension(384)
            .indexName("rag-knowledge-base")
            .build();

    // Knowledge base about LangChain4j
    List<Document> knowledgeBase = Arrays.asList(
        Document.from("LangChain4j 1.0 introduced the ChatModel interface as the primary way to interact with language models."),
        Document.from("The AiServices interface in LangChain4j allows you to create type-safe AI-powered services using annotations."),
        Document.from("LangChain4j supports multiple embedding models including OpenAI embeddings and local models like AllMiniLM."),
        Document.from("ContentRetriever in LangChain4j is used to retrieve relevant content for RAG applications."),
        Document.from("LangChain4j provides built-in support for Redis as a vector store for production RAG systems.")
    );

    DocumentSplitter splitter = DocumentSplitters.recursive(200, 50);
    List<TextSegment> segments = splitter.splitAll(knowledgeBase);
    
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(3)
            .minScore(0.6)
            .build();

    interface KnowledgeAssistant {
        @SystemMessage("You are a helpful assistant that answers questions about LangChain4j based on the provided context. " +
                      "If the context doesn't contain enough information to answer the question, say so.")
        String answer(String question);
    }

    KnowledgeAssistant assistant = AiServices.builder(KnowledgeAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    // Test the RAG system
    String[] questions = {
        "What is the primary interface for chat in LangChain4j 1.0?",
        "How does LangChain4j support type-safe AI services?",
        "What vector stores does LangChain4j support?"
    };

    for (String question : questions) {
        String answer = assistant.answer(question);
        System.out.println("Q: " + question);
        System.out.println("A: " + answer);
        System.out.println();
        
        assertNotNull(answer);
        assertFalse(answer.trim().isEmpty());
    }
}
```

### 11.4 Data Persistence and Cleanup

Create utilities for managing the Redis vector store:

```java
@Test
void redisDataManagement() {
    assumeTrue(isRedisAvailable(), "Redis is not available");

    EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .host("localhost")
            .port(6379)
            .dimension(384)
            .indexName("test-cleanup")
            .build();

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();

    // Add some test data
    TextSegment segment = TextSegment.from("This is test data for cleanup demonstration.");
    Embedding embedding = embeddingModel.embed(segment).content();
    
    String id = embeddingStore.add(embedding, segment);
    System.out.println("Added segment with ID: " + id);

    // Verify data exists
    List<EmbeddingMatch<TextSegment>> results = embeddingStore.findRelevant(embedding, 1);
    assertFalse(results.isEmpty());
    
    // Clean up (if your Redis implementation supports it)
    // Note: Cleanup methods may vary depending on the specific Redis embedding store implementation
    System.out.println("Data management test completed");
}
```

### 11.5 Production RAG Configuration

Create a more production-ready RAG configuration:

```java
@Test
void productionRagConfiguration() {
    assumeTrue(isRedisAvailable(), "Redis is not available");

    // Configure models with production settings
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.1) // Lower temperature for more consistent responses
            .maxTokens(500)
            .build();

    EmbeddingModel embeddingModel = AllMiniLmL6V2EmbeddingModel.builder().build();
    
    // Configure Redis with production settings
    EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .host("localhost")
            .port(6379)
            .dimension(384)
            .indexName("production-rag")
            .prefix("prod:")
            .build();

    // Load comprehensive knowledge base
    List<Document> documents = Arrays.asList(
        Document.from("LangChain4j is a Java library that provides abstractions for working with Large Language Models."),
        Document.from("It supports multiple providers including OpenAI, Anthropic, and local models."),
        Document.from("The library includes features for chat memory, function calling, and document processing."),
        Document.from("RAG (Retrieval-Augmented Generation) allows AI to access external knowledge sources."),
        Document.from("Vector stores like Redis enable persistent storage of embeddings for RAG applications."),
        Document.from("LangChain4j uses builder patterns for configuring AI services and models."),
        Document.from("The @Tool annotation enables AI models to call Java methods during conversations."),
        Document.from("AiServices provides a high-level interface for creating AI-powered applications.")
    );

    DocumentSplitter splitter = DocumentSplitters.recursive(150, 30);
    List<TextSegment> segments = splitter.splitAll(documents);
    
    // Add metadata for better retrieval
    for (int i = 0; i < segments.size(); i++) {
        TextSegment segment = segments.get(i);
        segment.metadata().put("chunk_id", String.valueOf(i));
        segment.metadata().put("source", "langchain4j_docs");
        segment.metadata().put("created_at", LocalDateTime.now().toString());
    }
    
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    // Configure retriever with optimized settings
    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(4)
            .minScore(0.7) // Higher threshold for production
            .build();

    interface ProductionAssistant {
        @SystemMessage("You are an expert assistant for LangChain4j documentation. " +
                      "Provide accurate, helpful answers based on the context provided. " +
                      "If you cannot answer based on the context, clearly state that.")
        String answer(String question);
    }

    ProductionAssistant assistant = AiServices.builder(ProductionAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    // Test with various question types
    String[] testQuestions = {
        "What is LangChain4j and what does it do?",
        "How do I use tools with LangChain4j?",
        "What is RAG and how does it work with LangChain4j?",
        "How do I configure Redis as a vector store?"
    };

    for (String question : testQuestions) {
        String answer = assistant.answer(question);
        System.out.println("Q: " + question);
        System.out.println("A: " + answer);
        System.out.println("---");
        
        assertNotNull(answer);
        assertFalse(answer.trim().isEmpty());
    }
}
```

[↑ Back to table of contents](#table-of-contents)

## Conclusion

Congratulations! You've completed a comprehensive tour of LangChain4j's capabilities. You've learned how to:

- Interact with LLMs through LangChain4j's `ChatModel` interface
- Stream responses for better user experience  
- Extract structured data from LLM responses using `AiServices`
- Use prompt templates for consistent prompting
- Maintain conversation state with `ChatMemory`
- Work with vision capabilities for image analysis
- Generate images using AI models
- Extend AI capabilities with custom tools using `@Tool` annotations
- Create high-level AI services with `AiServices` interface
- Build Retrieval-Augmented Generation (RAG) systems with document processing
- Use Redis as a persistent vector store for production RAG applications

These skills provide a solid foundation for building AI-powered applications using LangChain4j and the Java ecosystem. The patterns you've learned can be extended and combined to create sophisticated AI applications tailored to your specific needs.

Key takeaways:
- LangChain4j 1.0 uses `ChatModel` as the primary interface
- Builder patterns provide flexible configuration
- `AiServices` enables type-safe, annotation-driven AI integration
- RAG systems combine retrieval and generation for more accurate responses
- Redis provides production-ready vector storage for scalable applications

Continue exploring the LangChain4j documentation and community examples to further enhance your AI application development skills.