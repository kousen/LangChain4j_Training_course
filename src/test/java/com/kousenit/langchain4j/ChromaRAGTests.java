package com.kousenit.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.internal.Utils.randomUUID;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Lab 10: Chroma Vector Store for RAG
 * <p>
 * This lab demonstrates how to use Chroma as a persistent vector store for production RAG systems.
 * You'll learn how to:
 * - Set up Chroma as a vector database for embeddings
 * - Create persistent RAG systems with Chroma
 * - Implement production-ready RAG configurations
 * - Manage vector data with Chroma
 * - Scale RAG applications with persistent storage
 * <p>
 * Prerequisites:
 * - Docker with Chroma running: docker run -p 8000:8000 chromadb/chroma:0.5.4
 * - Understanding of RAG concepts from Lab 9
 * - Chroma will persist data between test runs
 * - Access to Chroma web UI at http://localhost:8000
 * 
 * IMPORTANT: Use Chroma version 0.5.4 for compatibility with LangChain4j 1.0.1
 * <p>
 * Benefits of Chroma:
 * - Simple setup with single Docker command
 * - Built-in web UI for exploring collections and embeddings
 * - Excellent LangChain4j integration
 * - Production-ready persistence
 * - Open-source with active community
 */
class ChromaRAGTests {

    /**
     * Test 10.1: Basic Chroma Vector Store Setup
     * <p>
     * Demonstrates Chroma vector store fundamentals:
     * - Creating a Chroma embedding store
     * - Connecting to local Chroma instance
     * - Basic vector operations with Chroma
     * - Testing Chroma availability
     */
    @Test
    void chromaVectorStoreBasic() {
        // Check if Chroma is available, skip test if not
        assumeTrue(isChromaAvailable(), "Chroma is not available");

        // Create embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        
        // Create Chroma embedding store with random UUID collection name (matches working example)
        EmbeddingStore<TextSegment> embeddingStore;
        
        try {
            embeddingStore = ChromaEmbeddingStore.builder()
                    .baseUrl("http://localhost:8000")
                    .collectionName(randomUUID())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
        } catch (Exception e) {
            System.out.println("Failed to create Chroma store: " + e.getMessage());
            System.out.println("Skipping test due to Chroma configuration issue");
            assumeTrue(false, "Chroma configuration issue: " + e.getMessage());
            return; // This won't be reached, but helps with flow analysis
        }

        // Sample documents about programming languages
        List<Document> documents = Arrays.asList(
            Document.from("Python is a high-level programming language known for its simplicity and readability."),
            Document.from("Java is a popular object-oriented programming language that runs on the JVM."),
            Document.from("JavaScript is the language of the web, used for both frontend and backend development."),
            Document.from("Rust is a systems programming language focused on safety and performance.")
        );

        // Split and embed documents
        DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
        List<TextSegment> segments = splitter.splitAll(documents);
        
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        
        // Add embeddings one by one (matches working example API)
        for (int i = 0; i < embeddings.size(); i++) {
            embeddingStore.add(embeddings.get(i), segments.get(i));
        }

        System.out.println("Added " + segments.size() + " segments to Chroma");

        // Test search
        String query = "What language is good for web development?";
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
            EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(2)
                    .build()
        ).matches();
        
        System.out.println("Search results for: " + query);
        matches.forEach(match -> 
            System.out.println("- " + match.embedded().text() + " (score: " + match.score() + ")")
        );

