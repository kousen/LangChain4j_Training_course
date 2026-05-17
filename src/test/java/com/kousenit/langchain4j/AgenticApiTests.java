package com.kousenit.langchain4j;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Lab 11: Agentic API
 *
 * <p>The {@code langchain4j-agentic} module lets you compose multiple LLM-backed agents into
 * workflows. LangChain4j 1.15 adds reusable agentic patterns, including the voting pattern in
 * {@code langchain4j-agentic-patterns}. In this starter branch, each test outlines the
 * implementation steps; the completed versions are on the {@code solutions} branch.
 */
class AgenticApiTests {

    /**
     * Test 11.1: Single Typed Agent
     *
     * <p>TODO: Define a one-method agent interface annotated with {@code @Agent}, build it with
     * {@code AgenticServices.agentBuilder(...)}, and call it like a normal typed service.
     */
    @Test
    void singleTypedAgent() {
        // TODO: Create a ChatModel.

        // TODO: Define/build a CreativeWriter agent with outputKey("story").

        // TODO: Invoke the typed agent and assert the story is non-blank.

        fail("TODO: Implement single typed agent test");
    }

    /**
     * Test 11.2: Sequence Workflow
     *
     * <p>TODO: Compose a writer agent and an audience editor agent with {@code
     * AgenticServices.sequenceBuilder()}.
     */
    @Test
    void sequenceWorkflow() {
        // TODO: Build writer and editor sub-agents.

        // TODO: Use sequenceBuilder().subAgents(writer, editor).outputKey("story").build().

        // TODO: Invoke with a Map containing topic and audience.

        fail("TODO: Implement sequence workflow test");
    }

    /**
     * Test 11.3: Loop Workflow with Exit Condition
     *
     * <p>TODO: Compose a writer, scorer, and editor so the loop improves a story until the score is
     * high enough or max iterations is reached.
     */
    @Test
    void loopWorkflow() {
        // TODO: Build writer, scorer, and style editor agents.

        // TODO: Create loopBuilder().subAgents(scorer, styleEditor).maxIterations(3).

        // TODO: Add an exit condition based on scope.readState("score", 0.0) >= 0.7.

        // TODO: Put the writer and loop into a sequence and assert the final story is non-blank.

        fail("TODO: Implement loop workflow test");
    }

    /**
     * Test 11.4: Voting Pattern
     *
     * <p>TODO: Use {@code AgenticServices.plannerBuilder(SentimentVoter.class)} with three
     * sentiment-classifier sub-agents and {@code .planner(VotingPlanner::new)}. The no-arg
     * {@code VotingPlanner} uses majority vote, which is the default classification strategy.
     */
    @Test
    void votingPattern() {
        // TODO: Build three ChatModel instances with different temperatures for diversity.

        // TODO: Build three SentimentClassifier agents with output keys vote1, vote2, and vote3.

        // TODO: Build a SentimentVoter with plannerBuilder(...).planner(VotingPlanner::new).

        // TODO: Classify a clearly positive sentence and assert the majority result is POSITIVE.

        fail("TODO: Implement voting pattern test");
    }

    /*
     * TODO: Add the public inner agent interfaces used by the tests:
     *
     * - CreativeWriter
     * - AudienceEditor
     * - StyleScorer
     * - StyleEditor
     * - SentimentClassifier
     * - SentimentVoter
     *
     * The interfaces must be public because AgentInvoker reflectively invokes
     * them from a different package.
     */
}
