package com.hat.imodel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevelopmentPhase {
    private String phase;
    private String duration;
    private List<ProjectTask> tasks;
}
