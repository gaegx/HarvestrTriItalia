package com.gaegxh.harvester.service.parse;

import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.model.TicketSolution;

import java.util.List;

public interface ParseService {
    List<TicketSolution> parseTickets(String jsonResponse, Task task);
}
