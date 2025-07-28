package com.gaegxh.harvester.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task") // Название таблицы в БД (можно изменить при необходимости)
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
}
