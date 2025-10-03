plugins {
    id("java")
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.kousenit"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // LangChain4j BOM for version management
    implementation(platform("dev.langchain4j:langchain4j-bom:1.7.1"))

    // Core LangChain4j
    implementation("dev.langchain4j:langchain4j")

    // LangChain4j model integrations
    implementation("dev.langchain4j:langchain4j-open-ai")
    implementation("dev.langchain4j:langchain4j-anthropic")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini")

    // Document processing and RAG
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2")
    implementation("dev.langchain4j:langchain4j-easy-rag")

    // Vector stores
    implementation("dev.langchain4j:langchain4j-chroma")

    // MCP (Model Context Protocol) support
    implementation("dev.langchain4j:langchain4j-mcp")

    // Security fix: override vulnerable transitive dependency
    implementation("org.apache.poi:poi-ooxml:5.4.0")

    // Utilities for exercises
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    // Configure ratcheting to only format files changed from main branch
    // ratchetFrom("origin/main")

    java {
        target("src/**/*.java")
        palantirJavaFormat("2.63.0")  // This is what langchain4j uses
    }

    // If you have Kotlin files
    kotlin {
        target("src/**/*.kt")
        ktlint()
    }

    // If you have Groovy files
    groovy {
        target("src/**/*.groovy")
        greclipse()
    }

    // Format build files and other misc files
    format("misc") {
        target("*.gradle", "*.gradle.kts", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
}
