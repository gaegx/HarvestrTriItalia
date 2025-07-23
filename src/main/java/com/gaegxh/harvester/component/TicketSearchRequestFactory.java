package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.AdvancedSearchRequest;
import com.gaegxh.harvester.model.Criteria;
import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.service.ticket.InputReader;
import com.gaegxh.harvester.service.ticket.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TicketSearchRequestFactory {
    private static final Logger logger = LoggerFactory.getLogger(TicketSearchRequestFactory.class);
    private static final int DEFAULT_LIMIT = 10;
    private final InputReader inputReader;
    private final InputValidator inputValidator;
    private final CriteriaFactory criteriaFactory;
    private final AdvancedSearchRequestFactory advancedSearchRequestFactory;

    public TicketSearchRequestFactory(
            InputReader inputReader,
            InputValidator inputValidator,
            CriteriaFactory criteriaFactory,
            AdvancedSearchRequestFactory advancedSearchRequestFactory) {
        this.inputReader = inputReader;
        this.inputValidator = inputValidator;
        this.criteriaFactory = criteriaFactory;
        this.advancedSearchRequestFactory = advancedSearchRequestFactory;
    }

    public TicketSearchRequest createInitialRequest(long departureStationId, long arrivalStationId) {
        logger.info("Создание начального запроса");
        String date = inputValidator.validateDepartureDate(
                inputReader.readString("Введите дату отправления (yyyy-MM-dd, например, 2025-07-25): ")
        );
        String time = inputValidator.validateDepartureTime(
                date,
                inputReader.readString("Введите время отправления (HH:mm, например, 15:00): ")
        );
        return createRequest(departureStationId, arrivalStationId, time, DEFAULT_LIMIT);
    }

    public TicketSearchRequest createRequestWithTime(long departureStationId, long arrivalStationId, String departureTime) {
        logger.info("Создание запроса с указанным временем отправления: {}", departureTime);
        return createRequest(departureStationId, arrivalStationId, departureTime, DEFAULT_LIMIT);
    }


    private TicketSearchRequest createRequest(long departureStationId, long arrivalStationId, String departureTime, int limit) {
        int adults = inputReader.readInt("Введите количество взрослых: ", 1, 10);
        int children = inputReader.readInt("Введите количество детей: ", 0, 10);
        Criteria criteria = criteriaFactory.createCriteria(limit);
        AdvancedSearchRequest advancedSearchRequest = advancedSearchRequestFactory.createAdvancedSearchRequest();

        return TicketSearchRequest.builder()
                .departureLocationId(departureStationId)
                .arrivalLocationId(arrivalStationId)
                .departureTime(departureTime)
                .adults(adults)
                .children(children)
                .criteria(criteria)
                .advancedSearchRequest(advancedSearchRequest)
                .build();
    }
}