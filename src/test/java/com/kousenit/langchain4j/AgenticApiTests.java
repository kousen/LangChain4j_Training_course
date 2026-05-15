package com.kousenit.langchain4j;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Lab 11: Agentic API
 * <p>
 * The {@code langchain4j-agentic} module (introduced across the 1.8 →
 * 1.14 releases) lets you compose multiple LLM-backed agents into
 * workflows. The fundamental building blocks are:
 * <ul>
 *   <li>An <b>agent</b> — a method on an interface annotated with
 *       {@code @Agent}, much like {@code AiServices} but with a name
 *       and an output key.</li>
 *   <li>A <b>composition</b> — combine agents into sequences, loops,
 *       conditionals, or parallel fan-outs via {@code AgenticServices}.</li>
 * </ul>
 * Each agent's output flows into a shared {@code AgenticScope} keyed by
 * {@code outputKey}; downstream agents and exit conditions read from it.
 */
class AgenticApiTests {

    /**
     * Test 11.1: Single Typed Agent
     * <p>
     * Define a one-method agent interface with {@code @Agent}, build it
     * via {@link AgenticServices#agentBuilder(Class)}, and call it like
     * any AiService.
     */
    @Test
    void singleTypedAgent() {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        System.out.println("=== Single Typed Agent Test ===");
        String story = writer.writeStoryAbout("a robot who learns to garden");
        System.out.println("Story: " + story);
        System.out.println("=".repeat(50));

        assertNotNull(story, "Story should not be null");
        assertThat(story).as("Generated story").isNotBlank().hasSizeGreaterThan(50);
    }

    /**
     * Test 11.2: Sequence Workflow
     * <p>
     * Two agents in sequence: a writer produces a story, then an editor
     * adapts it for an audience. Each agent's output is keyed in the
     * shared {@code AgenticScope} so the next agent can reference it via
     * {@code @V}.
     */
    @Test
    void sequenceWorkflow() {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        AudienceEditor editor = AgenticServices.agentBuilder(AudienceEditor.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        UntypedAgent novelist = AgenticServices.sequenceBuilder()
                .subAgents(writer, editor)
                .outputKey("story")
                .build();

        Map<String, Object> input = Map.of(
                "topic", "a dragon learning to bake bread",
                "audience", "five-year-olds");

        System.out.println("=== Sequence Workflow Test ===");
        String result = (String) novelist.invoke(input);
        System.out.println("Final story: " + result);
        System.out.println("=".repeat(50));

        assertNotNull(result, "Result should not be null");
        assertThat(result).as("Edited story").isNotBlank().hasSizeGreaterThan(50);
    }

    /**
     * Test 11.3: Loop Workflow with Exit Condition
     * <p>
     * A scorer rates each draft on a 0.0–1.0 scale; if the score is below
     * the threshold, an editor revises the draft and the loop runs again.
     * The loop stops when either the score crosses 0.7 or
     * {@code maxIterations} is hit.
     */
    @Test
    void loopWorkflow() {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        StyleScorer scorer = AgenticServices.agentBuilder(StyleScorer.class)
                .chatModel(model)
                .outputKey("score")
                .build();

        StyleEditor styleEditor = AgenticServices.agentBuilder(StyleEditor.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        UntypedAgent reviewLoop = AgenticServices.loopBuilder()
                .subAgents(scorer, styleEditor)
                .maxIterations(3)
                .exitCondition(scope -> scope.readState("score", 0.0) >= 0.7)
                .build();

        UntypedAgent pipeline = AgenticServices.sequenceBuilder()
                .subAgents(writer, reviewLoop)
                .outputKey("story")
                .build();

        System.out.println("=== Loop Workflow Test ===");
        Object result = pipeline.invoke(Map.of("topic", "a lighthouse keeper's diary"));
        System.out.println("Final result: " + result);
        System.out.println("=".repeat(50));

        assertNotNull(result, "Result should not be null");
        assertThat(result.toString()).as("Final story after loop").isNotBlank();
    }

    // --- Agent interfaces ---

    public interface CreativeWriter {
        @SystemMessage("You are a creative short-story writer. Keep stories under 200 words.")
        @UserMessage("Write a short story about {{topic}}.")
        @Agent("Generate a short story on a given topic")
        String writeStoryAbout(@V("topic") String topic);
    }

    public interface AudienceEditor {
        @SystemMessage("You rewrite stories so they're appropriate for the requested audience.")
        @UserMessage("Rewrite this story for {{audience}}:\n\n{{story}}")
        @Agent("Adapt a story for a target audience")
        String editForAudience(@V("story") String story, @V("audience") String audience);
    }

    public interface StyleScorer {
        @SystemMessage("You are a literary critic. Reply with a single number from 0.0 to 1.0.")
        @UserMessage("Rate the literary quality of this story on a 0.0-1.0 scale:\n\n{{story}}")
        @Agent("Score the literary quality of a story")
        double scoreStory(@V("story") String story);
    }

    public interface StyleEditor {
        @SystemMessage("You polish prose to improve literary quality.")
        @UserMessage("Improve the prose of this story:\n\n{{story}}")
        @Agent("Improve the literary quality of a story")
        String polish(@V("story") String story);
    }
}
