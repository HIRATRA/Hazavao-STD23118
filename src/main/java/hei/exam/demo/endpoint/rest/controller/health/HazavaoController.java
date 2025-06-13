package hei.exam.demo.endpoint.rest.controller.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.List;

@RestController
public class HazavaoController {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @GetMapping("/hazavao")
    public Mono<String> hazavao(@RequestParam String teny) {
        String prompt = "Hazavao amin'ny teny malagasy ny hevitra sy famaritana ny teny hoe \"" + teny + "\".";

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        )
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                            return (String) message.get("content");
                        } else {
                            return "Tsy afaka nanome famaritana.";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Error processing response from OpenAI.";
                    }
                })
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just("Error calling OpenAI API: " + e.getMessage());
                });
    }
}
