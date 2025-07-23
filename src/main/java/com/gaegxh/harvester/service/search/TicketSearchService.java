package com.gaegxh.harvester.service.search;

import com.gaegxh.harvester.model.TicketSearchRequest;

public interface TicketSearchService {
    String searchTickets(TicketSearchRequest request);
}