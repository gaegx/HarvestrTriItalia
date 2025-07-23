package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvancedSearchRequest {
    private boolean bestFare;
    private boolean bikeFilter;
}
