package com.gaegxh.harvester.service.ticket;

import com.gaegxh.harvester.model.TicketSolution;

import java.util.List;

public interface TicketService {
    public void saveAll(List<TicketSolution> tickets);
}
