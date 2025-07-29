package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Criteria;
import com.gaegxh.harvester.service.ticket.InputReader;
import com.gaegxh.harvester.service.ticket.InputValidator;
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
        String order = inputValidator.validateOrder(
                inputReader.readString("Введите порядок сортировки (DEPARTURE_DATE, PRICE, DURATION, по умолчанию: DEPARTURE_DATE): ")
        );

        return Criteria.builder()
                .order(order)
                .offset(0)
                .limit(limit)
                .build();
    }
}