package com.gaegxh.harvester.service.ticket.Impl;

import com.gaegxh.harvester.service.ticket.InputReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleInputReader implements InputReader {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleInputReader.class);
    private final Scanner scanner;

    public ConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        logger.info("Введена строка: {}", input);
        return input;
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    logger.info("Введено число: {}", value);
                    return value;
                }
                System.out.println("Значение должно быть между " + min + " и " + max);
            } catch (NumberFormatException e) {
                logger.warn("Неверный формат числа: {}", e.getMessage());
                System.out.println("Введите корректное число");
            }
        }
    }

    @Override
    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (да/нет): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("да") || input.equals("yes")) {
                logger.info("Введено: true для {}", prompt);
                return true;
            } else if (input.equals("нет") || input.equals("no")) {
                logger.info("Введено: false для {}", prompt);
                return false;
            }
            System.out.println("Введите 'да' или 'нет'");
        }
    }
}