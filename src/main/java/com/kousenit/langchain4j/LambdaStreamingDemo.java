package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

        // Create a CountDownLatch to wait for completion
        CountDownLatch latch = new CountDownLatch(1);

        // Use a StreamingChatResponseHandler to handle the response
        model.chat(userMessage, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                System.out.print(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("\nStreaming completed with reason: " + response.finishReason());
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("Error occurred: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait for the streaming to complete
        latch.await(30, TimeUnit.SECONDS);
    }
}
