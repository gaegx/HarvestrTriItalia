package com.gaegxh.harvester.model;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class TicketSolution {

    private static final String WRITE_SEPARATOR = "   ";


    private Long id;


    private String date;


    private String departure;


    private String arrival;


    private String coachClassName;


    private String trainBrand;


    private String trainClass;


    private String trainNumber;


    private String departureTime;


    private String duration;


    private String price;


    private String currency;


    private String fare;


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
