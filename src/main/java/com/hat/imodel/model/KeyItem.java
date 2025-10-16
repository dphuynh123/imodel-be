package com.hat.imodel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyItem {
    private String key;
    private int countDown;
}
