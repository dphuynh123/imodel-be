package com.hat.imodel.service;

import com.hat.imodel.model.KeyItem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import java.util.Base64;

@Service
@Slf4j
public class GeminiImageService {

    private String apiKey;

    private static final String MODEL = "gemini-2.5-flash-image";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final RestTemplate restTemplate = new RestTemplate();

    private List<KeyItem> keyItem = new ArrayList<>(List.of(
            KeyItem.builder().key("ASDAOWIJDIADA").countDown(5).build(),
            KeyItem.builder().key("QWEIQUWOEUOAWD").countDown(5).build(),
            KeyItem.builder().key("ZXCNQOWDPQQWEA").countDown(5).build(),
            KeyItem.builder().key("VKMLLWAMDASD3A").countDown(5).build(),
            KeyItem.builder().key("VKJKAOWJEIO21OL").countDown(5).build(),
            KeyItem.builder().key("1OIJOLJIASDKMCL").countDown(5).build()
    ));

    @PostConstruct
    public void init() {
        apiKey = Optional.ofNullable(System.getProperty("GEMINI_API_KEY")).orElse("GEMINI_API_KEY");
        log.info("GEMINI_API_KEY is {}",apiKey);
    }

    // === Utility: Convert file to inlineData ===
    private Map<String, Object> fileToInlineData(MultipartFile file) throws IOException {
        String mimeType = file.getContentType();
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", base64);
        Map<String, Object> part = new HashMap<>();
        part.put("inlineData", inlineData);
        return part;
    }

    private Map<String, Object> dataUrlToPart(String dataUrl) {
        String[] arr = dataUrl.split(",");
        if (arr.length < 2) throw new RuntimeException("Invalid data URL");
        String mimeType = arr[0].replace("data:", "").split(";")[0];
        String data = arr[1];
        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", data);
        Map<String, Object> part = new HashMap<>();
        part.put("inlineData", inlineData);
        return part;
    }

    private String handleApiResponse(Map<String, Object> response) {
        // Handle blocking
        if (response.containsKey("promptFeedback")) {
            Map<String, Object> promptFeedback = (Map<String, Object>) response.get("promptFeedback");
            if (promptFeedback.containsKey("blockReason")) {
                throw new RuntimeException("Request was blocked: " + promptFeedback.get("blockReason"));
            }
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No candidates returned from model.");
        }

        for (Map<String, Object> candidate : candidates) {
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null) continue;
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null) continue;
            for (Map<String, Object> part : parts) {
                if (part.containsKey("inlineData")) {
                    Map<String, String> inlineData = (Map<String, String>) part.get("inlineData");
                    String mimeType = inlineData.get("mimeType");
                    String data = inlineData.get("data");
                    return "data:" + mimeType + ";base64," + data;
                }
            }
        }

        throw new RuntimeException("The AI model did not return an image. Possibly blocked or filtered.");
    }

    private String sendRequest(List<Map<String, Object>> parts) {
        String url = ENDPOINT + MODEL + ":generateContent?key=" + apiKey;

        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", parts);

        Map<String, Object> body = new HashMap<>();
        body.put("contents", contents);
        body.put("generationConfig", Map.of("responseModalities", List.of("IMAGE", "TEXT")));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return handleApiResponse(response.getBody());
    }

    // === Generate Fashion Model Image ===
    public String generateModelImage(MultipartFile userImage , String apiKey) throws IOException {
        Assert.notNull(apiKey,"apiKey cannot be null");

        KeyItem key = keyItem.stream().filter(z -> z.getKey().equals(apiKey)).findFirst().orElseThrow();
        Assert.isTrue(key.getCountDown() > 0 , "Out of limit token");

        Map<String, Object> userImagePart = fileToInlineData(userImage);
        String prompt = """
            You are an expert fashion photographer AI. Transform the person in this image into a full-body fashion model photo suitable for an e-commerce website.
            The background must be a clean, neutral studio backdrop (light gray, #f0f0f0). 
            The person should have a neutral, professional model expression. 
            Preserve the person's identity, unique features, and body type, but place them in a standard, relaxed standing model pose.
            The final image must be photorealistic. Return ONLY the final image.
        """;

        Map<String, Object> textPart = Map.of("text", prompt);

        int index = keyItem.indexOf(key);
        key.setCountDown(key.getCountDown() - 1);
        keyItem.set(index , key);

        return sendRequest(List.of(userImagePart, textPart));
    }

    // === Generate Virtual Try-On ===
    public String generateVirtualTryOnImage(String modelImageUrl, MultipartFile garmentImage) throws IOException {
        Map<String, Object> modelImagePart = dataUrlToPart(modelImageUrl);
        Map<String, Object> garmentImagePart = fileToInlineData(garmentImage);

        String prompt = """
            You are an expert virtual try-on AI. You will be given a 'model image' and a 'garment image'. 
            Create a photorealistic image where the person from the 'model image' is wearing the clothing from the 'garment image'.

            Rules:
            1. Completely replace the original clothing with the new garment.
            2. Preserve the model’s face, hair, body shape, and pose.
            3. Preserve the original background perfectly.
            4. Fit the new garment naturally with lighting and shadows.
            5. Return ONLY the final image.
        """;

        Map<String, Object> textPart = Map.of("text", prompt);
        return sendRequest(List.of(modelImagePart, garmentImagePart, textPart));
    }

    // === Pose Variation ===
    public String generatePoseVariation(String tryOnImageUrl, String poseInstruction) {
        Map<String, Object> tryOnImagePart = dataUrlToPart(tryOnImageUrl);
        String prompt = "Regenerate this image from a different perspective: \"" + poseInstruction + "\". " +
                "Keep the person, clothing, and background identical. Return ONLY the final image.";

        Map<String, Object> textPart = Map.of("text", prompt);
        return sendRequest(List.of(tryOnImagePart, textPart));
    }
}

