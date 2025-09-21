package com.si.mindhealth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

import com.si.mindhealth.configs.GeminiProperties;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class GeminiService {

  private final WebClient geminiWebClient;
  private final GeminiProperties props;

  public String generateText(String prompt) {

    Map<String, Object> body = Map.of(
        "contents", List.of(
            Map.of("parts", List.of(Map.of("text", prompt)))),

        "generationConfig", Map.of(
            "thinkingConfig", Map.of(
                "thinkingBudget", 0)));

    var uri = String.format("/v1beta/models/%s:generateContent?key=%s",
        props.model(), props.apiKey());

    var response = geminiWebClient.post()
        .uri(uri)
        .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResp -> clientResp.bodyToMono(String.class)
            .defaultIfEmpty("Unknown error")
            .flatMap(msg -> Mono.error(new RuntimeException("Gemini error: " + msg))))
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .block();

    if (response == null)
      return null;

    var candidates = (List<Map<String, Object>>) response.get("candidates");
    if (candidates == null || candidates.isEmpty())
      return null;

    var content = (Map<String, Object>) candidates.get(0).get("content");
    if (content == null)
      return null;

    var parts = (List<Map<String, Object>>) content.get("parts");
    if (parts == null || parts.isEmpty())
      return null;

    var sb = new StringBuilder();
    for (var p : parts) {
      var t = (String) p.get("text");
      if (t != null)
        sb.append(t);
    }
    return sb.toString();
  }
}
