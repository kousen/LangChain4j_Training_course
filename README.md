# LangChain4j Training Course

A hands-on training course for learning LangChain4j (pinned to 1.15.0) through progressive lab exercises.

## Getting Started

This repository contains starter code for a comprehensive LangChain4j training course. Students build functionality incrementally through guided TODO exercises.

### Repository Structure

- **`main` branch**: Starter code with TODO-guided exercises
- **`solutions` branch**: Complete implementations for reference
- **`labs.md`**: Step-by-step lab instructions and exercises

### Prerequisites

1. **Java 17+** (the Gradle wrapper is 9.1+, so Java 25 is supported)
2. **Environment Variables**:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key
   ```

### Quick Start

```bash
# Clone the repository (main branch contains starter code)
git clone https://github.com/kousen/LangChain4j_Training_course.git
cd LangChain4j_Training_course

# Build the project
./gradlew build

# Run basic tests (many will be empty until you implement them)
./gradlew test

# View complete solutions (when needed)
git checkout solutions
```

### Code Style

This project uses automatic code formatting with [Spotless](https://github.com/diffplug/spotless) and [Palantir Java Format](https://github.com/palantir/palantir-java-format):

```bash
# Check code formatting
./gradlew spotlessCheck

# Apply code formatting
./gradlew spotlessApply
```

The formatting style matches LangChain4j's standards for consistency.

### Slides PDF

A current PDF of the slides is rebuilt automatically whenever `slides/slides.md` lands on `main` or `solutions` and attached to a rolling release:

**<https://github.com/kousen/LangChain4j_Training_course/releases/latest/download/slides-export.pdf>**

The PDF is not committed to the repo; it is built by [`.github/workflows/build-slides-pdf.yml`](.github/workflows/build-slides-pdf.yml).

## Course Structure

Follow the exercises in [labs.md](labs.md) to build LangChain4j applications from scratch:

1. **Basic Chat Interactions** - Simple AI conversations
2. **Streaming Responses** - Real-time AI responses, with mid-flight cancellation
3. **Structured Data Extraction** - AI-powered data parsing
4. **AI Services Interface** - Type-safe service interfaces (incl. per-call `ChatRequestParameters`)
5. **Chat Memory** - Conversation context, multi-user isolation, `ChatMemory.set()`
6. **AI Tools** - Function calling, including `Optional` parameters
6.5. **MCP Integration** - External tools via Model Context Protocol (spec 2025-11-25)
7. **Multimodal Capabilities** - Image analysis, OpenAI transcription
8. **Image Generation** - `gpt-image-2`
9. **Retrieval-Augmented Generation (RAG)** - AI with a knowledge base
10. **Chroma Vector Store for RAG** - Persistent vector storage
11. **Agentic API** - Compose multi-step LLM workflows

## Learning Approach

- **Start with TODOs**: Each test class contains guided TODO comments
- **Build incrementally**: Complete one lab before moving to the next
- **Reference solutions**: Check the `solutions` branch when needed
- **Hands-on learning**: Learn by implementing, not copying

## Support

- **Lab Instructions**: See [labs.md](labs.md)
- **Complete Examples**: Switch to `solutions` branch
- **Issues**: Report problems via GitHub issues
