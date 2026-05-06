package ht.mbds.BUISSERETH.test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Test5 {

    // L'interface de notre Assistant
    interface Assistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        // Assure-toi que ton fichier PDF est bien à la racine du projet
        Path path = Paths.get("ml.pdf");

        System.out.println("--- Chargement et indexation du PDF ---");

        // 1. Chargement et découpage du PDF
        Document document = FileSystemDocumentLoader.loadDocument(path, new ApachePdfBoxDocumentParser());
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
        List<TextSegment> segments = splitter.split(document);

        // 2. Modèles (Gemini + BGE local)
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();

        EmbeddingModel embeddingModel = new BgeSmallEnV15EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 3. Ingestion des segments dans la mémoire vectorielle
        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        // 4. Configuration du moteur de recherche (Retriever)
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        // 5. Création de l'Assistant avec RAG et mémoire de conversation
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(15))
                .build();

        // 6. Lancement de la conversation interactive
        conversationAvec(assistant);
    }

    /**
     * Méthode pour gérer la boucle de discussion avec l'utilisateur.
     */
    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question (ou tapez 'fin' pour quitter) : ");
                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                System.out.println("==================================================");

                if ("fin".equalsIgnoreCase(question)) {
                    System.out.println("Fin de la session. Au revoir !");
                    break;
                }

                // L'IA cherche dans le PDF et répond
                String reponse = assistant.chat(question);

                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}