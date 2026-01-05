package com.hat.imodel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class BusinessIdea {
    public UUID id;
    public String title;
    public String description;

    public BusinessIdea() {
        this.id = UUID.randomUUID();
    }
}