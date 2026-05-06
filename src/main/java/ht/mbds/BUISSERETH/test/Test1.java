package ht.mbds.BUISSERETH.test;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {

    public static void main(String[] args) {

        // 1. Configuration du modèle
        ChatModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();
        //creation du chat
        String response = gemini.chat("Hello Gemini!");
        System.out.println(response);
        String response2 = gemini.chat("quel est le president de la france?");
        System.out.println(response2);
    }
}