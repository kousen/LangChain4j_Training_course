# LangChain4j Course Labs

This series of labs will guide you through building LangChain4j applications that use various capabilities of large language models. By the end of these exercises, you'll have hands-on experience with text generation, structured data extraction, prompt templates, chat memory, vision capabilities, and more.

> **Note:** This project is pinned to LangChain4j 1.15.0. The `ChatModel` interface (and the rest of the LangChain4j 1.x surface) is stable here. The course covers the agentic API plus the 1.15 voting pattern, MCP client (spec 2025-11-25), built-in OpenAI transcription, gpt-image-2 image generation, `@Tool` parameters with `defaultValue`, and the now-built-in hybrid search support in PgVector / Elasticsearch.

## Table of Contents

- [Setup](#setup)
- [Lab 1: Basic Chat Interactions](#lab-1-basic-chat-interactions)
- [Lab 2: Streaming Responses](#lab-2-streaming-responses)
- [Lab 3: Structured Data Extraction](#lab-3-structured-data-extraction)
- [Lab 4: AI Services Interface](#lab-4-ai-services-interface)
- [Lab 5: Chat Memory](#lab-5-chat-memory)
- [Lab 6: AI Tools](#lab-6-ai-tools)
- [Lab 6.5: MCP Integration](#lab-65-mcp-integration)
- [Lab 7: Multimodal Capabilities](#lab-7-multimodal-capabilities)
- [Lab 8: Image Generation](#lab-8-image-generation)
- [Lab 9: Retrieval-Augmented Generation (RAG)](#lab-9-retrieval-augmented-generation-rag)
- [Lab 10: Chroma Vector Store for RAG](#lab-10-chroma-vector-store-for-rag)
- [Lab 11: Agentic API](#lab-11-agentic-api)
- [Conclusion](#conclusion)

## Setup

1. Make sure you have the following prerequisites:
   - Java 17+ (the Gradle wrapper is 9.1+, so Java 25 is supported)
   - An IDE (IntelliJ IDEA, Eclipse, VS Code)
   - API key for OpenAI

2. Set the required environment variables:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key
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
- Use `.chatModel()` method (not `.chatLanguageModel()`) in LangChain4j 1.0+

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

### 6.5 Optional Tool Parameters

LangChain4j 1.12 added `Optional<T>` support for tool parameters. Use it when the absence of a value is meaningful and the tool itself should decide how to handle the gap. The same effect is available via `@P(required = false)`; pick `Optional<T>` when the type itself should communicate the optionality.

`WeatherTool.getWeatherWithDefault` demonstrates the pattern:

```java
@Tool("Get the current weather; units defaults to metric if not specified")
public String getWeatherWithDefault(
        @P("City name") String city,
        @P("Unit system: metric or imperial") Optional<String> units) {
    return getCurrentWeather(city, units.orElse("metric"));
}
```

Test it from `AiToolsTests`:

```java
@Test
void optionalToolParameters() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .tools(new WeatherTool())
            .build();

    String response = assistant.chat("What's the weather in Berlin? I don't care about units.");
    System.out.println("Response: " + response);

    assertThat(response).containsIgnoringCase("berlin");
}
```

### 6.6 Default Tool Parameter Values

LangChain4j 1.15 added `@P(defaultValue = "...")`. The parameter is marked optional in the JSON schema sent to the LLM; if the LLM omits it, LangChain4j parses the declared default and substitutes it before invoking the method. Use it when the tool has a sensible fallback the model shouldn't have to think about.

Compare to Lab 6.5's `Optional<T>`: pick `Optional<T>` when absence is meaningful business logic, and `defaultValue` when the tool simply has a good default. **You cannot combine the two on the same parameter** — `AiServices.builder(...).tools(...).build()` will throw `IllegalConfigurationException`.

Supported default-value types include `String`, primitives and their boxed equivalents, `enum`, `UUID`, `BigDecimal`/`BigInteger`, `List`/`Set`/arrays (as JSON arrays), `Map` (as JSON object), and POJOs (as JSON objects). Defaults are re-parsed on every invocation, so a tool that mutates a defaulted collection won't poison later calls.

`ArticleSearchTool` demonstrates three of the most useful shapes — a primitive, an enum, and a `List<String>`:

```java
public class ArticleSearchTool {

    public enum SortBy { RELEVANCE, DATE, RATING }

    @Tool("Search articles matching a query. Optional: limit, sortBy, languages.")
    public String searchArticles(
            @P("Search query") String query,
            @P(value = "Maximum number of results", defaultValue = "10") int limit,
            @P(value = "Sort order", defaultValue = "RELEVANCE") SortBy sortBy,
            @P(value = "ISO language codes to include", defaultValue = "[\"en\"]") List<String> languages) {
        // ... canned search results
    }
}
```

Test it from `AiToolsTests`:

```java
@Test
void defaultToolParameters() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .tools(new ArticleSearchTool())
            .build();

    // The user prompt supplies no limit / sortBy / languages — defaults should fire.
    String response = assistant.chat("Find me articles about virtual threads.");
    System.out.println(response);

    assertThat(response).containsIgnoringCase("virtual threads");
}
```

**Validation happens at registration time.** Misconfigured defaults (typos, numeric overflow, invalid enum constants, `defaultValue` combined with `Optional<T>`, `defaultValue` on framework-injected parameters like `@ToolMemoryId`) all throw `IllegalConfigurationException` from the `tools(...).build()` call, naming the offending `ClassName.methodName.parameterName` — much friendlier than failing on the first LLM call.

[↑ Back to table of contents](#table-of-contents)

## Lab 6.5: MCP Integration

Model Context Protocol (MCP) lets AI applications consume tools and resources hosted by external services. LangChain4j 1.14 ships an MCP client against the **2025-11-25 spec**. The standard transports are **stdio** and **Streamable HTTP**; LangChain4j also supports Docker stdio and a non-standard WebSocket transport. This lab uses stdio for simplicity (no extra infrastructure beyond `npx`). Legacy HTTP/SSE exists for older servers but is deprecated.

**Prerequisites:**
- Understanding of @Tool annotation from Lab 6
- Node.js and npm installed for running the MCP "everything" server
- MCP "everything" server accessed via: `npx -y @modelcontextprotocol/server-everything stdio`

**Lab Structure:**
This lab includes 4 progressive MCP integration tests:
1. **Basic MCP Client Setup** - Create MCP client and tool provider
2. **MCP Tools with AiServices** - Integrate external MCP tools with AI conversations
3. **Combining Local and MCP Tools** - Use both local @Tool and external MCP tools
4. **MCP Tool Provider Configuration** - Configure MCP tool providers

### 6.5.1 Optimized Test Setup with Shared MCP Client

The implementation uses a shared MCP client across all tests for better performance:

```java
class McpIntegrationTests {

    // Shared MCP client across all tests for efficiency
    private static McpClient sharedMcpClient;

    @BeforeAll
    static void setupSharedMcpClient() {
        // Create single stdio transport for MCP "everything" server via npx
        McpTransport sharedTransport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-everything", "stdio"))
                .logEvents(false) // Reduce noise across multiple tests
                .build();

        // Create shared MCP client
        sharedMcpClient = new DefaultMcpClient.Builder()
                .key("SharedMcpClient")
                .transport(sharedTransport)
                .initializationTimeout(Duration.ofSeconds(60))
                .build();

        System.out.println("Shared MCP client initialized for all tests");
    }

    @AfterAll
    static void teardownSharedMcpClient() throws Exception {
        // The transport will automatically clean up the npx process
        if (sharedMcpClient != null) {
            sharedMcpClient.close();
            System.out.println("Shared MCP client cleanup completed");
        }
    }

    @Test
    void basicMcpClientSetup() {
        // Use shared MCP client for efficiency (initialized in @BeforeAll)
        // This demonstrates the basic setup process that was used to create the shared client

        // Create MCP tool provider using the shared client
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(sharedMcpClient)
                .build();

        System.out.println("Successfully created MCP tool provider using shared client");

        // Verify tool provider was created
        assertNotNull(toolProvider, "MCP tool provider should be created");
        assertNotNull(sharedMcpClient, "Shared MCP client should be available");

        System.out.println("MCP setup verification completed successfully!");

        // Note: The actual transport/client setup is demonstrated in @BeforeAll method
        // This pattern avoids creating multiple npx processes and improves test performance
    }
}
```

### 6.5.2 MCP Tools with AiServices

Integrate MCP tools with LangChain4j AiServices using the shared client:

```java
@Test
void mcpToolsWithAiServices() {
    // Configure chat model
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.3)
            .build();

    // Create MCP tool provider using shared client for efficiency
    McpToolProvider mcpToolProvider = McpToolProvider.builder()
            .mcpClients(sharedMcpClient)
            .build();

    // Define AI assistant interface
    interface McpAssistant {
        String chat(String message);
    }

    // Build AI service with MCP tools
    McpAssistant assistant = AiServices.builder(McpAssistant.class)
            .chatModel(chatModel)
            .toolProvider(mcpToolProvider)
            .build();

    // Test using MCP tools through the assistant
    System.out.println("\n=== Testing MCP Tool Integration ===");

    String response1 = assistant.chat("What tools are available to you from the MCP server?");
    System.out.println("Available tools response: " + response1);

    String response2 = assistant.chat("Can you use any filesystem or utility tools to help me?");
    System.out.println("Tool capabilities response: " + response2);

    // Verify responses
    assertNotNull(response1, "Response about available tools should not be null");
    assertNotNull(response2, "Response about tool capabilities should not be null");
    assertFalse(response1.trim().isEmpty(), "Response should contain information about tools");

    System.out.println("MCP tool integration test completed successfully!");
}
```

### 6.5.3 Combining Local Tools and MCP Tools

Use both local @Tool methods and external MCP tools together (avoiding tool name conflicts):

```java
@Test
void combiningLocalAndMcpTools() {
    // Configure chat model
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.2)
            .build();

    // Create MCP tool provider using shared client for efficiency
    McpToolProvider mcpToolProvider = McpToolProvider.builder()
            .mcpClients(sharedMcpClient)
            .build();

    // Define AI assistant interface
    interface HybridAssistant {
        String chat(String message);
    }

    // Build AI service with both local tools and MCP tools
    // Note: Using only DateTimeTool to avoid conflicts with MCP server tools (e.g., "add" function)
    HybridAssistant assistant = AiServices.builder(HybridAssistant.class)
            .chatModel(chatModel)
            .tools(new DateTimeTool()) // Local tools - avoiding CalculatorTool due to potential conflicts
            .toolProvider(mcpToolProvider) // External MCP tools
            .build();

    System.out.println("\n=== Testing Hybrid Tool Integration ===");

    // Test combining local and external tools
    String response1 = assistant.chat("What's the current date and time, and what tools do you have available?");
    System.out.println("Hybrid tools response: " + response1);

    String response2 = assistant.chat("What's the current date, and can you also tell me what MCP tools you can access?");
    System.out.println("Mixed tool usage response: " + response2);

    // Verify responses demonstrate both tool types
    assertNotNull(response1, "Hybrid response should not be null");
    assertNotNull(response2, "Mixed tool response should not be null");
    assertTrue(response1.length() > 20, "Response should be substantive");
    assertTrue(response2.length() > 20, "Response should be substantive");

    System.out.println("\nSuccessfully demonstrated hybrid local + MCP tool integration!");
}
```

### 6.5.4 MCP Tool Provider Configuration

Demonstrate MCP tool provider configuration options:

```java
@Test
void mcpToolProviderWithFiltering() {
    // Configure chat model
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.3)
            .build();

    // Create MCP tool provider using shared client for efficiency
    // Note: filterToolNames might be available depending on LangChain4j version
    McpToolProvider toolProvider = McpToolProvider.builder()
            .mcpClients(sharedMcpClient)
            // .filterToolNames("specific_tool_name") // Example of potential filtering
            .build();

    // Define AI assistant interface
    interface FilteredAssistant {
        String chat(String message);
    }

    // Build AI service with MCP tool provider
    FilteredAssistant assistant = AiServices.builder(FilteredAssistant.class)
            .chatModel(chatModel)
            .toolProvider(toolProvider)
            .build();

    System.out.println("\n=== Testing MCP Tool Provider ===");

    String response = assistant.chat("What tools do you have available from the MCP server?");
    System.out.println("MCP tools response: " + response);

    // Verify response
    assertNotNull(response, "MCP response should not be null");
    assertFalse(response.trim().isEmpty(), "Response should contain tool information");

    System.out.println("\nSuccessfully demonstrated MCP tool provider setup!");
}

```

**Important Notes for Lab 6.5:**
- LangChain4j provides MCP **client** support (connects to external MCP servers)
- The "everything" server is a demo MCP server showcasing various tool types
- Uses **shared MCP client** pattern for better test performance (single npx process)
- MCP tools integrate seamlessly with local @Tool methods via `.toolProvider()`
- **Tool Name Conflicts**: Avoid CalculatorTool with MCP servers as they may provide conflicting tool names (e.g., "add"). Use DateTimeTool instead.
- The `@BeforeAll`/`@AfterAll` pattern demonstrates proper resource management
- MCP enables distributed tool ecosystems beyond single-application boundaries
- The npx command automatically handles MCP server lifecycle during testing

[↑ Back to table of contents](#table-of-contents)

## Lab 7: Multimodal Capabilities

Multimodal capabilities allow AI models to analyze images and transcribe audio. This lab uses GPT-5.1 for vision tasks and OpenAI's dedicated transcription model for audio.

**Prerequisites:**
- An image file `bowl_of_fruit.jpg` in `src/main/resources/`
- An audio file `tftjs.mp3` in `src/main/resources/`
- OpenAI API key with access to vision and transcription models

**Lab Structure:**
This lab includes 4 progressive multimodal tests:
1. **Local Image Analysis** - Analyze images from local resources
2. **Remote Image Analysis** - Analyze images from URLs
3. **Audio Transcription** - Transcribe audio with OpenAI's transcription API
4. **Structured Image Analysis** - Extract structured data from images

### 7.1 Local Image Analysis

Create a test that analyzes a local image file:

```java
@Test
void localImageAnalysis() throws IOException {
    // Create GPT-5.1 model with vision capabilities
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_5_1)
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
    // Create GPT-5.1 vision model
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_5_1)
            .build();

    // Use a publicly available image URL
    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

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

### 7.3 Audio Transcription

LangChain4j 1.10 added a dedicated OpenAI transcription model. Use it directly instead of routing audio through a multimodal chat endpoint:

```java
@Test
void audioTranscription() throws IOException {
    OpenAiAudioTranscriptionModel transcriptionModel = OpenAiAudioTranscriptionModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-4o-transcribe")  // also: "whisper-1", "gpt-4o-mini-transcribe"
            .build();

    byte[] audioBytes;
    try (var inputStream = getClass().getClassLoader().getResourceAsStream("tftjs.mp3")) {
        if (inputStream == null) {
            throw new RuntimeException("Could not find tftjs.mp3 in resources");
        }
        audioBytes = inputStream.readAllBytes();
    }

    Audio audio = Audio.builder()
            .binaryData(audioBytes)
            .mimeType("audio/mp3")
            .build();

    AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
            .audio(audio)
            .build();

    AudioTranscriptionResponse response = transcriptionModel.transcribe(request);
    String transcript = response.text();
    System.out.println("Transcript: " + transcript);

    assertAll("Audio transcription validation",
        () -> assertNotNull(transcript, "Transcript should not be null"),
        () -> assertFalse(transcript.trim().isEmpty(), "Transcript should not be empty"));
}
```

Note: this replaces the earlier Gemini-based audio path. The dedicated transcription model is simpler and avoids the `GOOGLEAI_API_KEY` requirement.

### 7.4 Structured Image Analysis

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
    // Create GPT-5.1 vision model
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_5_1)
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
- Uses GPT-5.1 for vision capabilities (Tests 7.1, 7.2, 7.4)
- Uses OpenAI's dedicated transcription model for audio processing (Test 7.3)
- Demonstrates ImageContent for vision and Audio / AudioTranscriptionRequest for audio
- Includes proper null checks for resource loading to avoid NullPointerException
- Uses Base64 encoding for local images and direct URLs for remote images
- Audio file (`tftjs.mp3`) must be present in `src/main/resources/`
- Audio transcription uses `OPENAI_API_KEY`; no `GOOGLEAI_API_KEY` is required
- Demonstrates both simple string responses and structured data extraction
- Uses AssertJ and JUnit 5 assertAll() for comprehensive testing
- Rate limiting: Consider adding delays between API calls if running multiple multimodal tests

[↑ Back to table of contents](#table-of-contents)

## Lab 8: Image Generation

Generate images from text prompts using OpenAI's `gpt-image-2` model. **DALL-E 3 was deprecated on May 12, 2026**; GPT Image models are its replacement and return base64-encoded image data rather than URLs.

**Prerequisites:**
- OpenAI API key
- Understanding of prompt engineering for image generation

**Lab Structure:**
This lab includes 5 image generation tests:
1. **Basic Image Generation** - Default settings
2. **Image Generation with Options** - size and quality (note: quality values changed from DALL-E)
3. **Artistic Style Variations** - styles are now expressed in the prompt (no `style` builder option)
4. **Multiple Variations** - Loop through several prompts
5. **Save to File** - Decode base64 and write a PNG to disk

### 8.1 Basic Image Generation

```java
private static final String GPT_IMAGE_2 = "gpt-image-2";

@Test
void basicImageGeneration() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_IMAGE_2)
            .build();

    String prompt = "A majestic dragon soaring over a crystal castle at sunset, fantasy art style";

    Response<Image> response = model.generate(prompt);
    String base64 = response.content().base64Data();

    assertNotNull(base64, "Base64 image data should not be null");
    assertThat(base64).as("Base64 image data").isNotBlank().hasSizeGreaterThan(100);
}
```

### 8.2 Image Generation with Options

GPT Image models accept `size` and `quality`. Quality values are `low`, `medium`, `high`, `auto` (different from DALL-E 3's `standard`/`hd`):

```java
@Test
void imageGenerationWithOptions() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_IMAGE_2)
            .size("1024x1024")
            .quality("high")
            .build();

    String prompt = "A futuristic cityscape at dawn with flying vehicles, neon lights, cyberpunk aesthetic";

    Response<Image> response = model.generate(prompt);
    String base64 = response.content().base64Data();

    assertNotNull(base64, "Base64 image data should not be null");
    assertThat(base64).hasSizeGreaterThan(1000);
}
```

### 8.3 Artistic Style Variations

GPT Image models have no `style` builder parameter. Express style in the prompt:

```java
@Test
void artisticStyleVariations() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_IMAGE_2)
            .size("1024x1024")
            .build();

    Response<Image> watercolor = model.generate(
        "A serene Japanese garden with cherry blossoms, watercolor painting style");
    Response<Image> technical = model.generate(
        "A detailed cross-section of a mechanical watch, technical illustration style");

    assertNotNull(watercolor.content().base64Data());
    assertNotNull(technical.content().base64Data());
}
```

### 8.4 Multiple Variations

```java
@Test
void creativeImageVariations() {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_IMAGE_2)
            .size("1024x1024")
            .build();

    String[] prompts = {
        "A steampunk robot playing chess, brass and copper tones",
        "A minimalist abstract representation of music, flowing lines",
        "A cozy library in a treehouse, warm lighting, books floating magically"
    };

    for (String prompt : prompts) {
        Response<Image> response = model.generate(prompt);
        assertThat(response.content().base64Data()).hasSizeGreaterThan(1000);
    }
}
```

### 8.5 Save Generated Image to File

Decode the base64 response and write a PNG to disk:

```java
@Test
void saveGeneratedImageToFile() throws IOException {
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_IMAGE_2)
            .build();

    Response<Image> response = model.generate("A warrior cat rides a dragon into battle");
    byte[] imageBytes = Base64.getDecoder().decode(response.content().base64Data());

    Path outputDir = Path.of("build", "generated-images");
    if (!Files.exists(outputDir)) {
        Files.createDirectories(outputDir);
    }
    Path outputPath = outputDir.resolve("generated_image.png");
    Files.write(outputPath, imageBytes);

    assertTrue(Files.exists(outputPath));
    assertTrue(Files.size(outputPath) > 0);
}
```

**Notes for Lab 8:**
- GPT Image models return base64 through this LangChain4j path; there is no URL response shape.
- Image generation is metered separately from chat — watch costs when iterating.
- LangChain4j 1.14's `OpenAiImageModelName` enum does not yet include GPT Image constants, so this lab passes the model ID as a string.

[↑ Back to table of contents](#table-of-contents)

## Lab 9: Retrieval-Augmented Generation (RAG)

**Prerequisites:** Make sure your `build.gradle.kts` includes the embedding/RAG modules:

```kotlin
implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q")
implementation("dev.langchain4j:langchain4j-easy-rag")
implementation("dev.langchain4j:langchain4j-document-parser-apache-tika")
```

> **Hybrid search:** As of LangChain4j 1.11, hybrid (vector + keyword) search is built into the PgVector and Elasticsearch stores. The Chroma examples below still teach the pure-vector fundamentals; switch to PgVector / Elasticsearch when you need lexical recall in addition to semantic similarity.

### 9.1 Basic Document Loading and Embedding

Create a test that demonstrates document loading and embedding:

```java
@Test
void basicDocumentEmbedding() {
    // Create embedding model
    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

    // Create in-memory embedding store
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    // Create some sample documents
    List<Document> documents = Arrays.asList(
        Document.from("LangChain4j is a Java library for building AI applications."),
        Document.from("It provides integration with various language models like OpenAI and Google AI."),
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

    List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding).maxResults(2).build()).matches();

    System.out.println("Found " + matches.size() + " relevant segments:");
    matches.forEach(match ->
        System.out.println("- " + match.embedded().text() + " (score: " + match.score() + ")")
    );

    assertFalse(matches.isEmpty());
}
```

### 9.2 RAG with ContentRetriever

Create a more sophisticated RAG implementation:

```java
@Test
void ragWithContentRetriever() {
    // Set up models
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
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

### 9.3 RAG with File Documents

Create a test that loads documents from files:

```java
@Test
void ragWithFileDocuments() throws IOException {
    // Create a sample text file for testing
    Path tempFile = Files.createTempFile("sample", ".txt");
    Files.writeString(tempFile, """
        LangChain4j is a powerful Java library for building applications with Large Language Models (LLMs).

        Key features include:
        - Integration with multiple AI providers (OpenAI, Google AI, etc.)
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

### 9.4 Advanced RAG with Metadata Filtering

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

## Lab 10: Chroma Vector Store for RAG

> **Note**: Lab 10 has been updated to use Chroma instead of Redis for better compatibility and stability.
> The complete working implementation is available in `ChromaRAGTests.java` on the solutions branch.

### Prerequisites

To use Chroma as a vector store, you need a running Chroma instance:

```bash
docker run -p 8000:8000 chromadb/chroma:0.5.4
```

**Important**: LangChain4j 1.14 uses ChromaDB's API V2 client. Chroma 0.5.4 remains a stable tested baseline, and newer Chroma versions that support API V2 should work as well.

### 10.1 Basic Chroma Vector Store Operations

Create a test that demonstrates fundamental vector store capabilities:

```java
@Test
void chromaVectorStoreOperations() {
    // Skip test if Chroma is not available
    assumeTrue(isChromaAvailable(), "Chroma is not available");

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // Create Chroma embedding store with unique collection name
    EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl("http://localhost:8000")
            .collectionName(randomUUID())
            .logRequests(true)
            .logResponses(true)
            .build();

    // Sample documents about programming languages
    List<Document> documents = Arrays.asList(
        Document.from("Python is a high-level programming language known for its simplicity and readability."),
        Document.from("Java is a popular object-oriented programming language that runs on the JVM."),
        Document.from("JavaScript is the language of the web, used for both frontend and backend development."),
        Document.from("Rust is a systems programming language focused on safety and performance."),
        Document.from("Go is a statically typed, compiled programming language designed for building scalable systems.")
    );

    DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
    List<TextSegment> segments = splitter.splitAll(documents);

    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);

    System.out.println("Added " + segments.size() + " segments to Chroma");

    // Test multiple searches to verify functionality
    String[] queries = {
        "What language is good for web development?",
        "Which language is designed for system programming?",
        "What language runs on the JVM?"
    };

    for (String query : queries) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
            EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(2)
                    .build()
        ).matches();

        System.out.println("\nSearch: " + query);
        matches.forEach(match ->
            System.out.printf("- %.3f: %s%n", match.score(), match.embedded().text())
        );

        // Verify search results
        assertFalse(matches.isEmpty(), "Should find matches for: " + query);
        assertTrue(matches.get(0).score() > 0.5, "Top match should have decent similarity");
    }
}

private boolean isChromaAvailable() {
    try {
        // Simple HTTP check to Chroma heartbeat endpoint
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api/v1/heartbeat"))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    } catch (Exception e) {
        System.out.println("Chroma not available: " + e.getMessage());
        return false;
    }
}
```

### 10.2 Production RAG System with Chroma

Create a comprehensive production-ready RAG system using Chroma with metadata and optimized settings:

```java
@Test
void productionRagSystem() {
    // Check Chroma availability
    assumeTrue(isChromaAvailable(), "Chroma is not available");

    // Configure models with production settings
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.1) // Lower temperature for consistent responses
            .maxTokens(500)
            .build();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // Create Chroma embedding store
    EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl("http://localhost:8000")
            .collectionName(randomUUID())
            .build();

    // Comprehensive knowledge base about LangChain4j
    List<Document> documents = Arrays.asList(
        Document.from("LangChain4j 1.0 introduced the ChatModel interface as the primary way to interact with language models."),
        Document.from("The AiServices interface in LangChain4j allows you to create type-safe AI-powered services using annotations."),
        Document.from("LangChain4j supports multiple embedding models including OpenAI embeddings and local models like AllMiniLM."),
        Document.from("ContentRetriever in LangChain4j is used to retrieve relevant content for RAG applications."),
        Document.from("LangChain4j provides built-in support for Chroma as a vector store for production RAG systems."),
        Document.from("The @Tool annotation enables AI models to call Java methods during conversations."),
        Document.from("RAG (Retrieval-Augmented Generation) allows AI to access external knowledge sources for better answers."),
        Document.from("LangChain4j uses builder patterns throughout the library for configuring AI services and models.")
    );

    // Process documents with metadata for better retrieval
    DocumentSplitter splitter = DocumentSplitters.recursive(150, 30);
    List<TextSegment> segments = splitter.splitAll(documents);

    // Add metadata for production use cases
    for (int i = 0; i < segments.size(); i++) {
        TextSegment segment = segments.get(i);
        segment.metadata().put("chunk_id", String.valueOf(i));
        segment.metadata().put("source", "langchain4j_docs");
        segment.metadata().put("created_at", LocalDateTime.now().toString());
    }

    // Store embeddings in Chroma
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);
    System.out.println("Stored " + segments.size() + " knowledge segments in Chroma");

    // Configure retriever with production settings
    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(3)
            .minScore(0.6) // Balanced threshold for good results
            .build();

    // Create AI assistant interface
    interface LangChain4jAssistant {
        @SystemMessage("You are an expert assistant for LangChain4j documentation. " +
                      "Provide accurate, helpful answers based on the provided context. " +
                      "If the context doesn't contain enough information, clearly state that.")
        String answer(String question);
    }

    // Build the RAG system
    LangChain4jAssistant assistant = AiServices.builder(LangChain4jAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    // Test with comprehensive questions
    String[] questions = {
        "What is the primary interface for chat in LangChain4j 1.0?",
        "How does LangChain4j support type-safe AI services?",
        "What is RAG and how does it help AI applications?",
        "How do I use tools with LangChain4j?",
        "What vector stores does LangChain4j support?"
    };

    System.out.println("\n=== RAG System Q&A Test ===");
    for (String question : questions) {
        String answer = assistant.answer(question);
        System.out.println("\nQ: " + question);
        System.out.println("A: " + answer);

        // Verify response quality
        assertNotNull(answer, "Answer should not be null");
        assertFalse(answer.trim().isEmpty(), "Answer should not be empty");
        assertTrue(answer.length() > 20, "Answer should be substantive");
    }

    System.out.println("\n" + "=".repeat(50));
    System.out.println("Production RAG system test completed successfully!");
}
```

### 10.3 RAG with Document Parsing

Demonstrate realistic document processing by loading and parsing actual PDF files with Apache Tika:

```java
@Test
void ragWithDocumentParsing() {
    // Check Chroma availability
    assumeTrue(isChromaAvailable(), "Chroma is not available");

    // Configure models for document processing
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .temperature(0.2) // Slightly higher for more natural responses
            .maxTokens(600)
            .build();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // Create Chroma embedding store
    EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl("http://localhost:8000")
            .collectionName(randomUUID())
            .build();

    // Load PDF document from resources (Apache Tika will parse the PDF)
    Path documentPath = Paths.get("src/test/resources/LangChain4j-Modern-Features.pdf");
    Document document = FileSystemDocumentLoader.loadDocument(documentPath);

    System.out.println("Loaded PDF document with " + document.text().length() + " characters");

    // Split document with appropriate chunk sizes for technical content
    DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
    List<TextSegment> segments = splitter.split(document);

    // Add metadata to track document source
    for (int i = 0; i < segments.size(); i++) {
        TextSegment segment = segments.get(i);
        segment.metadata().put("chunk_id", String.valueOf(i));
        segment.metadata().put("source_file", "LangChain4j-Modern-Features.pdf");
        segment.metadata().put("document_type", "pdf_documentation");
        segment.metadata().put("format", "PDF");
        segment.metadata().put("processed_at", LocalDateTime.now().toString());
    }

    // Generate embeddings and store in Chroma
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
    embeddingStore.addAll(embeddings, segments);
    System.out.println("Processed and stored " + segments.size() + " document segments");

    // Configure content retriever for document-based queries
    ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(4) // More results for technical queries
            .minScore(0.5) // Lower threshold for broader relevant content
            .build();

    // Create specialized assistant for document questions
    interface DocumentAssistant {
        @SystemMessage("You are an expert assistant that answers questions based on technical documentation. " +
                      "Provide accurate, detailed answers based on the provided context from the document. " +
                      "If the document doesn't contain enough information to fully answer the question, " +
                      "clearly state what information is available and what is missing.")
        String answer(String question);
    }

    // Build the document-based RAG system
    DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(retriever)
            .build();

    // Test with questions about the parsed document content
    String[] documentQuestions = {
        "What are the key API changes introduced in LangChain4j 1.0?",
        "How does the new ChatModel interface differ from previous versions?",
        "What vector stores are supported for production deployments?",
        "What are the recommended best practices for testing LangChain4j applications?",
        "How does the @Tool annotation system work in version 1.0?"
    };

    System.out.println("\n=== Document-Based RAG Q&A Test ===");
    for (String question : documentQuestions) {
        String answer = assistant.answer(question);
        System.out.println("\nQ: " + question);
        System.out.println("A: " + answer);

        // Verify response quality for document-based content
        assertNotNull(answer, "Answer should not be null");
        assertFalse(answer.trim().isEmpty(), "Answer should not be empty");
        assertTrue(answer.length() > 30, "Answer should be detailed for technical content");
    }

    System.out.println("\n" + "=".repeat(60));
    System.out.println("Document parsing and RAG integration test completed successfully!");
    System.out.println("Document segments processed: " + segments.size());
    System.out.println("Total characters indexed: " +
        segments.stream().mapToInt(s -> s.text().length()).sum());
}
```

### Helper Method

The `isChromaAvailable()` helper method checks if Chroma is running before executing tests:

```java
private boolean isChromaAvailable() {
    try {
        // Simple HTTP check to Chroma heartbeat endpoint
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api/v1/heartbeat"))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    } catch (Exception e) {
        System.out.println("Chroma not available: " + e.getMessage());
        return false;
    }
}
```

**Important Notes for Lab 10:**
- Uses ChromaDB's API V2 client.
- Recommended Chroma server version: 0.5.4+ (or latest stable).
- Collection names use `randomUUID()` to avoid conflicts between test runs.
- The `isChromaAvailable()` helper ensures tests only run when Chroma is reachable.
- Chroma includes a web UI at `http://localhost:8000`.
- Batch operations with `addAll()` are more efficient than individual `add()` calls.
- Document parsing uses Apache Tika for broad file-format support.
- For production: hybrid (vector + keyword) search is built into the PgVector and Elasticsearch stores as of LangChain4j 1.11; for HQL-based retrieval over JPA entities, see `HibernateContentRetriever` (1.13).

[↑ Back to table of contents](#table-of-contents)

## Lab 11: Agentic API

The `langchain4j-agentic` module composes multiple LLM-backed agents into workflows. The fundamental building blocks are:

- An **agent** — a method on an interface annotated with `@Agent`, much like an `AiService` but with a name and an output key.
- A **composition** — combine agents into sequences, loops, conditionals, or parallel fan-outs via `AgenticServices`.

Each agent's output flows into a shared `AgenticScope` keyed by `outputKey`; downstream agents and exit conditions read from it.

**Prerequisites:**
- Add the dependency to `build.gradle.kts`:
  ```kotlin
  implementation("dev.langchain4j:langchain4j-agentic")
  ```

### 11.1 A Single Typed Agent

Define an agent interface with `@Agent` and build it via `AgenticServices.agentBuilder(Class)`:

```java
interface CreativeWriter {
    @SystemMessage("You are a creative short-story writer. Keep stories under 200 words.")
    @UserMessage("Write a short story about {{topic}}.")
    @Agent("Generate a short story on a given topic")
    String writeStoryAbout(@V("topic") String topic);
}

@Test
void singleTypedAgent() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
            .chatModel(model)
            .outputKey("story")
            .build();

    String story = writer.writeStoryAbout("a robot who learns to garden");
    System.out.println(story);

    assertThat(story).isNotBlank().hasSizeGreaterThan(50);
}
```

### 11.2 Sequence Workflow

Chain two agents — a writer that produces a story, then an editor that adapts it for an audience. Each agent's output is keyed in the shared `AgenticScope` so the next can reference it via `@V`:

```java
interface AudienceEditor {
    @SystemMessage("You rewrite stories so they're appropriate for the requested audience.")
    @UserMessage("Rewrite this story for {{audience}}:\n\n{{story}}")
    @Agent("Adapt a story for a target audience")
    String editForAudience(@V("story") String story, @V("audience") String audience);
}

@Test
void sequenceWorkflow() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
            .chatModel(model).outputKey("story").build();
    AudienceEditor editor = AgenticServices.agentBuilder(AudienceEditor.class)
            .chatModel(model).outputKey("story").build();

    UntypedAgent novelist = AgenticServices.sequenceBuilder()
            .subAgents(writer, editor)
            .outputKey("story")
            .build();

    String result = (String) novelist.invoke(Map.of(
        "topic", "a dragon learning to bake bread",
        "audience", "five-year-olds"));

    assertThat(result).isNotBlank();
}
```

### 11.3 Loop Workflow with Exit Condition

A scorer rates each draft from 0.0 to 1.0; if the score is below the threshold, an editor revises the draft and the loop runs again. The loop stops when either the score crosses 0.7 or `maxIterations` is hit:

```java
interface StyleScorer {
    @SystemMessage("You are a literary critic. Reply with a single number from 0.0 to 1.0.")
    @UserMessage("Rate the literary quality of this story on a 0.0-1.0 scale:\n\n{{story}}")
    @Agent("Score the literary quality of a story")
    double scoreStory(@V("story") String story);
}

interface StyleEditor {
    @SystemMessage("You polish prose to improve literary quality.")
    @UserMessage("Improve the prose of this story:\n\n{{story}}")
    @Agent("Improve the literary quality of a story")
    String polish(@V("story") String story);
}

@Test
void loopWorkflow() {
    ChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO)
            .build();

    CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
            .chatModel(model).outputKey("story").build();
    StyleScorer scorer = AgenticServices.agentBuilder(StyleScorer.class)
            .chatModel(model).outputKey("score").build();
    StyleEditor styleEditor = AgenticServices.agentBuilder(StyleEditor.class)
            .chatModel(model).outputKey("story").build();

    UntypedAgent reviewLoop = AgenticServices.loopBuilder()
            .subAgents(scorer, styleEditor)
            .maxIterations(3)
            .exitCondition(scope -> scope.readState("score", 0.0) >= 0.7)
            .build();

    UntypedAgent pipeline = AgenticServices.sequenceBuilder()
            .subAgents(writer, reviewLoop)
            .outputKey("story")
            .build();

    Object result = pipeline.invoke(Map.of("topic", "a lighthouse keeper's diary"));
    assertThat(result.toString()).isNotBlank();
}
```

### 11.4 Voting Pattern (1.15+)

LangChain4j 1.15 introduced the voting pattern in the new `langchain4j-agentic-patterns` module. Multiple sub-agents run **in parallel** and a `VotingPlanner` aggregates their outputs via a pluggable `VotingStrategy`. The no-arg constructor uses `VotingStrategy.majority()`, which is the right default for classification.

Add the dependency:

```kotlin
implementation("dev.langchain4j:langchain4j-agentic-patterns")
```

Diversity matters — three identical agents will almost always produce the same vote. Common sources of diversity: different prompts, different temperatures, different models, or different providers. This example uses three temperatures:

```java
interface SentimentClassifier {
    @SystemMessage("You classify sentiment. Reply with exactly one word: POSITIVE, NEGATIVE, or NEUTRAL.")
    @UserMessage("Classify the sentiment of this text:\n\n{{text}}")
    @Agent("Classify the sentiment of a text")
    String classify(@V("text") String text);
}

interface SentimentVoter {
    @Agent("Aggregate sentiment votes from multiple classifiers")
    String classify(@V("text") String text);
}

@Test
void votingPattern() {
    ChatModel cold = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO).temperature(0.0).build();
    ChatModel warm = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO).temperature(0.5).build();
    ChatModel hot  = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(GPT_4_1_NANO).temperature(1.0).build();

    SentimentClassifier strict   = AgenticServices.agentBuilder(SentimentClassifier.class)
            .chatModel(cold).outputKey("vote1").build();
    SentimentClassifier balanced = AgenticServices.agentBuilder(SentimentClassifier.class)
            .chatModel(warm).outputKey("vote2").build();
    SentimentClassifier creative = AgenticServices.agentBuilder(SentimentClassifier.class)
            .chatModel(hot).outputKey("vote3").build();

    SentimentVoter voter = AgenticServices.plannerBuilder(SentimentVoter.class)
            .subAgents(strict, balanced, creative)
            .outputKey("classification")
            .planner(VotingPlanner::new)
            .build();

    String result = voter.classify("I absolutely love this product!");
    assertThat(result).isEqualToIgnoringCase("POSITIVE");
}
```

For richer aggregation — e.g., three critics scoring a draft and averaging the scores — pass a custom `VotingStrategy` lambda to `VotingPlanner`:

```java
VotingStrategy averageScore = votes -> votes.stream()
        .mapToDouble(v -> ((CritiqueResult) v).score())
        .average().orElse(0.0);

AgenticServices.plannerBuilder()
    .subAgents(styleCritic, originalityCritic, engagementCritic)
    .planner(() -> new VotingPlanner(averageScore))
    .outputKey("critique")
    .build();
```

**Per-agent model selection (also new in 1.15).** Use `chatModel(scope -> ...)` to pick a model based on shared state — for example, upgrade to a stronger (more expensive) model only when the voting critics produce a high score:

```java
StoryEditor editor = AgenticServices.agentBuilder(StoryEditor.class)
        .chatModel(scope -> scope.readState("score", 0.0) > 0.8 ? enhancedModel : baseModel)
        .outputKey("story")
        .build();
```

**Notes for Lab 11:**
- `AgenticServices` also provides `parallelBuilder()`, `conditionalBuilder()`, `supervisorBuilder()` (an LLM-driven planner that chooses the next sub-agent), and `plannerBuilder()` (custom planners — the voting pattern is one such planner).
- The agentic API is still marked experimental in the LangChain4j docs; the surface has been stable across the 1.13 → 1.15 releases but expect occasional refinements.
- Output keys form an implicit dataflow graph; mistyping a `@V` template variable is a common source of "empty output" bugs.

[↑ Back to table of contents](#table-of-contents)

## Conclusion

Congratulations! You've completed a comprehensive tour of LangChain4j's capabilities. You've learned how to:

- Interact with LLMs through LangChain4j's `ChatModel` interface
- Stream responses (and cancel them mid-flight) for better UX
- Extract structured data from LLM responses using `AiServices`
- Use prompt templates and per-call `ChatRequestParameters`
- Maintain conversation state with `ChatMemory`, including replacing it via `set()`
- Extend AI capabilities with custom tools, including `Optional` parameters and `@P(defaultValue = ...)`
- Integrate external tools through MCP (stdio / Docker / WebSocket)
- Work with multimodal capabilities — image analysis and OpenAI transcription
- Generate images using `gpt-image-2`
- Build Retrieval-Augmented Generation (RAG) systems with document processing
- Use Chroma as a persistent vector store for production RAG applications
- Compose multi-agent workflows with the agentic API, including the 1.15 voting pattern

Key takeaways:
- LangChain4j 1.x's `ChatModel` is the primary interface for chat
- Builder patterns provide flexible, type-safe configuration
- `AiServices` enables annotation-driven AI integration
- The agentic API turns "one big prompt" into composable workflow building blocks
- Hybrid search in PgVector / Elasticsearch is built-in for production RAG
- Multimodal capabilities span images, audio (transcription), and (in 1.9+) video

Continue exploring the LangChain4j documentation and community examples to further enhance your AI application development skills.
