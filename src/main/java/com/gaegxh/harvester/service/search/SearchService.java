package com.gaegxh.harvester.service.search;

import com.gaegxh.harvester.model.TicketSearchRequest;

public interface SearchService {
    String searchTickets(TicketSearchRequest request);
}