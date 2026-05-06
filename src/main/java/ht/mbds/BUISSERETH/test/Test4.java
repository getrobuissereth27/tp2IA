package ht.mbds.BUISSERETH.test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test4 {

    /**
     * Interface de service IA.
     * LangChain4j va générer l'implémentation automatiquement.
     */
    interface Assistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        // 1. Récupération de la clé API et du chemin du fichier
        String apiKey = System.getenv("GEMINI_API_KEY");
        Path path = Paths.get("infos.txt");

        // 2. Chargement et découpage du document
        // On charge le fichier texte et on le découpe en segments de 300 caractères
        // Remplace la ligne de chargement par celle-ci
        Document document = FileSystemDocumentLoader.loadDocument(path, new TextDocumentParser());
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        // 3. Configuration des modèles
        // Gemini pour la génération de texte (Chat)
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();

        // BGE pour la recherche sémantique (Local)
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        // 4. Stockage des vecteurs en mémoire
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // On transforme les segments en vecteurs et on les stocke
        System.out.println("Ingestion du document en cours...");
        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        // 5. Configuration du Retriever (le moteur de recherche du RAG)
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .build();

        // 6. Création de l'Assistant avec RAG et Mémoire
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // 7. Test final
        System.out.println("--- Démarrage de la session RAG ---");
        String question = "Comment s'appelle le chat de Pierre ?";
        System.out.println("Question : " + question);

        String reponse = assistant.chat(question);

        System.out.println("\nRéponse de l'IA (basée sur infos.txt) :");
        System.out.println(reponse);
    }
}