plugins {
    id("java")
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
    implementation(platform("dev.langchain4j:langchain4j-bom:1.0.1"))
    
    // Core LangChain4j
    implementation("dev.langchain4j:langchain4j")
    
    // LangChain4j model integrations
    implementation("dev.langchain4j:langchain4j-open-ai")
    implementation("dev.langchain4j:langchain4j-anthropic")
    
    // Document processing and RAG
    implementation("dev.langchain4j:langchain4j-document-loader-apache-tika")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q")
    implementation("dev.langchain4j:langchain4j-easy-rag")
    
    // Vector stores
    implementation("dev.langchain4j:langchain4j-redis")
    implementation("redis.clients:jedis:5.1.0")
    
    // Utilities for exercises
    implementation("org.slf4j:slf4j-simple:2.0.9")
    
    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
}