package com.kousenit.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
     * Test 10.1: Basic Chroma Vector Store Operations
     * <p>
     * Demonstrates fundamental vector store capabilities:
     * - Creating a Chroma embedding store
     * - Storing and searching document embeddings
     * - Basic vector similarity search
     * - Data persistence verification
     */
    @Test
    void chromaVectorStoreOperations() {
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
        // List<Document> documents = List.of(
        //     Document.from("Python is a high-level programming language known for its simplicity and readability."),
        //     Document.from("Java is a popular object-oriented programming language that runs on the JVM."),
        //     Document.from("JavaScript is the language of the web, used for both frontend and backend development."),
        //     Document.from("Rust is a systems programming language focused on safety and performance."),
        //     Document.from("Go is a statically typed, compiled programming language designed for building scalable systems.")
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

        // TODO: Test multiple searches to verify functionality
        // String[] queries = {
        //     "What language is good for web development?",
        //     "Which language is designed for system programming?",
        //     "What language runs on the JVM?"
        // };
        //
        // for (String query : queries) {
        //     Embedding queryEmbedding = embeddingModel.embed(query).content();
        //     
        //     List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
        //         EmbeddingSearchRequest.builder()
        //                 .queryEmbedding(queryEmbedding)
        //                 .maxResults(2)
        //                 .build()
        //     ).matches();
        //     
        //     System.out.println("\nSearch: " + query);
        //     matches.forEach(match -> 
        //         System.out.printf("- %.3f: %s%n", match.score(), match.embedded().text())
        //     );
        //
        //     // Verify search results
        //     assertFalse(matches.isEmpty(), "Should find matches for: " + query);
        //     assertTrue(matches.get(0).score() > 0.5, "Top match should have decent similarity");
        // }
        
        System.out.println("TODO: Implement Chroma vector store operations");
    }

    /**
     * Test 10.2: Production RAG System with Chroma
     * <p>
     * Demonstrates a complete production-ready RAG implementation:
     * - Comprehensive knowledge base with metadata
     * - Optimized retrieval configuration
     * - AI-powered question answering
     * - Production model settings and error handling
     */
    @Test
    void productionRagSystem() {
        // TODO: Check Chroma availability
        // assumeTrue(isChromaAvailable(), "Chroma is not available");

        // TODO: Configure models with production settings
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.1) // Lower temperature for consistent responses
        //         .maxTokens(500)
        //         .build();

        // TODO: Create embedding model and Chroma store
        // EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        // EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
        //         .baseUrl("http://localhost:8000")
        //         .collectionName(randomUUID())
        //         .build();

        // TODO: Create comprehensive knowledge base about LangChain4j
        // List<Document> documents = List.of(
        //     Document.from("LangChain4j 1.0 introduced the ChatModel interface as the primary way to interact with language models."),
        //     Document.from("The AiServices interface in LangChain4j allows you to create type-safe AI-powered services using annotations."),
        //     Document.from("LangChain4j supports multiple embedding models including OpenAI embeddings and local models like AllMiniLM."),
        //     Document.from("ContentRetriever in LangChain4j is used to retrieve relevant content for RAG applications."),
        //     Document.from("LangChain4j provides built-in support for Chroma as a vector store for production RAG systems."),
        //     Document.from("The @Tool annotation enables AI models to call Java methods during conversations."),
        //     Document.from("RAG (Retrieval-Augmented Generation) allows AI to access external knowledge sources for better answers."),
        //     Document.from("LangChain4j uses builder patterns throughout the library for configuring AI services and models.")
        // );

        // TODO: Process documents with metadata for production use cases
        // DocumentSplitter splitter = DocumentSplitters.recursive(150, 30);
        // List<TextSegment> segments = splitter.splitAll(documents);
        // 
        // for (int i = 0; i < segments.size(); i++) {
        //     TextSegment segment = segments.get(i);
        //     segment.metadata().put("chunk_id", String.valueOf(i));
        //     segment.metadata().put("source", "langchain4j_docs");
        //     segment.metadata().put("created_at", LocalDateTime.now().toString());
        // }
        // 
        // List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        // embeddingStore.addAll(embeddings, segments);
        // System.out.println("Stored " + segments.size() + " knowledge segments in Chroma");

        // TODO: Configure retriever and create AI assistant interface
        // ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        //         .embeddingStore(embeddingStore)
        //         .embeddingModel(embeddingModel)
        //         .maxResults(3)
        //         .minScore(0.6)
        //         .build();
        //
        // interface LangChain4jAssistant {
        //     @SystemMessage("You are an expert assistant for LangChain4j documentation. " +
        //                   "Provide accurate, helpful answers based on the provided context. " +
        //                   "If the context doesn't contain enough information, clearly state that.")
        //     String answer(String question);
        // }
        //
        // LangChain4jAssistant assistant = AiServices.builder(LangChain4jAssistant.class)
        //         .chatModel(chatModel)
        //         .contentRetriever(retriever)
        //         .build();

        // TODO: Test with comprehensive questions and verify response quality
        // String[] questions = {
        //     "What is the primary interface for chat in LangChain4j 1.0?",
        //     "How does LangChain4j support type-safe AI services?",
        //     "What is RAG and how does it help AI applications?",
        //     "How do I use tools with LangChain4j?",
        //     "What vector stores does LangChain4j support?"
        // };
        //
        // System.out.println("\n=== RAG System Q&A Test ===");
        // for (String question : questions) {
        //     String answer = assistant.answer(question);
        //     System.out.println("\nQ: " + question);
        //     System.out.println("A: " + answer);
        //     
        //     assertNotNull(answer, "Answer should not be null");
        //     assertFalse(answer.trim().isEmpty(), "Answer should not be empty");
        //     assertTrue(answer.length() > 20, "Answer should be substantive");
        // }
        // 
        // System.out.println("\n" + "=".repeat(50));
        // System.out.println("Production RAG system test completed successfully!");
        
        System.out.println("TODO: Implement production RAG system with Chroma");
    }

    /**
     * Test 10.3: RAG with Document Parsing
     * <p>
     * Demonstrates realistic document processing with file loading and parsing:
     * - Loading documents from the filesystem
     * - Parsing PDF documents using Apache Tika
     * - Processing real document content through the RAG pipeline
     * - Querying parsed document content with AI
     * 
     * Note: This test uses a real PDF file containing LangChain4j 1.0+ documentation,
     * demonstrating true document parsing capabilities beyond simple text loading.
     */
    @Test
    void ragWithDocumentParsing() {
        // TODO: Check Chroma availability
        // assumeTrue(isChromaAvailable(), "Chroma is not available");

        // TODO: Configure models for document processing
        // ChatModel chatModel = OpenAiChatModel.builder()
        //         .apiKey(System.getenv("OPENAI_API_KEY"))
        //         .modelName(GPT_4_1_NANO)
        //         .temperature(0.2) // Slightly higher for more natural responses
        //         .maxTokens(600)
        //         .build();

        // TODO: Create embedding model and Chroma store
        // EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        // EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
        //         .baseUrl("http://localhost:8000")
        //         .collectionName(randomUUID())
        //         .build();

        // TODO: Load PDF document from resources (Apache Tika will parse the PDF)
        // Path documentPath = Paths.get("src/test/resources/LangChain4j-Modern-Features.pdf");
        // Document document = FileSystemDocumentLoader.loadDocument(documentPath);
        // 
        // System.out.println("Loaded PDF document with " + document.text().length() + " characters");

        // TODO: Split document with appropriate chunk sizes for technical content
        // DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        // List<TextSegment> segments = splitter.split(document);
        // 
        // // Add metadata to track document source
        // for (int i = 0; i < segments.size(); i++) {
        //     TextSegment segment = segments.get(i);
        //     segment.metadata().put("chunk_id", String.valueOf(i));
        //     segment.metadata().put("source_file", "LangChain4j-Modern-Features.pdf");
        //     segment.metadata().put("document_type", "pdf_documentation");
        //     segment.metadata().put("format", "PDF");
        //     segment.metadata().put("processed_at", LocalDateTime.now().toString());
        // }

        // TODO: Generate embeddings and store in Chroma
        // List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        // embeddingStore.addAll(embeddings, segments);
        // System.out.println("Processed and stored " + segments.size() + " document segments");

        // TODO: Configure content retriever for document-based queries
        // ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        //         .embeddingStore(embeddingStore)
        //         .embeddingModel(embeddingModel)
        //         .maxResults(4) // More results for technical queries
        //         .minScore(0.5) // Lower threshold for broader relevant content
        //         .build();

        // TODO: Create specialized assistant interface for document questions
        // interface DocumentAssistant {
        //     @SystemMessage("You are an expert assistant that answers questions based on technical documentation. " +
        //                   "Provide accurate, detailed answers based on the provided context from the document. " +
        //                   "If the document doesn't contain enough information to fully answer the question, " +
        //                   "clearly state what information is available and what is missing.")
        //     String answer(String question);
        // }

        // TODO: Build the document-based RAG system and test with questions
        // DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
        //         .chatModel(chatModel)
        //         .contentRetriever(retriever)
        //         .build();
        //
        // String[] documentQuestions = {
        //     "What are the key API changes introduced in LangChain4j 1.0?",
        //     "How does the new ChatModel interface differ from previous versions?",
        //     "What vector stores are supported for production deployments?",
        //     "What are the recommended best practices for testing LangChain4j applications?",
        //     "How does the @Tool annotation system work in version 1.0?"
        // };
        //
        // System.out.println("\n=== Document-Based RAG Q&A Test ===");
        // for (String question : documentQuestions) {
        //     String answer = assistant.answer(question);
        //     System.out.println("\nQ: " + question);
        //     System.out.println("A: " + answer);
        //     
        //     // Verify response quality for document-based content
        //     assertNotNull(answer, "Answer should not be null");
        //     assertFalse(answer.trim().isEmpty(), "Answer should not be empty");
        //     assertTrue(answer.length() > 30, "Answer should be detailed for technical content");
        // }
        // 
        // System.out.println("\n" + "=".repeat(60));
        // System.out.println("Document parsing and RAG integration test completed successfully!");
        // System.out.println("Document segments processed: " + segments.size());
        // System.out.println("Total characters indexed: " + 
        //     segments.stream().mapToInt(s -> s.text().length()).sum());
        
        System.out.println("TODO: Implement document parsing and RAG integration");
    }

    /**
     * Helper method to check if Chroma is available.
     * Attempts to connect to Chroma on localhost:8000.
     */
    private boolean isChromaAvailable() {
        try {
            // Simple HTTP check to Chroma heartbeat endpoint
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
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
}