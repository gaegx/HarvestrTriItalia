package com.gaegxh.harvester.service.filter;

import com.gaegxh.harvester.model.TicketSolution;

public interface SolutionFilter {
    boolean filter(TicketSolution solutions);
}
