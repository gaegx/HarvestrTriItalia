package com.gaegxh.harvester.service.valid.Impl;

import com.gaegxh.harvester.service.valid.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class ConsoleInputValidator implements InputValidator {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleInputValidator.class);
    private static final String DEFAULT_OFFSET = "+02:00";

    @Override
    public String validateDepartureDate(String input) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                LocalDate.parse(input, dateFormatter);
                logger.info("Валидная дата отправления: {}", input);
                return input;
            } catch (DateTimeParseException e) {
                logger.warn("Неверный формат даты: {}. Ожидается yyyy-MM-dd, например, 2025-07-25", input);
                System.out.println("Неверный формат даты. Введите дату в формате yyyy-MM-dd (например, 2025-07-25): ");
                input = System.console() != null ? System.console().readLine() : new Scanner(System.in).nextLine().trim();
            }
        }
    }

    @Override
    public String validateDepartureTime(String date, String time) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            try {
                LocalTime.parse(time, timeFormatter);
                OffsetDateTime departureDateTime = OffsetDateTime.parse(
                        date + "T" + time + ":00.000" + DEFAULT_OFFSET,
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                );
                String result = departureDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                logger.info("Валидное время отправления: {} преобразовано в {}", time, result);
                return result;
            } catch (DateTimeParseException e) {
                logger.warn("Неверный формат времени: {}. Ожидается HH:mm, например, 15:00", time);
                System.out.println("Неверный формат времени. Введите время в формате HH:mm (например, 15:00): ");
                time = System.console() != null ? System.console().readLine() : new Scanner(System.in).nextLine().trim();
            }
        }
    }

    @Override
    public String validateOrder(String input) {
        String upperInput = input.trim().toUpperCase();
        while (true) {
            if (upperInput.equals("DEPARTURE_DATE") || upperInput.equals("PRICE") || upperInput.equals("DURATION")) {
                logger.info("Валидный порядок сортировки: {}", upperInput);
                return upperInput;
            }
            logger.warn("Неверный порядок сортировки: {}. Ожидается DEPARTURE_DATE, PRICE или DURATION", input);
            System.out.println("Неверный порядок. Введите один из: DEPARTURE_DATE, PRICE, DURATION: ");
            upperInput = System.console() != null ? System.console().readLine() : new Scanner(System.in).nextLine().trim().toUpperCase();
        }
    }
}