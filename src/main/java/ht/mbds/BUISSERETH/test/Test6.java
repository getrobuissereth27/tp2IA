package ht.mbds.BUISSERETH.test;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import java.util.Scanner;

interface AssistantMeteo {
    String chat(String message);
}

public class Test6 {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_API_KEY");

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .logRequestsAndResponses(true)
                .build();

        // 2. Création de l'assistant
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .tools(new MeteoTool())
                .build();

        // 3. Boucle de conversation
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Assistant Météo (Test 6) ===");
            while (true) {
                System.out.println("\n==================================================");
                System.out.println("Posez votre question (ou 'fin') : ");
                String question = scanner.nextLine();

                if (question.isBlank()) continue;
                if ("fin".equalsIgnoreCase(question)) break;

                System.out.println("==================================================");
                String reponse = assistant.chat(question);
                System.out.println("\nAssistant : " + reponse);
            }
        }
    }
}