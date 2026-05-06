package ht.mbds.BUISSERETH.test;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import java.time.Duration;

public class Test3{
public static void main(String[] args) {
    String apiKey = System.getenv("GEMINI_API_KEY");

    // 1. Initialisation du modèle d'embeddings
    // On utilise text-embedding-004, optimisé pour transformer le texte en vecteurs
    EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
            .apiKey(apiKey)
            .modelName("embedding-001")
            .timeout(Duration.ofSeconds(100))
            .build();

   // https://generativelanguage.googleapis.com/v1beta/models?key=AIzaSyD9qkmDjs6IM8BRZkJNJKjXXBuTY20Bj0o

    // 2. Définition des phrases à comparer
    String phrase1 = "Le chat dort sur le tapis.";
    String phrase2 = "Un félin se repose sur la moquette.";
    String phrase3 = "La programmation en Java est passionnante.";

    // 3. Génération des vecteurs (Embeddings)
    Embedding vector1 = embeddingModel.embed(phrase1).content();
    Embedding vector2 = embeddingModel.embed(phrase2).content();
    Embedding vector3 = embeddingModel.embed(phrase3).content();

    // 4. Calcul de la similarité cosinus (score entre 0 et 1)
    double sim12 = CosineSimilarity.between(vector1, vector2);
    double sim13 = CosineSimilarity.between(vector1, vector3);

    // 5. Affichage des résultats
    System.out.println("Similarité (Chat vs Félin) : " + sim12);
    System.out.println("Similarité (Chat vs Java)  : " + sim13);
}
}
