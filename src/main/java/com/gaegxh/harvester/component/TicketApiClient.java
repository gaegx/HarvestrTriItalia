package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.service.search.Impl.SearchServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TicketApiClient {
    private static final Logger logger = LoggerFactory.getLogger(TicketApiClient.class);
    private final SearchServiceImpl ticketSearchService;

    public TicketApiClient(SearchServiceImpl ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    public String fetchSolutions(TicketSearchRequest request) {
        try {
            String response = ticketSearchService.searchTickets(request);
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при выполнении запроса к API /ticket/solutions: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить ответ от API /ticket/solutions", e);
        }
    }
}