package dev.elayachi.mbank.web;

import dev.elayachi.mbank.ai.RagChatService;
import dev.elayachi.mbank.dtos.Requests.ChatRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class ChatRestController {

    private final RagChatService ragChatService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        return Map.of("response", ragChatService.chat(request.message()));
    }
}
