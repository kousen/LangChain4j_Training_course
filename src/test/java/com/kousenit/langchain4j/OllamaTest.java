package com.kousenit.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class OllamaTest {
    private final ChatModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("gpt-oss:20b")
            .build();

    record MyPerson(String first, String last, LocalDate dob) { }

    interface MyPersonExtractor {
        MyPerson extractPerson(String text);
    }

    @Test
    void extractPerson() {
        MyPersonExtractor extractor = AiServices.builder(MyPersonExtractor.class)
                .chatModel(model)
                .build();

        MyPerson person = extractor.extractPerson("""
                Jean-Luc, Captain of the Enterprise,
                was born in the Picard family
                in Labarre, France, on the 13th of Juillet
                in the year 2305. His older brother Robert
                teased hm as a child.
                """);
        System.out.println(person);
    }
}
