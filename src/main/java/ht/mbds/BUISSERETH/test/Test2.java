package ht.mbds.BUISSERETH.test;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

public class Test2 {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_API_KEY");

        // 1. Configuration du modèle
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();
        // 2. Création du template de prompt
        // j\'utilise {{variable}} pour définir les zones dynamiques
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}"
        );

        // 3. Préparation des données (Variables)
        Map<String, Object> variables = new HashMap<>();
        variables.put("langue", "anglais");
        variables.put("texte", "Bonjour le monde, ici on utilise le framework LangChain4j qui facilite l'intégration de l'IA en Java.");

        // 4. Application des variables au template
        Prompt prompt = promptTemplate.apply(variables);

        // 5. Envoi au LLM et affichage
        System.out.println("Prompt envoyé : " + prompt.text());

        String response = model.chat(prompt.text());

        System.out.println("--- Résultat de la traduction ---");
        System.out.println(response);
    }
}