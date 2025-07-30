package com.gaegxh.harvester.service.search.Impl;

import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.service.search.SearchService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${ticket.api.base-url:https://www.lefrecce.it/Channels.Website.BFF.WEB/website}")
    private String baseUrl;



    @Override
    public String searchTickets(TicketSearchRequest request) {
        try {

            HttpResponse<String> response = Unirest.post(baseUrl + "/ticket/solutions")
                    .header("Content-Type", "application/json")
                    .body(request)
                    .asString();

            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Не удалось получить ответ от API /ticket/solutions", e);
        }
    }
}
