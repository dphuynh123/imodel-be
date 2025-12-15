package com.hat.imodel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    private String problemTitle;
    private List<BusinessIdea> businessIdeas;
}