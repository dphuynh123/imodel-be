package com.hat.imodel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hat.imodel.model.IdeaInput;
import com.hat.imodel.model.MindMapData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiIdeaService {

    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("GEMINI_API_KEY environment variable is not set.");
        } else {
            log.info("GEMINI_API_KEY is set.");
        }
    }

    public MindMapData generateBusinessMindMap(IdeaInput ideaInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> config = new HashMap<>();
        config.put("responseMimeType", "application/json");
        config.put("responseSchema", ideaInput.getResponseSchema());
        config.put("temperature", 0.8);
        config.put("topP", 0.95);

        List<Map<String, Object>> contentsList = new ArrayList<>();
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", List.of(Map.of("text", ideaInput.getPrompt())));

        contentsList.add(contentMap);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("contents", contentsList);

        requestBody.put("generationConfig", config);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);


        // Build URI with API key if required
        String uri = UriComponentsBuilder.fromHttpUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent")
                .queryParam("key", apiKey)
                .toUriString();

        try {

            ResponseEntity<Map> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

             Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("Empty response body");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Object text = parts.get(0).get("text");
                        if (text != null) {
                            return objectMapper.readValue(text.toString(), MindMapData.class);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}