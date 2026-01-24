package com.kousenit.langchain4j;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.junit.jupiter.api.Test;

/**
 * Lab 6.6: Multi-Agent Systems (AgenticServices)
 *
 * <p>NEW in LangChain4j 1.10.0: The langchain4j-agentic module provides a powerful framework for
 * orchestrating multi-agent AI workflows. This lab introduces the core concepts:
 *
 * <ul>
 *   <li>{@code @Agent} annotation - Marks methods as agent entry points
 *   <li>{@code AgenticServices} - Builder factory for creating agents and workflows
 *   <li>{@code UntypedAgent} - Generic agent that accepts Map inputs
 * </ul>
 *
 * <p>This lab covers basic patterns. For advanced orchestration (parallel, loops, conditionals,
 * supervisors), see the official tutorial: <a
 * href="https://docs.langchain4j.dev/tutorials/agents/">LangChain4j Agents Tutorial</a>
 */
class MultiAgentTests {

  // ============================================================
  // Agent Interface Definitions
  // ============================================================

  /**
   * A creative writer agent that generates stories based on topics. The @Agent annotation marks
   * this method as an agent entry point with a description that helps other agents understand its
   * purpose.
   */
  interface CreativeWriter {
    @Agent("Generates creative stories based on the given topic")
    @UserMessage(
        """
            You are a creative writer with a vivid imagination.
            Write a short, engaging story (2-3 paragraphs) about: {{topic}}
            Make it creative and captivating.
            """)
    String generateStory(@V("topic") String topic);
  }

  /**
   * An editor agent that adapts content for specific audiences. This agent takes existing content
   * and rewrites it for a target audience.
   */
  interface AudienceEditor {
    @Agent("Adapts stories for specific target audiences")
    @UserMessage(
        """
            You are an expert editor who adapts content for different audiences.
            Take this story and rewrite it for {{audience}}:

            Original story:
            {{story}}

            Keep the core narrative but adjust vocabulary, tone, and complexity
            to be appropriate for the target audience.
            """)
    String editForAudience(@V("story") String story, @V("audience") String audience);
  }

  // ============================================================
  // Test Methods
  // ============================================================

  /**
   * Test 6.6.1: Create and Use a Basic Agent
   *
   * <p>Demonstrates creating a single agent using AgenticServices.agentBuilder(). The @Agent
   * annotation marks the method as an agent entry point.
   *
   * <p>Key concepts:
   *
   * <ul>
   *   <li>Interface-based agent definition with @Agent annotation
   *   <li>AgenticServices.agentBuilder() for agent creation
   *   <li>outputKey() to specify where results are stored in AgenticScope
   * </ul>
   */
  @Test
  void basicAgentWithAnnotation() {
    // TODO: Create a ChatModel for the agent
    // ChatModel model = OpenAiChatModel.builder()
    //         .apiKey(System.getenv("OPENAI_API_KEY"))
    //         .modelName(GPT_4_1_NANO)
    //         .build();

    // TODO: Build the CreativeWriter agent using AgenticServices
    // CreativeWriter writer = AgenticServices
    //         .agentBuilder(CreativeWriter.class)
    //         .chatModel(model)
    //         .outputKey("story")
    //         .build();

    // TODO: Invoke the agent
    // String story = writer.generateStory("a robot learning to paint");

    // TODO: Print and verify the result
    // System.out.println("=== Basic Agent Output ===");
    // System.out.println(story);
    //
    // assertNotNull(story, "Story should not be null");
    // assertFalse(story.trim().isEmpty(), "Story should not be empty");
  }

  /**
   * Test 6.6.2: Create a Sequential Agent Workflow
   *
   * <p>Demonstrates chaining multiple agents in a sequential workflow. The output of one agent
   * becomes input for the next.
   *
   * <p>Workflow: CreativeWriter → AudienceEditor
   *
   * <p>Key concepts:
   *
   * <ul>
   *   <li>AgenticServices.sequenceBuilder() for workflow creation
   *   <li>subAgents() to define the execution order
   *   <li>UntypedAgent for flexible, map-based invocation
   *   <li>Shared state via outputKey between agents
   * </ul>
   */
  @Test
  void sequentialAgentWorkflow() {
    // TODO: Create a ChatModel for the agents
    // ChatModel model = OpenAiChatModel.builder()
    //         .apiKey(System.getenv("OPENAI_API_KEY"))
    //         .modelName(GPT_4_1_NANO)
    //         .build();

    // TODO: Build the CreativeWriter agent
    // CreativeWriter writer = AgenticServices
    //         .agentBuilder(CreativeWriter.class)
    //         .chatModel(model)
    //         .outputKey("story")  // Output stored as "story"
    //         .build();

    // TODO: Build the AudienceEditor agent
    // AudienceEditor editor = AgenticServices
    //         .agentBuilder(AudienceEditor.class)
    //         .chatModel(model)
    //         .outputKey("story")  // Also outputs to "story" (overwrites)
    //         .build();

    // TODO: Create a sequential workflow combining both agents
    // UntypedAgent pipeline = AgenticServices
    //         .sequenceBuilder()
    //         .subAgents(writer, editor)
    //         .outputKey("story")
    //         .build();

    // TODO: Invoke the workflow with initial inputs
    // Map<String, Object> inputs = Map.of(
    //         "topic", "a space explorer discovering a new planet",
    //         "audience", "children aged 6-8"
    // );
    //
    // String finalStory = (String) pipeline.invoke(inputs);

    // TODO: Print and verify the result
    // System.out.println("=== Sequential Workflow Output ===");
    // System.out.println("Topic: " + inputs.get("topic"));
    // System.out.println("Target Audience: " + inputs.get("audience"));
    // System.out.println("\n--- Final Story (edited for audience) ---");
    // System.out.println(finalStory);
    //
    // assertNotNull(finalStory, "Final story should not be null");
    // assertFalse(finalStory.trim().isEmpty(), "Final story should not be empty");
  }
}
