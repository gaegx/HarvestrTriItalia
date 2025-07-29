package com.gaegxh.harvester.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Task {

    @Id
    @Column(name = "task_uuid", nullable = false, unique = true)
    private String taskUuid;

    @Column(name = "crawler_type")
    private String crawlerType;

    @Column(name = "departure_station")
    private String departureStation;

    @Column(name = "arrival_station")
    private String arrivalStation;

    @Column(name = "coach_classes")
    private String coachClasses;

    @Column(name = "train_brands")
    private String trainBrands;

    @Column(name = "fare_codes")
    private String fareCodes;

    @Column(name = "departure_date")
    private String departureDate;

    @Column(name = "reverse")
    private int reverse;

    @Column(name = "maximum_changes")
    private int changes;

    @Column(name = "dep_station_uuid")
    private String departureStationUuid;

    @Column(name = "arr_station_uuid")
    private String arrivalStationUuid;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private int priority;

    @Column(name = "result")
    private String result;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "retry_count")
    private int retryCount;
}
