package com.hat.imodel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPlan {
    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("estimated_total_duration")
    private String estimatedTotalDuration;

    @JsonProperty("development_phases")
    private List<DevelopmentPhase> developmentPhases;

    private UUID projectId;
}