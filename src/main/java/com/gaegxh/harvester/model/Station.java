package com.gaegxh.harvester.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Station {
    private long id;
    private String name;
    private String displayName;
    private boolean multistation;

    @Override
    public String toString() {
        return displayName + " (" + (multistation ? "город" : "станция") + ")";
    }


}
