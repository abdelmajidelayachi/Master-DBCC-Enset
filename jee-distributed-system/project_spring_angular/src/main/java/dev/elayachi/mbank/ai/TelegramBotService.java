package dev.elayachi.mbank.ai;

import tools.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Client Telegram du chatbot : long polling sur l'API HTTP de Telegram
 * (getUpdates / sendMessage), chaque message recu est traite par le service RAG.
 * Desactive si telegram.bot.token est vide.
 */
@Service
@Slf4j
public class TelegramBotService {

    private final RagChatService ragChatService;
    private final String token;
    private final RestClient restClient;
    private volatile boolean running = false;

    public TelegramBotService(RagChatService ragChatService,
                              @Value("${telegram.bot.token}") String token) {
        this.ragChatService = ragChatService;
        this.token = token;
        this.restClient = RestClient.builder().baseUrl("https://api.telegram.org").build();
    }

    @PostConstruct
    public void start() {
        if (token == null || token.isBlank()) {
            log.info("Bot Telegram desactive (TELEGRAM_BOT_TOKEN non defini)");
            return;
        }
        running = true;
        Thread pollingThread = new Thread(this::pollLoop, "telegram-bot");
        pollingThread.setDaemon(true);
        pollingThread.start();
        log.info("Bot Telegram demarre");
    }

    @PreDestroy
    public void stop() {
        running = false;
    }

    private void pollLoop() {
        long offset = 0;
        while (running) {
            try {
                JsonNode response = restClient.get()
                        .uri("/bot{token}/getUpdates?timeout=50&offset={offset}", token, offset)
                        .retrieve()
                        .body(JsonNode.class);
                for (JsonNode update : response.get("result")) {
                    offset = update.get("update_id").asLong() + 1;
                    JsonNode message = update.get("message");
                    if (message != null && message.has("text")) {
                        String chatId = message.get("chat").get("id").asText();
                        String answer = ragChatService.chat(message.get("text").asText());
                        sendMessage(chatId, answer);
                    }
                }
            } catch (Exception e) {
                log.warn("Erreur du bot Telegram, nouvelle tentative dans 5s : {}", e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void sendMessage(String chatId, String text) {
        restClient.post()
                .uri("/bot{token}/sendMessage", token)
                .body(Map.of("chat_id", chatId, "text", text))
                .retrieve()
                .toBodilessEntity();
    }
}
