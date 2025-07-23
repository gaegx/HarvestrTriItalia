package com.gaegxh.harvester.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TicketSolution {
    private static final String WRITE_SEPARATOR = "   ";

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

    public TicketSolution(TicketSolution ticket) {
        this.date = ticket.getDate();
        this.departure = ticket.getDeparture();
        this.arrival = ticket.getArrival();
        this.coachClassName = ticket.getCoachClassName();
        this.trainBrand = ticket.getTrainBrand();
        this.trainClass = ticket.getTrainClass();
        this.trainNumber = ticket.getTrainNumber();
        this.departureTime = ticket.getDepartureTime();
        this.duration = ticket.getDuration();
        this.price = ticket.getPrice();
        this.currency = ticket.getCurrency();
        this.fare = ticket.getFare();
        this.changeStation = ticket.getChangeStation();
    }

    @Override
    public String toString() {
        if (trainBrand == null || trainBrand.equals("")){
            trainBrand = "NULL";
        }
        if (trainClass == null || trainClass.equals("")) {
            trainClass = "NULL";
        }
        if(changeStation == null || changeStation.equals("")) {
            changeStation = "NULL";
        }
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

    public static String getEmptyTicket(String dateToWrite, String departure, String arrival) {
        return dateToWrite + WRITE_SEPARATOR +
                departure + WRITE_SEPARATOR +
                arrival + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR +
                "NULL" + WRITE_SEPARATOR;
    }

}
