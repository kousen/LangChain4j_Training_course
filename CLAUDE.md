# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **hands-on training course** for learning LangChain4j through progressive lab exercises. The repository is structured as a proper training course where students build functionality incrementally.

### Repository Structure

- **`main` branch**: Starter code with TODO-guided exercises for students
- **`solutions` branch**: Complete working implementations for reference
- **Test classes**: Contain TODO comments guiding students through implementation
- **Example classes**: Skeleton implementations with TODO instructions

The course demonstrates integration of Large Language Models (LLMs) with Java applications using the LangChain4j library (version 1.0.1), covering:

- Text generation and chat capabilities
- Structured data extraction  
- Prompt engineering with templates
- Chat memory for maintaining conversation context
- Function calling with local @Tool methods
- External tool integration via Model Context Protocol (MCP)
- Vision capabilities for image understanding and generation
- Audio processing (text-to-speech and speech-to-text)
- Retrieval-Augmented Generation (RAG) with PDF and web content

## Common Commands

### Build and Run

```bash
# Build the project
./gradlew build

# Run example main classes directly (no application runner needed)
./gradlew run --main-class=com.kousenit.langchain4j.examples.BasicChatExample

# Run tests with environment variables
OPENAI_API_KEY=your_key ./gradlew test
```

### Testing

```bash
# Run all tests (many will be empty TODO stubs in main branch)
./gradlew test

# Run specific test classes (students implement these progressively)
./gradlew test --tests OpenAiChatTests
./gradlew test --tests AnthropicChatTests
./gradlew test --tests RAGTests

# Run individual test methods
./gradlew test --tests OpenAiChatTests.basicChat

# To see working tests, switch to solutions branch
git checkout solutions
./gradlew test
```

### Redis Setup (for RAG with Redis vector store)

```bash
# Start Redis Stack container
docker run -p 6379:6379 redis/redis-stack:latest
```

### Issue Management

```bash
# Create a new GitHub issue
gh issue create --title "Issue Title" --body "Issue description"

# List open issues
gh issue list

# Close an issue
gh issue close <issue-number>
```

**Important**: Always create GitHub issues for new features, major refactors, or bug fixes before starting work. This helps with project tracking and documentation.

**CRITICAL REMINDER**: Before implementing any significant changes or new features:
1. **CREATE** a GitHub issue first using `gh issue create`
2. **IMPLEMENT** the feature or fix
3. **CLOSE** the issue when complete using `gh issue close <number>`

This workflow ensures proper documentation and project tracking. Don't forget to close issues upon completion!

## Lab Implementation Workflow

Follow this established workflow when implementing each lab:

### 1. Create GitHub Issue
```bash
gh issue create --title "Lab X: [Lab Title]" --body "Implement Lab X exercises following the labs.md specification"
```

### 2. Create Feature Branch (from main)
```bash
git checkout main
git pull origin main
git checkout -b labX-[descriptive-name]
```

### 3. Implement Starter Code (on feature branch)
- Create test class with TODO comments guiding students
- Include proper imports and basic structure
- Add comprehensive JavaDoc comments explaining each exercise
- Ensure tests fail gracefully with clear guidance

### 4. Merge to Main Branch
```bash
git checkout main
git merge labX-[descriptive-name]
git push origin main
```

### 5. Switch to Solutions Branch
```bash
git checkout solutions
git pull origin solutions
```

### 6. Implement Complete Solutions (on solutions branch)
- Replace all TODO comments with working implementations
- Ensure all tests pass with OPENAI_API_KEY set
- Include proper error handling and assertions
- Add detailed logging/output for educational value

### 7. Test and Commit Solutions
```bash
OPENAI_API_KEY=your_key ./gradlew test --tests [TestClass]
git add .
git commit -m "Complete Lab X: [Lab Title] implementation

- Implement all working test methods
- Add proper error handling and assertions  
- Include educational logging and output
- All tests pass with valid API keys

Closes #[issue-number]

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 8. Push and Clean Up
```bash
git push origin solutions
git branch -d labX-[descriptive-name]  # Delete local feature branch
gh issue close [issue-number] --comment "Lab X implementation complete with starter code and solutions"
```

### 9. Verification Steps
- ✅ Main branch has starter code with TODO comments
- ✅ Solutions branch has complete working implementation
- ✅ All tests pass on solutions branch
- ✅ Feature branch deleted
- ✅ GitHub issue closed
- ✅ labs.md documentation matches implementation

**Important**: Always test solutions before committing. Use `OPENAI_API_KEY=your_key ./gradlew test` to verify all tests pass.

## CRITICAL: Branch Management Guidelines

**⚠️ NEVER merge main branch into solutions branch without careful review!**

### Branch Purposes
- **`main` branch**: Starter code with TODO stubs for students
- **`solutions` branch**: Complete working implementations for reference

### Safe Merge Practices
1. **Before any merge**: Always check target branch has complete implementations
2. **Use selective merging**: Cherry-pick specific commits rather than full merges
3. **Documentation-only merges**: Only merge documentation/config changes, never test code
4. **Verify after merge**: Run tests to ensure solutions still work

### Emergency Recovery
If solutions are accidentally overwritten:
```bash
# Find the last good commit with complete implementations
git log --oneline solutions --grep="complete\|implement\|working"

