package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_5_NANO;

public class OpenAiDemo {
    public static void main(String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_5_NANO)
                .build();

        String response = model.chat("Why is the sky blue?");

        System.out.println("Simple Query Response:");
        System.out.println(response);
        System.out.println("=".repeat(50));
    }
}
