package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Criteria {
    private boolean frecceOnly;
    private boolean regionalOnly;
    private boolean intercityOnly;
    private boolean tourismOnly;
    private boolean noChanges;
    private String order;
    private int offset;
    private int limit;
}
