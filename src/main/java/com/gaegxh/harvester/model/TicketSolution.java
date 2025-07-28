package com.gaegxh.harvester.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "ticket")
public class TicketSolution {

    private static final String WRITE_SEPARATOR = "   ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_date", nullable = false)
    private String date;

    @Column(name = "departure_code", nullable = false)
    private String departure;

    @Column(name = "arrival_code", nullable = false)
    private String arrival;

    @Column(name = "coach_class")
    private String coachClassName;

    @Column(name = "train_brand")
    private String trainBrand;

    @Column(name = "train_class")
    private String trainClass;

    @Column(name = "train_number")
    private String trainNumber;

    @Column(name = "departure_time")
    private String departureTime;

    @Column(name = "duration")
    private String duration;

    @Column(name = "price")
    private String price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "fare_code")
    private String fare;

    @Column(name = "change_stations")
    private String changeStation;


    public TicketSolution(String date, String departure, String arrival) {
        this.date = date;
        this.departure = departure;
        this.arrival = arrival;
        this.coachClassName = "NULL";
        this.trainBrand = "NULL";
        this.trainClass = "NULL";
        this.trainNumber = "NULL";
        this.departureTime = "NULL";
        this.duration = "NULL";
        this.price = "NULL";
        this.currency = "NULL";
        this.fare = "NULL";
        this.changeStation = "NULL";
    }



    @Override
    public String toString() {
        if (trainBrand == null || trainBrand.isEmpty()) trainBrand = "NULL";
        if (trainClass == null || trainClass.isEmpty()) trainClass = "NULL";
        if (changeStation == null || changeStation.isEmpty()) changeStation = "NULL";

        return date + WRITE_SEPARATOR +
                departure + WRITE_SEPARATOR +
                arrival + WRITE_SEPARATOR +
                coachClassName + WRITE_SEPARATOR +
                trainBrand + WRITE_SEPARATOR +
                trainClass + WRITE_SEPARATOR +
                trainNumber + WRITE_SEPARATOR +
                departureTime + WRITE_SEPARATOR +
                duration + WRITE_SEPARATOR +
                price + WRITE_SEPARATOR +
                currency + WRITE_SEPARATOR +
                fare + WRITE_SEPARATOR +
                changeStation;
    }

    public TicketSolution(String date, String departure, String arrival,
                          String coachClassName, String trainBrand, String trainClass, String trainNumber,
                          String departureTime, String duration, String price, String currency,
                          String fare, String changeStation) {
        this.date = date;
        this.departure = departure;
        this.arrival = arrival;
        this.coachClassName = coachClassName;
        this.trainBrand = trainBrand;
        this.trainClass = trainClass;
        this.trainNumber = trainNumber;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
        this.currency = currency;
        this.fare = fare;
        this.changeStation = changeStation;
    }


}
