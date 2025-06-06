package com.kousenit.langchain4j;

import java.util.List;

/**
 * Record representing an actor and their filmography.
 * Used in Lab 3: Structured Data Extraction exercises.
 * <p>
 * This record demonstrates how LangChain4j can extract structured data
 * from AI responses and automatically map it to Java objects.
 */
public record ActorFilms(String actor, List<String> movies) {}
