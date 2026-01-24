# LangChain4j 1.7.1 → 1.10.0 Upgrade Notes

**Date**: 2025-01-24
**Previous Version**: 1.7.1
**New Version**: 1.10.0

## Overview

This upgrade spans three minor versions (1.8.0, 1.9.0, 1.10.0) and introduces the new `langchain4j-agentic` module for multi-agent workflows.

## Changes Applied

### 1. Dependency Updates

**File**: `build.gradle.kts`

```kotlin
// BOM version update
implementation(platform("dev.langchain4j:langchain4j-bom:1.10.0"))

// New agentic module
implementation("dev.langchain4j:langchain4j-agentic")
```

### 2. New Test Class

**File**: `src/test/java/com/kousenit/langchain4j/MultiAgentTests.java`

New test class demonstrating the `langchain4j-agentic` module:
- `basicAgentWithAnnotation()` - Single agent with `@Agent` annotation
- `sequentialAgentWorkflow()` - Sequential workflow with `AgenticServices.sequenceBuilder()`

### 3. Documentation Updates

| File | Changes |
|------|---------|
| `labs.md` | Updated version note, rewrote Lab 6.6 for agentic module |
| `README.md` | Updated version reference |
| `CLAUDE.md` | Updated version and features list |
| `slides/slides.md` | Updated all version references |
| `slides/slides-comprehensive.md` | Updated all version references |

## New Features in 1.10.0

### 1. langchain4j-agentic Module ⭐ NEW

The major addition in this upgrade. Provides:

- **`@Agent` annotation** - Marks methods as agent entry points
- **`AgenticServices`** - Builder factory for agents and workflows
- **Workflow patterns**:
  - `sequenceBuilder()` - Sequential execution
  - `parallelBuilder()` - Concurrent execution
  - `loopBuilder()` - Iterative with exit conditions
  - `conditionalBuilder()` - Branching logic
  - `supervisorBuilder()` - LLM-driven orchestration

```java
// Basic agent
CreativeWriter writer = AgenticServices
    .agentBuilder(CreativeWriter.class)
    .chatModel(model)
    .outputKey("story")
    .build();

// Sequential workflow
UntypedAgent pipeline = AgenticServices
    .sequenceBuilder()
    .subAgents(writer, editor)
    .outputKey("story")
    .build();
```

### 2. Model Catalog Support

Unified model selection for Anthropic, Gemini, OpenAI, and Mistral providers.

### 3. AgentListener/AgentMonitor

Observability hooks for monitoring agent invocations:

```java
.listener(new AgentListener() {
    @Override
    public void beforeAgentInvocation(AgentRequest request) {
        System.out.println("Starting: " + request.agentName());
    }
    @Override
    public void afterAgentInvocation(AgentResponse response) {
        System.out.println("Completed: " + response.agentName());
    }
})
```

### 4. Audio Transcription (OpenAI)

New support for audio transcription in OpenAI integration.

### 5. Hybrid Retrieval

Combined retrieval strategies for RAG applications.

### 6. Anthropic Enhancements

- Structured outputs support
- PDF input via URL
- Strict tools parameter

### 7. MCP Improvements

- WebSocket transport support (in addition to stdio)
- Client reconnection after network failures

### 8. Streaming Cancellation

Ability to cancel ongoing streaming requests.

## Version History (1.7.1 → 1.10.0)

### 1.8.0 Changes
- Streaming cancellation capability
- AgenticScope accessible from @Tool methods
- AiServices can return void
- Document class made public

### 1.9.0 Changes
- Generic agentic planner introduced
- `@subagent` annotation removed (replaced by planner architecture)
- MCP WebSocket transport
- Dynamic window sizing for chat memory
- Case-insensitive enum deserialization
- GPT-5/o1 series support with reasoning effort control

### 1.10.0 Changes
- Model Catalog for multiple providers
- AgentListener/AgentMonitor observability
- Audio transcription support
- Hybrid retrieval
- Anthropic structured outputs
- MCP reconnection fixes

## Breaking Changes

### Agentic API (1.9.0)

The `@subagent` annotation was removed. Multi-agent patterns now use:
- `AgenticServices.agentBuilder()` for single agents
- `AgenticServices.sequenceBuilder()` for sequential workflows
- Other builder methods for parallel, conditional, loop patterns

### Output Format Instructions (1.10.0)

Output format instructions now append to the last `TextContent` rather than being handled separately. This is a behavioral change that may affect structured extraction.

## Build Notes

### Java Version Requirement

**Important**: Gradle's Kotlin DSL does not yet support Java 25. Use Java 21 for building:

```bash
export JAVA_HOME=/path/to/java21
./gradlew build
```

The project is configured for Java 21 toolchain in `build.gradle.kts`.

## Testing Status

### Compilation
✅ All code compiles with LangChain4j 1.10.0

### Test Execution
Tests require API keys to run:
```bash
OPENAI_API_KEY=xxx ./gradlew test
```

## Lab 6.6 Changes

Lab 6.6 (Multi-Agent Systems) was completely rewritten to use the `langchain4j-agentic` module:

**Before (1.7.1)**: Manual agent chaining with `AiServices`
```java
// Old approach - manual chaining
ResearchAgent researcher = AiServices.builder(ResearchAgent.class)
        .chatModel(model).build();
WriterAgent writer = AiServices.builder(WriterAgent.class)
        .chatModel(model).build();

String research = researcher.research(topic);
String article = writer.write(research);
```

**After (1.10.0)**: Proper agentic workflows with `AgenticServices`
```java
// New approach - AgenticServices with @Agent annotation
CreativeWriter writer = AgenticServices
        .agentBuilder(CreativeWriter.class)
        .chatModel(model)
        .outputKey("story")
        .build();

UntypedAgent pipeline = AgenticServices
        .sequenceBuilder()
        .subAgents(writer, editor)
        .outputKey("story")
        .build();

String result = (String) pipeline.invoke(Map.of("topic", "..."));
```

## References

- [LangChain4j 1.10.0 Release](https://github.com/langchain4j/langchain4j/releases/tag/1.10.0)
- [LangChain4j Agents Tutorial](https://docs.langchain4j.dev/tutorials/agents/)
- [Agentic Examples](https://github.com/langchain4j/langchain4j-examples/tree/main/agentic-tutorial)
