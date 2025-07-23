package com.gaegxh.harvester.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @SerializedName("task_uuid")
    private String taskUuid;

    @SerializedName("crawler_type")
    private String crawlerType;

    @SerializedName("departure_station")
    private String departureStation;

    @SerializedName("arrival_station")
    private String arrivalStation;

    @SerializedName("coach_classes")
    private String coachClasses;

    @SerializedName("train_brands")
    private String trainBrands;

    @SerializedName("fare_codes")
    private String fareCodes;

    @SerializedName("departure_date")
    private String departureDate;

    @SerializedName("reverse")
    private int reverse;

    @SerializedName("maximum_changes")
    private int changes;

    @SerializedName("dep_station_uuid")
    private String departureStationUuid;

    @SerializedName("arr_station_uuid")
    private String arrivalStationUuid;
}
