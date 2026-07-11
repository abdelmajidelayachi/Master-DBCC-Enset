package dev.elayachi.mbank.ai;

import tools.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chatbot RAG (Retrieval Augmented Generation) :
 * 1. les documents de resources/rag/ sont decoupes en fragments et indexes
 *    avec des embeddings OpenAI (en memoire),
 * 2. pour chaque question, les fragments les plus proches (similarite cosinus)
 *    sont injectes dans le prompt du modele de chat.
 */
@Service
@Slf4j
public class RagChatService {

    private record Chunk(String text, double[] embedding) {
    }

    private final RestClient restClient;
    private final String apiKey;
    private final String chatModel;
    private final String embeddingModel;
    private final List<Chunk> chunks = new ArrayList<>();
    private volatile boolean indexed = false;

    public RagChatService(@Value("${ai.api.key}") String apiKey,
                          @Value("${ai.api.base-url}") String baseUrl,
                          @Value("${ai.chat.model}") String chatModel,
                          @Value("${ai.embedding.model}") String embeddingModel) {
        this.apiKey = apiKey;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String chat(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Le chatbot n'est pas configure : definissez GEMINI_API_KEY (ou secrets.properties) puis redemarrez le serveur.";
        }
        try {
            ensureIndexed();
            String context = retrieveContext(question);
            return complete(question, context);
        } catch (Exception e) {
            log.error("Erreur du chatbot", e);
            return "Desole, une erreur est survenue lors du traitement de votre question. Veuillez reessayer.";
        }
    }

    private synchronized void ensureIndexed() throws Exception {
        if (indexed) {
            return;
        }
        List<String> texts = new ArrayList<>();
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:rag/*.md");
        for (Resource resource : resources) {
            String content = resource.getContentAsString(StandardCharsets.UTF_8);
            texts.addAll(splitInChunks(content));
        }
        if (!texts.isEmpty()) {
            List<double[]> embeddings = embed(texts);
            for (int i = 0; i < texts.size(); i++) {
                chunks.add(new Chunk(texts.get(i), embeddings.get(i)));
            }
        }
        indexed = true;
        log.info("Index RAG construit : {} fragments", chunks.size());
    }

    private List<String> splitInChunks(String content) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String paragraph : content.split("\\n\\s*\\n")) {
            if (current.length() + paragraph.length() > 900 && !current.isEmpty()) {
                result.add(current.toString());
                current = new StringBuilder();
            }
            current.append(paragraph).append("\n\n");
        }
        if (!current.isEmpty()) {
            result.add(current.toString());
        }
        return result;
    }

    private String retrieveContext(String question) throws Exception {
        if (chunks.isEmpty()) {
            return "";
        }
        double[] questionEmbedding = embed(List.of(question)).getFirst();
        return chunks.stream()
                .sorted(Comparator.comparingDouble((Chunk c) -> cosineSimilarity(questionEmbedding, c.embedding()))
                        .reversed())
                .limit(4)
                .map(Chunk::text)
                .collect(Collectors.joining("\n---\n"));
    }

    private List<double[]> embed(List<String> texts) {
        JsonNode response = restClient.post()
                .uri("/embeddings")
                .body(Map.of("model", embeddingModel, "input", texts))
                .retrieve()
                .body(JsonNode.class);
        List<double[]> embeddings = new ArrayList<>();
        for (JsonNode item : response.get("data")) {
            JsonNode vector = item.get("embedding");
            double[] values = new double[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                values[i] = vector.get(i).asDouble();
            }
            embeddings.add(values);
        }
        return embeddings;
    }

    private String complete(String question, String context) {
        String systemPrompt = """
                Tu es l'assistant virtuel de la banque E-Bank.
                Reponds en francais, de facon concise et professionnelle.
                Utilise en priorite le CONTEXTE ci-dessous pour repondre.
                Si la reponse ne se trouve pas dans le contexte, dis-le poliment
                et invite le client a contacter son conseiller.

                CONTEXTE :
                %s
                """.formatted(context);
        JsonNode response = restClient.post()
                .uri("/chat/completions")
                .body(Map.of(
                        "model", chatModel,
                        "messages", List.of(
                                Map.of("role", "system", "content", systemPrompt),
                                Map.of("role", "user", "content", question))))
                .retrieve()
                .body(JsonNode.class);
        return response.get("choices").get(0).get("message").get("content").asText();
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
