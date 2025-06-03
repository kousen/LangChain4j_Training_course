package com.kousenit.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 9: Retrieval-Augmented Generation (RAG)
 * <p>
 * This lab demonstrates how to build RAG systems with LangChain4j.
 * You'll learn how to:
 * - Load and embed documents for semantic search
 * - Create an embedding store for vector similarity search
 * - Implement content retrieval for augmenting AI responses
 * - Build RAG-enabled AI services
 * - Use Redis as a persistent vector store for production
 * <p>
 * Prerequisites:
 * - Understanding of embeddings and vector similarity
 * - Basic knowledge of document processing
 * - For Lab 10: Docker with Redis Stack (optional)
 */
class RAGTests {

    /**
     * Test 9.1: Basic Document Loading and Embedding
     * <p>
     * Demonstrates the fundamentals of document processing:
     * - Creating an embedding model
     * - Setting up an in-memory embedding store
     * - Processing documents into embeddings
     * - Performing similarity search
     */
    @Test
    void basicDocumentEmbedding() {
        // Create embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        
        // Create in-memory embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Create some sample documents
        List<Document> documents = List.of(
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

        System.out.printf("Embedded %d document segments%n", segments.size());
        
        // Test similarity search
        String query = "What is LangChain4j?";
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        
        List<EmbeddingMatch<TextSegment>> matches =
                embeddingStore.search(EmbeddingSearchRequest.builder()
                        .queryEmbedding(queryEmbedding)
                        .maxResults(2)
                        .build()).matches();

        System.out.printf("Found %d relevant segments:%n", matches.size());
        matches.forEach(match ->
                System.out.printf("- %s (score: %s)%n", match.embedded().text(), match.score())
        );

        assertFalse(matches.isEmpty());
    }

    /**
     * Test 9.2: RAG with ContentRetriever
     * <p>
     * Demonstrates building a complete RAG system:
     * - Setting up chat and embedding models
     * - Creating a content retriever
     * - Building a RAG-enabled AI service
     * - Testing knowledge-augmented responses
     */
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
        List<Document> documents = List.of(
            Document.from("""
                Java is a programming language and computing platform
                first released by Sun Microsystems in 1995."""),
            Document.from("""
                Java is object-oriented, class-based, and designed to
                have as few implementation dependencies as possible."""),
            Document.from("""
                Java applications are typically compiled to bytecode
                that can run on any Java virtual machine (JVM)."""),
            Document.from("""
            Java is one of the most popular programming languages in use,
            particularly for client-server web applications.""")
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

    /**
     * Test 9.3: RAG with File Documents
     * <p>
     * Demonstrates loading documents from files:
     * - Creating and loading text files
     * - Processing file content for RAG
     * - Building a document-based assistant
     * - Handling file I/O operations
     */
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

            EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
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

    /**
     * Test 9.4: Advanced RAG with Metadata Filtering
     * <p>
     * Demonstrates advanced RAG features:
     * - Adding metadata to documents
     * - Creating documents about different topics
     * - Using metadata for filtering (if supported)
     * - Building topic-specific assistants
     */
    @Test
    void ragWithMetadataFiltering() {
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Create documents with metadata
        List<Document> javaDocs = List.of(
            Document.from("Java was created by James Gosling at Sun Microsystems."),
            Document.from("Java uses automatic memory management with garbage collection.")
        );

        List<Document> pythonDocs = List.of(
            Document.from("Python was created by Guido van Rossum in 1991."),
            Document.from("Python uses reference counting for memory management.")
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

}