package com.gaegxh.harvester.service.ticket;

public interface InputValidator {
    String validateDepartureDate(String input);
    String validateDepartureTime(String date, String time);
    String validateOrder(String input);
}