package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Criteria {
    private String order;
    private int offset;
    private int limit;
}