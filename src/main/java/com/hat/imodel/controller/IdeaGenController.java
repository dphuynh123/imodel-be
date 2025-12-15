package com.hat.imodel.controller;

import com.hat.imodel.model.IdeaInput;
import com.hat.imodel.model.MindMapData;
import com.hat.imodel.service.GeminiIdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/idea-app")
public class IdeaGenController {

    private final GeminiIdeaService geminiIdeaService;

    @PostMapping("/gen")
    public MindMapData generateBusinessIdea(@RequestBody IdeaInput ideaInput) throws Exception {
        return geminiIdeaService.generateBusinessMindMap(ideaInput);
    }

}
