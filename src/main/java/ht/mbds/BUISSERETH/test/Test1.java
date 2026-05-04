package ht.mbds.BUISSERETH.test;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {

    public static void main(String[] args) {

        ChatModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();

        String response = gemini.chat("Hello Gemini!");
        System.out.println(response);
    }
}