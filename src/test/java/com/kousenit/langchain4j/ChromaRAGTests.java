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
        // TODO: Check if Chroma is available, skip test if not
        // assumeTrue(isChromaAvailable(), "Chroma is not available");

        // TODO: Create embedding model
        // EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        
        // TODO: Create Chroma embedding store with random UUID collection name
        // EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
        //         .baseUrl("http://localhost:8000")
        //         .collectionName(randomUUID())
        //         .logRequests(true)
        //         .logResponses(true)
        //         .build();

        // TODO: Sample documents about programming languages
        // List<Document> documents = Arrays.asList(
        //     Document.from("Python is a high-level programming language known for its simplicity and readability."),
        //     Document.from("Java is a popular object-oriented programming language that runs on the JVM."),
        //     Document.from("JavaScript is the language of the web, used for both frontend and backend development."),
        //     Document.from("Rust is a systems programming language focused on safety and performance.")
        // );

        // TODO: Split and embed documents
        // DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
        // List<TextSegment> segments = splitter.splitAll(documents);
        // 
        // List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        // 
        // // Add all embeddings at once (more efficient)
        // embeddingStore.addAll(embeddings, segments);
        // 
        // System.out.println("Added " + segments.size() + " segments to Chroma");

        // TODO: Test search
        // String query = "What language is good for web development?";
        // Embedding queryEmbedding = embeddingModel.embed(query).content();
        // 
        // List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
        //     EmbeddingSearchRequest.builder()
        //             .queryEmbedding(queryEmbedding)
        //             .maxResults(2)
        //             .build()
        // ).matches();
        // 
        // System.out.println("Search results for: " + query);
        // matches.forEach(match -> 
        //     System.out.println("- " + match.embedded().text() + " (score: " + match.score() + ")")
        // );

        // TODO: Verify results
        // assertFalse(matches.isEmpty());
        
        System.out.println("TODO: Implement Chroma vector store setup");
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
        // TODO: Implement RAG with Chroma persistence
        System.out.println("TODO: Implement RAG with Chroma persistence");
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
        // TODO: Implement Chroma data management
        System.out.println("TODO: Implement Chroma data management");
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
        // TODO: Implement production RAG configuration
        System.out.println("TODO: Implement production RAG configuration");
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