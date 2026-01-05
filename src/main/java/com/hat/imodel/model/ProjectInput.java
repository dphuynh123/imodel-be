package com.hat.imodel.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProjectInput {
    public UUID projectId;
    public String prompt;
}
