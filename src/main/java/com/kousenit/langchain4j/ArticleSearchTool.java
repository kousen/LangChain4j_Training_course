package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.List;

/**
 * ArticleSearchTool demonstrates {@code @P(defaultValue = "...")} (LangChain4j 1.15+).
 * <p>
 * When the LLM omits an argument, LangChain4j substitutes the declared
 * default before invoking the method. The parameter is also marked
 * optional in the JSON schema, so the model is explicitly told it may
 * skip it. Compare to {@code Optional<T>} (Lab 6.5): pick {@code Optional}
 * when absence is meaningful business logic, and {@code defaultValue}
 * when the tool has a sensible fallback the LLM shouldn't have to think
 * about.
 * <p>
 * This tool shows the three most useful default-value shapes: a
 * primitive ({@code int}), an enum, and a {@code List<String>} parsed
 * from a JSON literal. The body returns canned data so the LLM has
 * something deterministic to summarize.
 * <p>
 * Used in Lab 6: AI Tools exercises.
 */
public class ArticleSearchTool {

    public enum SortBy {
        RELEVANCE,
        DATE,
        RATING
    }

    @Tool(
            "Search articles matching a query. Optional: limit (default 10), sortBy (default RELEVANCE), languages (default English).")
    public String searchArticles(
            @P("Search query") String query,
            @P(value = "Maximum number of results", defaultValue = "10") int limit,
            @P(value = "Sort order", defaultValue = "RELEVANCE") SortBy sortBy,
            @P(value = "ISO language codes to include", defaultValue = "[\"en\"]") List<String> languages) {

        return String.format(
                "Found %d articles for query \"%s\" (sorted by %s, languages=%s):%n"
                        + "  1. %s: A Deep Dive%n"
                        + "  2. %s: Best Practices for 2026%n"
                        + "  3. The Future of %s",
                limit, query, sortBy, languages, query, query, query);
    }
}
