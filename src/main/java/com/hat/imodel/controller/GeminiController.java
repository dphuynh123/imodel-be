package com.hat.imodel.controller;

import com.hat.imodel.service.GeminiImageService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiImageService geminiService;

    public GeminiController(GeminiImageService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/model-image")
    public String generateModelImage(@RequestHeader(value = "X-User-Email" , required = false) String userEmail , @RequestParam("file") MultipartFile file) throws Exception {
        return geminiService.generateModelImage(file, userEmail);
    }

    @PostMapping("/content")
    public String generateContent(@RequestHeader(value = "X-User-Email" , required = false) String userEmail , @RequestParam("file") MultipartFile file) throws Exception {
        return geminiService.generateContentBaseImage(file);
    }

    @PostMapping("/virtual-tryon")
    public String generateVirtualTryOn(
            @RequestParam("modelImageUrl") String modelImageUrl,
            @RequestParam("garment") MultipartFile garment
    ) throws Exception {
        return geminiService.generateVirtualTryOnImage(modelImageUrl, garment);
    }

    @PostMapping("/pose-variation")
    public String generatePoseVariation(
            @RequestParam("tryOnImageUrl") String tryOnImageUrl,
            @RequestParam("pose") String poseInstruction
    ) throws Exception {
        return geminiService.generatePoseVariation(tryOnImageUrl, poseInstruction);
    }




    @GetMapping("/ping")
    public String test() {
        return "pong";
    }
}