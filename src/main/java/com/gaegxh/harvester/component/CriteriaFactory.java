package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Criteria;
import com.gaegxh.harvester.service.valid.InputReader;
import com.gaegxh.harvester.service.valid.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CriteriaFactory {
    private static final Logger logger = LoggerFactory.getLogger(CriteriaFactory.class);
    private final InputReader inputReader;
    private final InputValidator inputValidator;

    public CriteriaFactory(InputReader inputReader, InputValidator inputValidator) {
        this.inputReader = inputReader;
        this.inputValidator = inputValidator;
    }

    public Criteria createCriteria(int limit) {
        logger.info("Создание объекта Criteria с вводом пользователя");
        boolean frecceOnly = inputReader.readBoolean("Только поезда Frecce? (по умолчанию: нет)");
        boolean regionalOnly = inputReader.readBoolean("Только региональные поезда? (по умолчанию: нет)");
        boolean intercityOnly = inputReader.readBoolean("Только поезда Intercity? (по умолчанию: нет)");
        boolean tourismOnly = inputReader.readBoolean("Только туристические поезда? (по умолчанию: нет)");
        boolean noChanges = inputReader.readBoolean("Без пересадок? (по умолчанию: нет)");
        String order = inputValidator.validateOrder(
                inputReader.readString("Введите порядок сортировки (DEPARTURE_DATE, PRICE, DURATION, по умолчанию: DEPARTURE_DATE): ")
        );

        return Criteria.builder()
                .frecceOnly(frecceOnly)
                .regionalOnly(regionalOnly)
                .intercityOnly(intercityOnly)
                .tourismOnly(tourismOnly)
                .noChanges(noChanges)
                .order(order)
                .offset(0)
                .limit(limit)
                .build();
    }
}