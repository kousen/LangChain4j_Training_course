# LangChain4j Training Course

A hands-on training course for learning LangChain4j through progressive lab exercises.

## Getting Started

This repository contains starter code for a comprehensive LangChain4j training course. Students build functionality incrementally through guided TODO exercises.

### Repository Structure

- **`main` branch**: Starter code with TODO-guided exercises
- **`solutions` branch**: Complete implementations for reference
- **`labs.md`**: Step-by-step lab instructions and exercises

### Prerequisites

1. **Java 17+**
2. **Environment Variables**:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key
   # Optional for Lab 7.3 audio processing:
   export GOOGLEAI_API_KEY=your_google_api_key
   ```
3. **Docker** (for Chroma vector store in Lab 10):
   ```bash
   docker run -p 8000:8000 chromadb/chroma
   ```
4. **Node.js/npm** (for slides and MCP integration)

### Quick Start

```bash
# Clone the repository (main branch contains starter code)
git clone https://github.com/kousen/LangChain4j_Training_Course.git
cd LangChain4j_Training_Course

# Build the project
./gradlew build

# Run basic tests (many will be empty until you implement them)
./gradlew test

# View complete solutions (when needed)
git checkout solutions
```

## Course Structure

Follow the exercises in [labs.md](labs.md) to build LangChain4j applications from scratch:

1. **Basic Chat Interactions** - Simple AI conversations
2. **Streaming Responses** - Real-time AI responses  
3. **Structured Data Extraction** - AI-powered data parsing
4. **Prompt Templates** - Reusable AI prompts
5. **Chat Memory** - Conversation context and multi-user memory isolation
6. **AI Tools** - Function calling
7. **Multimodal Capabilities** - Image and audio analysis
8. **Image Generation** - AI-created images
9. **Retrieval-Augmented Generation (RAG)** - AI with knowledge base
10. **Production RAG** - Redis vector store optimization

## Learning Approach

- **Start with TODOs**: Each test class contains guided TODO comments
- **Build incrementally**: Complete one lab before moving to the next
- **Reference solutions**: Check the `solutions` branch when needed
- **Hands-on learning**: Learn by implementing, not copying

## Presentation Slides

Interactive presentation slides are available in the [`slides/`](slides/) directory:

```bash
cd slides
npm install
npm run dev
```

See [`slides/README.md`](slides/README.md) for full setup instructions.

## Support

- **Lab Instructions**: See [labs.md](labs.md)
- **Complete Examples**: Switch to `solutions` branch
- **Issues**: Report problems via GitHub issues