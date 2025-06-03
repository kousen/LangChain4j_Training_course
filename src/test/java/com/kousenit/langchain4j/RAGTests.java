package com.kousenit.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
        // TODO: Create an embedding model using AllMiniLmL6V2EmbeddingModel
        // EmbeddingModel embeddingModel = ...
        
        // TODO: Create an in-memory embedding store
        // EmbeddingStore<TextSegment> embeddingStore = ...

        // TODO: Create some sample documents about LangChain4j
        // List<Document> documents = Arrays.asList(
        //     Document.from("LangChain4j is a Java library for building AI applications."),
        //     ...
        // );

        // TODO: Split documents into segments using DocumentSplitters.recursive(100, 20)
        // DocumentSplitter splitter = ...
        // List<TextSegment> segments = ...

        // TODO: Embed all segments and add to the embedding store
        // List<Embedding> embeddings = ...
        // embeddingStore.addAll(...);

        System.out.println("TODO: Implement document embedding");
        
        // TODO: Test similarity search with a query
        // String query = "What is LangChain4j?";
        // Embedding queryEmbedding = ...
        // List<EmbeddingMatch<TextSegment>> matches = ...
        
        // TODO: Print and verify the results
        // System.out.println("Found " + matches.size() + " relevant segments:");
        // matches.forEach(...);

        // TODO: Add assertions to verify results
        // assertFalse(matches.isEmpty());
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
        // TODO: Set up the chat model
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .build();

        // TODO: Set up embedding model and store
        // EmbeddingModel embeddingModel = ...
        // EmbeddingStore<TextSegment> embeddingStore = ...

        // TODO: Load and process documents about Java
        // List<Document> documents = Arrays.asList(
        //     Document.from("Java is a programming language and computing platform first released by Sun Microsystems in 1995."),
        //     ...
        // );

        // TODO: Split and embed documents
        // DocumentSplitter splitter = DocumentSplitters.recursive(200, 50);
        // ...

        // TODO: Create a content retriever
        // ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        //         .embeddingStore(embeddingStore)
        //         .embeddingModel(embeddingModel)
        //         .maxResults(2)
        //         .minScore(0.5)
        //         .build();

        // TODO: Create RAG-enabled assistant interface
        // interface RagAssistant {
        //     String answer(String question);
        // }

        // TODO: Build the AI service with content retriever
        // RagAssistant assistant = AiServices.builder(RagAssistant.class)
        //         .chatModel(chatModel)
        //         .contentRetriever(retriever)
        //         .build();

        // TODO: Test RAG with a question
        // String question = "When was Java first released?";
        // String answer = assistant.answer(question);
        
        System.out.println("TODO: Implement RAG with ContentRetriever");

        // TODO: Verify the answer contains correct information
        // assertNotNull(answer);
        // assertTrue(answer.contains("1995"));
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
        // TODO: Create a sample text file for testing
        // Path tempFile = Files.createTempFile("sample", ".txt");
        // Files.writeString(tempFile, """
        //     LangChain4j is a powerful Java library for building applications with Large Language Models (LLMs).
        //     
        //     Key features include:
        //     - Integration with multiple AI providers (OpenAI, Anthropic, etc.)
        //     ...
        //     """);

        // try {
            // TODO: Set up models
            // ChatModel chatModel = ...
            // EmbeddingModel embeddingModel = ...
            // EmbeddingStore<TextSegment> embeddingStore = ...

            // TODO: Load document from file
            // Document document = FileSystemDocumentLoader.loadDocument(tempFile);
            
            // TODO: Process and embed the document
            // DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
            // ...

            // TODO: Create content retriever
            // ContentRetriever retriever = ...

            // TODO: Create document assistant interface
            // interface DocumentAssistant {
            //     String answer(String question);
            // }

            // TODO: Build and test the assistant
            // DocumentAssistant assistant = ...
            // String answer = assistant.answer("What are the key features of LangChain4j?");
            
            System.out.println("TODO: Implement RAG with file documents");

            // TODO: Verify results
            // assertNotNull(answer);
            // assertTrue(answer.toLowerCase().contains("langchain4j") || 
            //           answer.toLowerCase().contains("feature"));

        // } finally {
            // TODO: Clean up temporary file
            // Files.deleteIfExists(tempFile);
        // }
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
        // TODO: Set up models
        // ChatModel chatModel = ...
        // EmbeddingModel embeddingModel = ...
        // EmbeddingStore<TextSegment> embeddingStore = ...

        // TODO: Create documents with metadata
        // List<Document> javaDocs = Arrays.asList(
        //     Document.from("Java was created by James Gosling at Sun Microsystems.")
        //             .toBuilder().metadata("language", "java").metadata("topic", "history").build(),
        //     ...
        // );

        // List<Document> pythonDocs = Arrays.asList(
        //     Document.from("Python was created by Guido van Rossum in 1991.")
        //             .toBuilder().metadata("language", "python").metadata("topic", "history").build(),
        //     ...
        // );

        // TODO: Combine and process all documents
        // List<Document> allDocs = new ArrayList<>();
        // allDocs.addAll(javaDocs);
        // allDocs.addAll(pythonDocs);

        // TODO: Split and embed documents
        // DocumentSplitter splitter = ...
        // ...

        // TODO: Create retriever with metadata filtering
        // ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        //         .embeddingStore(embeddingStore)
        //         .embeddingModel(embeddingModel)
        //         .maxResults(2)
        //         .build();

        // TODO: Create language-specific assistant
        // interface LanguageAssistant {
        //     @SystemMessage("Answer questions based only on the provided context about programming languages.")
        //     String answerAboutLanguage(String question);
        // }

        // TODO: Build and test the assistant
        // LanguageAssistant assistant = ...
        // String answer = assistant.answerAboutLanguage("Who created Java and when?");
        
        System.out.println("TODO: Implement advanced RAG with metadata");

        // TODO: Verify the answer
        // assertNotNull(answer);
        // assertTrue(answer.toLowerCase().contains("james gosling") || answer.toLowerCase().contains("sun"));
    }

    /**
     * Helper method to check if Redis is available.
     * Used for Lab 10 Redis tests.
     */
    private boolean isRedisAvailable() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}