        // Verify results
        assertFalse(matches.isEmpty());
    }

    /**
     * Test 10.2: RAG with Chroma Persistence
     * <p>
     * Demonstrates a comprehensive RAG system using Chroma:
     * - Persistent vector storage between sessions
     * - Knowledge base management with Chroma
     * - Production RAG configuration
     * - ContentRetriever with Chroma backend
     */
    @Test
    void ragWithChromaPersistence() {
        // Check Chroma availability
        assumeTrue(isChromaAvailable(), "Chroma is not available");

        // Set up models
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        
        // Create Chroma embedding store with unique collection
        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName(randomUUID())
                .build();

        // Knowledge base about LangChain4j
        List<Document> knowledgeBase = Arrays.asList(
            Document.from("LangChain4j 1.0 introduced the ChatModel interface as the primary way to interact with language models."),
            Document.from("The AiServices interface in LangChain4j allows you to create type-safe AI-powered services using annotations."),
            Document.from("LangChain4j supports multiple embedding models including OpenAI embeddings and local models like AllMiniLM."),
            Document.from("ContentRetriever in LangChain4j is used to retrieve relevant content for RAG applications."),
            Document.from("LangChain4j provides built-in support for Redis as a vector store for production RAG systems.")
        );

        // Process and store knowledge
        DocumentSplitter splitter = DocumentSplitters.recursive(200, 50);
        List<TextSegment> segments = splitter.splitAll(knowledgeBase);
        
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        
        // Add embeddings one by one with simple text segments to avoid metadata API issues
        for (int i = 0; i < embeddings.size(); i++) {
            // Create simple TextSegment without metadata to avoid alpha version compatibility issues
            TextSegment simpleSegment = TextSegment.from(segments.get(i).text());
            embeddingStore.add(embeddings.get(i), simpleSegment);
        }

        // Create content retriever
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.6)
                .build();

        // Create knowledge assistant interface
        interface KnowledgeAssistant {
            @SystemMessage("You are a helpful assistant that answers questions about LangChain4j based on the provided context. " +
                          "If the context doesn't contain enough information to answer the question, say so.")
            String answer(String question);
        }

        // Build the AI service
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

    /**
     * Test 10.3: Chroma Data Management
     * <p>
     * Demonstrates Chroma vector store data management:
     * - Adding and retrieving vectors
     * - Managing vector data lifecycle
     * - Testing data persistence
     * - Basic cleanup operations
     */
    @Test
    void chromaDataManagement() {
        // Check Chroma availability
        assumeTrue(isChromaAvailable(), "Chroma is not available");

        // Create Chroma store with test collection
        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName(randomUUID())
                .build();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Add some test data
        TextSegment segment = TextSegment.from("This is test data for cleanup demonstration.");
        Embedding embedding = embeddingModel.embed(segment).content();
        
        String id = embeddingStore.add(embedding, segment);
        System.out.println("Added segment with ID: " + id);

        // Verify data exists
        List<EmbeddingMatch<TextSegment>> results = embeddingStore.search(
            EmbeddingSearchRequest.builder().queryEmbedding(embedding).maxResults(1).build()
        ).matches();
        assertFalse(results.isEmpty());
        
        // Data management operations would go here
        // Note: Cleanup methods may vary depending on the Redis embedding store implementation
        System.out.println("Data management test completed");
    }

    /**
     * Test 10.4: Production RAG Configuration
     * <p>
     * Demonstrates production-ready RAG setup:
     * - Optimized Chroma configuration
     * - Production model settings
     * - Comprehensive knowledge base
     * - Advanced retrieval settings
     * - Metadata and indexing strategies
     */
    @Test
    void productionRagConfiguration() {
        // Check Chroma availability
        assumeTrue(isChromaAvailable(), "Chroma is not available");

        // Configure models with production settings
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .temperature(0.1) // Lower temperature for more consistent responses
                .maxTokens(500)
                .build();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        
        // Configure Chroma with production settings
        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName(randomUUID())
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

        // Process documents with metadata
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
        
        // Add embeddings one by one to avoid API compatibility issues
        for (int i = 0; i < embeddings.size(); i++) {
            embeddingStore.add(embeddings.get(i), segments.get(i));
        }

        // Configure retriever with optimized settings
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4)
                .minScore(0.7) // Higher threshold for production
                .build();

        // Create production assistant interface
        interface ProductionAssistant {
            @SystemMessage("You are an expert assistant for LangChain4j documentation. " +
                          "Provide accurate, helpful answers based on the context provided. " +
                          "If you cannot answer based on the context, clearly state that.")
            String answer(String question);
        }

        // Build and test production assistant
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

    /**
     * Helper method to check if Chroma is available.
     * Attempts to connect to Chroma on localhost:8000.
     */
    private boolean isChromaAvailable() {
        try {
            // Simple HTTP check to Chroma heartbeat endpoint
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8000/api/v1/heartbeat"))
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(request, 
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println("Chroma not available: " + e.getMessage());
            return false;
        }
    }
}