package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.service.valid.InputReader;
import com.gaegxh.harvester.service.valid.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TicketSearchRequestFactory {
    private static final Logger logger = LoggerFactory.getLogger(TicketSearchRequestFactory.class);
    private static final int DEFAULT_LIMIT = 10;





    public TicketSearchRequest createInitialRequest(long departureStationId, long arrivalStationId,String date) {
        logger.info("Создание начального запроса");

        return createRequest(departureStationId, arrivalStationId, date, DEFAULT_LIMIT);
    }

    public TicketSearchRequest createRequestWithTime(long departureStationId, long arrivalStationId, String departureTime) {
        logger.info("Создание запроса с указанным временем отправления: {}", departureTime);
        return createRequest(departureStationId, arrivalStationId, departureTime, DEFAULT_LIMIT);
    }


    private TicketSearchRequest createRequest(long departureStationId, long arrivalStationId, String departureTime, int limit) {
        int adults =1;
        int children = 0;

        return TicketSearchRequest.builder()
                .departureLocationId(departureStationId)
                .arrivalLocationId(arrivalStationId)
                .departureTime(departureTime)
                .adults(adults)
                .children(children)
                .build();
    }
}