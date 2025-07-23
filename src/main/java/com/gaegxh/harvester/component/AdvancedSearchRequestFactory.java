package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.AdvancedSearchRequest;
import com.gaegxh.harvester.service.ticket.InputReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdvancedSearchRequestFactory {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedSearchRequestFactory.class);
    private final InputReader inputReader;

    public AdvancedSearchRequestFactory(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    public AdvancedSearchRequest createAdvancedSearchRequest() {
        logger.info("Создание объекта AdvancedSearchRequest с вводом пользователя");
        boolean bestFare = inputReader.readBoolean("Искать лучшие тарифы? (по умолчанию: нет)");
        boolean bikeFilter = inputReader.readBoolean("Фильтр для велосипедов? (по умолчанию: нет)");

        return AdvancedSearchRequest.builder()
                .bestFare(bestFare)
                .bikeFilter(bikeFilter)
                .build();
    }
}