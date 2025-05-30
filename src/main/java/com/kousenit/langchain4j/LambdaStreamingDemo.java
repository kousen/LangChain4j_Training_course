package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;

public class LambdaStreamingDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create OpenAI streaming chat model
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        // Create user message
        String userMessage = "Explain the benefits of using streaming in AI applications.";

        model.chat(userMessage, onPartialResponse(System.out::print));

        Thread.sleep(2500); // Wait for responses to complete
    }
}
