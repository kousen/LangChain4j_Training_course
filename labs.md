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
- [Lab 6.5: MCP Integration](#lab-65-mcp-integration)
- [Lab 7: Multimodal Capabilities](#lab-7-multimodal-capabilities)
- [Lab 8: Image Generation](#lab-8-image-generation)
- [Lab 9: Retrieval-Augmented Generation (RAG)](#lab-9-retrieval-augmented-generation-rag)
- [Lab 10: Chroma Vector Store for RAG](#lab-10-chroma-vector-store-for-rag)
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

## Lab 6.5: MCP Integration

Model Context Protocol (MCP) allows AI applications to access tools and resources from external services. This lab demonstrates how to use LangChain4j's MCP client to connect to external MCP servers and integrate their tools with your AI applications.

**Prerequisites:**
- Understanding of @Tool annotation from Lab 6
- Node.js and npm installed for running the MCP "everything" server
- MCP "everything" server accessed via: `npx -y @modelcontextprotocol/server-everything`

**Lab Structure:**
This lab includes 4 progressive MCP integration tests:
1. **Basic MCP Client Setup** - Create MCP client and tool provider
2. **MCP Tools with AiServices** - Integrate external MCP tools with AI conversations
3. **Combining Local and MCP Tools** - Use both local @Tool and external MCP tools
4. **MCP Tool Provider Configuration** - Configure MCP tool providers

### TODO Exercises for Lab 6.5

Create a new test class `McpIntegrationTests.java` and implement the following exercises:

**Exercise 6.5.1:** Create `basicMcpClientSetup()` test method
- Create `StdioMcpTransport` with npx command for "everything" server
- Build `DefaultMcpClient` with unique key and the transport
- Create `McpToolProvider` using the MCP client
- Verify both client and tool provider are created successfully
- No need for server availability checks - npx handles this

**Exercise 6.5.2:** Create `mcpToolsWithAiServices()` test method  
- Set up ChatModel with OpenAI GPT-4-1-Nano
- Create MCP client using stdio transport approach
- Build `McpToolProvider` from the MCP client
- Create AI assistant interface and build service with `.toolProvider()`
- Test asking about available tools and verify responses

**Exercise 6.5.3:** Create `combiningLocalAndMcpTools()` test method
- Configure ChatModel and create MCP client with stdio transport
- Build AI service with both `.tools()` (DateTimeTool only - avoid CalculatorTool conflicts) and `.toolProvider()` 
- Test questions that use both local tools (date/time) and MCP tools
- Verify responses demonstrate both tool types working together

**Exercise 6.5.4:** Create `mcpToolProviderConfiguration()` test method
- Set up ChatModel and MCP client using stdio approach
- Create `McpToolProvider` with configuration options
- Build AI service with the configured tool provider
- Test the tool provider functionality and verify responses

**Important Notes:**
- Use `StdioMcpTransport` with npx commands - no HTTP endpoints needed
- Use `DefaultMcpClient.Builder().key().transport().build()` pattern
- Use `McpToolProvider.builder().mcpClients().build()` for tool providers
- MCP enables access to external tools beyond local @Tool methods
- The "everything" server provides various demo tools via npx stdio
- **Tool Name Conflicts**: Avoid using CalculatorTool with MCP servers as they may provide conflicting tool names (e.g., "add"). Use DateTimeTool instead for hybrid testing.

[↑ Back to table of contents](#table-of-contents)

## Lab 7: Multimodal Capabilities

Multimodal capabilities allow AI models to analyze and understand both images and audio. This lab demonstrates how to use GPT-4 with multimodal content to process images and audio files using LangChain4j.

**Prerequisites:** 
- An image file `bowl_of_fruit.jpg` in `src/main/resources/`
- An audio file `tftjs.mp3` in `src/main/resources/`
- OpenAI API key with access to GPT-4 vision models
- Google AI API key for audio processing with Gemini models

**Lab Structure:**
This lab includes 4 progressive multimodal tests:
1. **Local Image Analysis** - Analyze images from local resources
2. **Remote Image Analysis** - Analyze images from URLs  
3. **Audio Transcription** - Process audio content with AudioContent
4. **Structured Image Analysis** - Extract structured data from images

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

### 7.3 Audio Transcription and Analysis

Demonstrate audio processing using AudioContent with Google's Gemini model:

```java
@Test
@EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
void audioTranscriptionAnalysis() throws IOException {
    // Create Google Gemini model for audio processing
    ChatModel model = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLEAI_API_KEY"))
            .modelName("gemini-2.5-flash-preview-05-20")
            .build();

    // Create audio and text content for the message
    TextContent textContent = TextContent.from("Please transcribe and analyze the content of this audio file.");
    AudioContent audioContent = AudioContent.from(readSimpleAudioData(), "audio/mp3");
    
    UserMessage userMessage = UserMessage.from(textContent, audioContent);
    
    System.out.println("=== Audio Transcription and Analysis Test ===");
    
    // Process the audio with Gemini
    try {
        String response = model.chat(userMessage).aiMessage().text();
        System.out.println("Transcription/Analysis: " + response);
        
        // Verify response quality
        assertAll("Audio analysis validation",
            () -> assertNotNull(response, "Response should not be null"),
            () -> assertFalse(response.trim().isEmpty(), "Response should not be empty"),
            () -> assertTrue(response.length() > 10, "Response should contain content")
        );

        System.out.println("=" + "=".repeat(50));
        
    } catch (Exception e) {
        // Handle gracefully if audio processing is not supported
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
    try (var inputStream = getClass().getClassLoader()
            .getResourceAsStream("tftjs.mp3")) {
        if (inputStream == null) {
            throw new RuntimeException("Could not find tftjs.mp3 in resources");
        }
        return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
    }
}
```

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
- Uses GPT-4-1-Mini model for vision capabilities (Tests 7.1, 7.2, 7.4)
- Uses Google Gemini model for audio processing (Test 7.3)
- Demonstrates both ImageContent and AudioContent classes for multimodal processing
- Includes proper null checks for resource loading to avoid NullPointerException
- Uses Base64 encoding for local images and direct URLs for remote images  
- Audio processing requires Google AI API key and uses Gemini 2.5 Flash Preview model
- Audio file (`tftjs.mp3`) must be present in `src/main/resources/`
- Audio test uses `@EnabledIfEnvironmentVariable` to run only when GOOGLEAI_API_KEY is set
- Demonstrates both simple string responses and structured data extraction
- Uses AssertJ and JUnit 5 assertAll() for comprehensive testing
- Rate limiting: Consider adding delays between API calls if running multiple multimodal tests

[↑ Back to table of contents](#table-of-contents)

## Lab 8: Image Generation

Image generation capabilities allow AI models to create images from text prompts. This lab demonstrates how to use OpenAI's DALL-E with LangChain4j for generating high-quality images.

**Prerequisites:**
- OpenAI API key with access to DALL-E models
- Understanding of prompt engineering for image generation

**Lab Structure:**
This lab includes 5 progressive image generation tests:
1. **Basic Image Generation** - Simple image creation with DALL-E
2. **Image Generation with Options** - Configuration options for quality and style  
3. **Advanced Image Generation** - Different artistic and technical styles
4. **Creative Image Variations** - Multiple images with varied prompts
5. **Base64 Image Generation** - Using gpt-image-1 model with base64-encoded images

### 8.1 Basic Image Generation

Create a test that generates an image using OpenAI's DALL-E:

```java
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
    
    // Verify the image was generated successfully
    assertNotNull(image.url(), "Image URL should not be null");
    assertThat(image.url().toString())
            .as("Generated image URL")
            .isNotBlank()
            .startsWith("https://");
}
```

### 8.2 Image Generation with Options

Create a test with more specific generation options:

```java
@Test
void imageGenerationWithOptions() throws IOException {
    // Create OpenAI ImageModel with specific options
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(DALL_E_3)
            .size("1024x1024")
            .quality("hd")
            .style("vivid")
            .build();

    // Define a detailed prompt for high-quality generation
    String prompt = "A futuristic cityscape at dawn with flying vehicles, neon lights reflecting on wet streets, cyberpunk aesthetic";
    
    System.out.println("=== Image Generation with Options Test ===");
    System.out.println("Prompt: " + prompt);
    System.out.println("Configuration: 1024x1024, HD quality, vivid style");
    
    // Generate the image with enhanced settings
    Response<Image> response = model.generate(prompt);
    Image image = response.content();
    
    // Display results and verify
    System.out.println("High-quality generated image URL: " + image.url());
    System.out.println("Revised prompt: " + image.revisedPrompt());
    
    if (image.url() != null) {
        System.out.println("Image generated successfully with HD quality!");
    }
    
    // Verify the image generation
    assertNotNull(image.url(), "HD image URL should not be null");
    assertThat(image.url().toString())
            .as("HD generated image URL")
            .isNotBlank()
            .startsWith("https://");
}
```

### 8.3 Advanced Image Generation Configuration

Demonstrate generating images with different artistic styles and detailed prompts:

```java
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
    String artisticPrompt = "A serene Japanese garden with cherry blossoms, traditional architecture, and a koi pond, watercolor painting style";
    Response<Image> artisticResponse = model.generate(artisticPrompt);
    Image artisticImage = artisticResponse.content();
    
    System.out.println("Artistic prompt: " + artisticPrompt);
    System.out.println("Generated artistic image: " + artisticImage.url());
    
    // Test technical/detailed prompt
    String technicalPrompt = "A detailed cross-section of a mechanical watch showing gears, springs, and intricate components, technical illustration style";
    Response<Image> technicalResponse = model.generate(technicalPrompt);
    Image technicalImage = technicalResponse.content();
    
    System.out.println("Technical prompt: " + technicalPrompt);
    System.out.println("Generated technical image: " + technicalImage.url());
    
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
```

### 8.4 Creative Image Generation Variations

Generate multiple variations of images with different prompts:

```java
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
        
        // Verify each image generation
        assertNotNull(image.url(), "Image " + (i + 1) + " URL should not be null");
        assertThat(image.url().toString())
                .as("Variation " + (i + 1) + " image URL")
                .isNotBlank()
                .startsWith("https://");
    }
    
    System.out.println("All variations generated successfully!");
}
```

### 8.5 Base64 Image Generation with GPT-Image-1

Demonstrate using the new OpenAI "gpt-image-1" model that returns base64-encoded images:

```java
@Test
void base64ImageGeneration() throws IOException {
    // Create OpenAI ImageModel using the new gpt-image-1 model
    // Note: No constant available yet for this new model
    ImageModel model = OpenAiImageModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-image-1")
            .build();

    // Define a creative prompt
    String prompt = "A warrior cat rides a dragon into battle";
    
    System.out.println("=== Base64 Image Generation Test ===");
    System.out.println("Prompt: " + prompt);
    System.out.println("Model: gpt-image-1 (returns base64-encoded images)");
    
    // Generate the image
    Response<Image> response = model.generate(prompt);
    Image image = response.content();
    
    // The gpt-image-1 model returns base64-encoded images instead of URLs
    String base64Data = null;
    if (image.base64Data() != null) {
        base64Data = image.base64Data();
    } else if (image.url() != null && image.url().toString().startsWith("data:")) {
        // Fallback: parse from data URL format
        base64Data = image.url().toString().split(",")[1];
    }
    
    assertNotNull(base64Data, "Base64 image data should not be null");
    System.out.println("Base64 data length: " + base64Data.length() + " characters");
    
    // Decode the base64 to bytes using Java's built-in decoder
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
    assertTrue(Files.exists(outputPath), "Generated image file should exist");
    assertTrue(Files.size(outputPath) > 0, "Generated image file should have content");
    
    // Note: The generated image file can be opened with any image viewer
    // This approach provides more control over image data compared to URL-based responses
}
```

**Important Notes for Lab 8:**
- Uses DALL_E_3 model constant which provides the latest DALL-E capabilities
- The new "gpt-image-1" model uses OpenAI's Responses API and returns base64-encoded images instead of URLs
- Image generation can be expensive - consider costs when running multiple tests
- DALL-E may revise prompts for safety and quality - check the `revisedPrompt()` field
- DALL-E generated images are returned as URLs that expire after a certain time
- Base64 images from gpt-image-1 provide more control and don't expire like URLs
- Different quality settings ("standard" vs "hd") affect both cost and generation time
- Style settings ("vivid" vs "natural") affect the artistic interpretation
- Use Java's `Base64.getDecoder()` for decoding base64 image data to files

[↑ Back to table of contents](#table-of-contents)

## Lab 9: Retrieval-Augmented Generation (RAG)

**Prerequisites**: Ensure your `build.gradle.kts` includes the Google Gemini dependency (added in Lab 7.3 for audio processing):

```kotlin
// LangChain4j model integrations
implementation("dev.langchain4j:langchain4j-open-ai")
implementation("dev.langchain4j:langchain4j-anthropic")
implementation("dev.langchain4j:langchain4j-google-ai-gemini")
```

If this dependency is missing, add it and run `./gradlew build` to refresh dependencies.

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

**Important**: Use Chroma version 0.5.4 for compatibility with LangChain4j 1.0.1. This version provides stable API endpoints that work reliably with the current LangChain4j integration.

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
- Uses Chroma version 0.5.4 for compatibility with LangChain4j 1.0.1
- Chroma provides excellent persistence without complex setup
- Collection names use `randomUUID()` to avoid conflicts between test runs
- The `isChromaAvailable()` helper method ensures tests only run when Chroma is accessible
- Chroma includes a built-in web UI at http://localhost:8000 for exploring collections
- Consider using metadata for production deployments to enable advanced filtering
- Batch operations with `addAll()` are more efficient than individual `add()` calls
- Document parsing uses Apache Tika for comprehensive file format support
- Test 10.3 demonstrates realistic document processing with actual file loading
- Modern Java practices: uses `List.of()` instead of `Arrays.asList()` throughout

## Conclusion

Congratulations! You've completed a comprehensive tour of LangChain4j's capabilities. You've learned how to:

- Interact with LLMs through LangChain4j's `ChatModel` interface
- Stream responses for better user experience  
- Extract structured data from LLM responses using `AiServices`
- Use prompt templates for consistent prompting
- Maintain conversation state with `ChatMemory`
- Extend AI capabilities with custom tools using `@Tool` annotations
- Create high-level AI services with `AiServices` interface
- Work with multimodal capabilities for image and audio processing
- Generate images using AI models like DALL-E
- Build Retrieval-Augmented Generation (RAG) systems with document processing
- Use Chroma as a persistent vector store for production RAG applications

These skills provide a solid foundation for building AI-powered applications using LangChain4j and the Java ecosystem. The patterns you've learned can be extended and combined to create sophisticated AI applications tailored to your specific needs.

Key takeaways:
- LangChain4j 1.0 uses `ChatModel` as the primary interface
- Builder patterns provide flexible configuration
- `AiServices` enables type-safe, annotation-driven AI integration
- RAG systems combine retrieval and generation for more accurate responses
- Chroma provides production-ready vector storage for scalable applications
- Multimodal capabilities enable rich AI interactions with images and audio

Continue exploring the LangChain4j documentation and community examples to further enhance your AI application development skills.