# Restore specific files from earlier commit
git checkout <good-commit-hash> -- src/test/java/com/oreilly/springaicourse/
git checkout <good-commit-hash> -- src/main/java/com/oreilly/springaicourse/

# Commit the restoration
git commit -m "Restore complete implementations from backup"
```

**Remember**: Solutions branch should NEVER have TODO comments in test methods!

## Required Environment Variables

Set these environment variables before running tests and examples:

```bash
export OPENAI_API_KEY=your_openai_api_key
export GOOGLEAI_API_KEY=your_google_api_key  # Required for Lab 7.3 audio processing
```

## Common Tasks

### Adding Navigation to Exercise Files

To add a table of contents with navigable links to any tutorial/exercise file:

1. Add a table of contents section at the top like this:
```markdown
## Table of Contents

- [Exercise 1: Basic Setup](#exercise-1-basic-setup)
- [Exercise 2: Advanced Features](#exercise-2-advanced-features)
```

2. For IntelliJ IDEA compatibility, use standard Markdown heading anchors (headings automatically generate anchors based on their text)

3. Add return links at the end of each section:
```markdown
[↑ Back to table of contents](#table-of-contents)
```

Note: The anchor names in the links should match the heading text (lowercase, with hyphens replacing spaces and special characters removed).

Example structure:
```markdown
## Table of Contents

- [Lab 1: Getting Started](#lab-1-getting-started)
- [Lab 2: Core Concepts](#lab-2-core-concepts)

## Lab 1: Getting Started

Content here...

[↑ Back to table of contents](#table-of-contents)

## Lab 2: Core Concepts

Content here...

[↑ Back to table of contents](#table-of-contents)
```

This pattern is useful for any long tutorial or exercise file to improve navigation.

## Code Architecture

### Key Components

1. **AI Model Clients**
   - `ChatModel` - Primary interface for interacting with AI models (LangChain4j 1.0)
   - Model-specific implementations for OpenAI and Google AI
   - Configured with builder patterns

2. **Memory and Tools**
   - `ChatMemory` - Maintains conversation history
   - `AiServices` - High-level interface for AI interactions
   - `@Tool` - Annotation for defining AI-callable functions

3. **RAG System**
   - `EmbeddingStore` - Stores document embeddings (In-memory or Redis)
   - Document loaders for various sources (PDF, web)
   - Text splitters for chunking documents
   - Embedding models for semantic search

4. **Content Handling**
   - `DocumentSplitter` - Splits documents into chunks
   - `ContentRetriever` - Retrieves relevant content for RAG
   - `ChatMemoryProvider` - Manages conversation state

5. **Configuration**
   - Builder patterns for all components
   - Environment variable-based configuration
   - No profiles needed - direct Java configuration

### Vector Store Implementation

The project uses the following vector store implementations:

1. **In-Memory EmbeddingStore** (Lab 9)
   - Simple in-memory vector store
   - Good for development and testing
   - No external dependencies

2. **Chroma EmbeddingStore** (Lab 10)
   - Persistent vector store using Chroma
   - Requires running Chroma server: `docker run -p 8000:8000 chromadb/chroma`
   - HTTP-based REST API
   - Good for production use with persistence

## Training Course Structure

This is a **hands-on training course** where students implement LangChain4j functionality progressively:

### Learning Approach
- **Main branch**: Students start here with TODO-guided starter code
- **Solutions branch**: Complete implementations for reference
- **Progressive labs**: Each lab builds on previous knowledge
- **Hands-on implementation**: Students learn by coding, not copying

### Lab Progression
The course follows a structured progression documented in `labs.md`:
1. **Basic chat interactions** - Simple AI conversations
2. **Streaming responses** - Real-time AI communication
3. **Structured data extraction** - AI-powered data parsing
4. **AI Services interface** - High-level AI integration patterns
5. **Chat memory** - Conversation context and multi-user memory isolation
6. **AI Tools** - Function calling with @Tool annotation (IMPLEMENTED)
6.5. **MCP Integration** - External tool integration via Model Context Protocol (IMPLEMENTED)
7. **Multimodal capabilities** - Image analysis with GPT-4 Vision, audio with Google Gemini
8. **Image generation** - AI-created images with DALL-E
9. **RAG implementation** - Knowledge-augmented AI
10. **Vector store optimization** - Production-ready RAG with Redis

**Note**: Lab ordering was optimized for pedagogical flow - tools before vision/image generation.

### Code Structure for Students
- **Test classes**: Contain TODO comments guiding implementation
- **Service classes**: Skeleton code with clear instructions
- **Working examples**: DateTimeTools, ActorFilms (students use these)
- **Reference implementations**: Available in solutions branch

## Implementation Best Practices (Lessons Learned)

### Tool Classes Architecture
**IMPORTANT**: Tool classes should be standalone classes in `src/main/java`, NOT static inner classes in test files.

**Correct Structure:**
```
src/main/java/com/kousenit/langchain4j/
├── DateTimeTool.java      # @Tool methods for date/time operations
├── WeatherTool.java       # @Tool methods for weather simulation
├── CalculatorTool.java    # @Tool methods for math operations
└── ActorFilms.java        # Data classes for structured extraction
```

**Benefits:**
- **Reusability** across multiple test classes
- **Proper separation of concerns** (tools vs tests)
- **Professional code organization** 
- **Students can study tool implementation patterns**

### Testing Strategy: Hybrid JUnit 5 + AssertJ
Use JUnit 5 `assertAll()` for grouping related assertions and AssertJ for complex string/object validation:

```java
// Group related assertions
assertAll("Basic tool usage validation",
    () -> assertNotNull(response1, "Response should not be null"),
    () -> assertFalse(response1.trim().isEmpty(), "Response should not be empty")
);

// Complex content validation with AssertJ
assertThat(response2.toLowerCase())
    .as("Location-specific response")
    .containsAnyOf("new york", "york");
```

**Key Lessons:**
- **Avoid case sensitivity issues** with `.toLowerCase()` + `containsAnyOf()`
- **Don't test exact AI response wording** - test for essential keywords
- **Use proper AssertJ methods** - no `containsAnyOfIgnoringCase()` (doesn't exist)

### Lab Documentation Synchronization
**CRITICAL**: `labs.md` on solutions branch is the authoritative version.

**Process:**
1. Update `labs.md` on solutions branch first
2. Copy to main branch: `git checkout solutions -- labs.md`
3. Verify both branches have identical content: `git diff main solutions -- labs.md`

### Memory Pattern Examples
Lab 5 includes the crucial **multi-user memory pattern** with `@MemoryId`:

```java
interface MultiUserAssistant {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}

// Separate memory per user
.chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
```

This pattern is **essential for production conversational AI applications**.

### Lab 6.5: MCP Integration (IMPLEMENTED)
- **MCP Integration**: ✅ Lab 6.5 complete - LangChain4j provides MCP client support (not server)
  - Uses npx command: `npx -y @modelcontextprotocol/server-everything`
  - Shared MCP client pattern for optimal test performance
  - Tool name conflict resolution (avoid CalculatorTool with MCP servers)
  - Prerequisites: Node.js/npm (not Docker)

### Future Considerations  
- **Multimodal capabilities**: ✅ Lab 7 complete - includes:
  - Image analysis with GPT-4 Vision (local/remote images, structured data extraction)
  - Audio processing with Google Gemini 2.5 Flash Preview model
- **Error handling patterns**: Include division by zero, invalid inputs in tool examples

### Audio Processing Update (Lab 7.3)
- **Model**: Google Gemini 2.5 Flash Preview (`gemini-2.5-flash-preview-05-20`)
- **API Key**: Requires `GOOGLEAI_API_KEY` environment variable
- **Audio Format**: MP3 files, Base64 encoded
- **Test Annotation**: Uses `@EnabledIfEnvironmentVariable` for conditional execution
- **Method Update**: `readSimpleAudioData()` loads and encodes audio from resources

### Course Materials Organization
- **Main course content**: Root directory (Java source, tests, labs.md)
- **Presentation slides**: `slides/` directory (Slidev-based presentation)
  - Run with: `cd slides && npm install && npm run dev`
  - Contains comprehensive overview of all LangChain4j features
  - Updated to reflect LangChain4j 1.0+ API changes

### Recent Updates (June 2025)
- **PDF Parsing**: Lab 10.3 now demonstrates Apache Tika PDF parsing with real PDF files
- **Slides Reorganization**: Moved all slide materials to dedicated `slides/` directory
- **API Corrections**: Fixed slides to use correct LangChain4j 1.0+ APIs (ChatModel, chat() method, etc.)