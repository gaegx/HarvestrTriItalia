package com.gaegxh.harvester.service.parse;

import com.gaegxh.harvester.model.TicketSolution;

import java.util.List;

public interface ParseService {
    public List<TicketSolution> parseSolutions(String jsonResponse);
}
