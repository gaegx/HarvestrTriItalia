package com.gaegxh.harvester.service.search.Impl;

import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.service.search.SearchService;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    @Value("${ticket.api.base-url:https://www.lefrecce.it/Channels.Website.BFF.WEB/website}")
    private String baseUrl;

    @Override
    public String searchTickets(TicketSearchRequest request) {
        try {
            String response = Unirest.post(baseUrl + "/ticket/solutions")
                    .header("Content-Type", "application/json")
                    .body(request)
                    .asString()
                    .getBody();
            return response;
        } catch (UnirestException e) {
            throw new RuntimeException("Не удалось получить ответ от API /ticket/solutions", e);
        }
    }
}