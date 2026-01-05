package com.hat.imodel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hat.imodel.model.IdeaInput;
import com.hat.imodel.model.MindMapData;
import com.hat.imodel.model.ProjectInput;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/gen")
    public MindMapData generateBusinessIdea(@RequestBody IdeaInput ideaInput) throws Exception {
        return geminiIdeaService.generateBusinessMindMap(ideaInput);
    }

    @PostMapping("/gen/task")
    public Object generateTestTask(@RequestBody ProjectInput ideaInput) throws Exception {

        return geminiIdeaService.generateProjectPlan(ideaInput);
    }


}
