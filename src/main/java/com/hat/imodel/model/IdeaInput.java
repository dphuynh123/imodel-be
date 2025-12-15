package com.hat.imodel.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class IdeaInput {

    public String prompt;
    public Map<String, Object> responseSchema;
}
