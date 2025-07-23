package com.gaegxh.harvester.service.export;

import com.gaegxh.harvester.model.TicketSolution;

import java.util.List;

public interface ExporterService {
    public  void writeTicketsToCsv(List<TicketSolution> tickets, String filePath);
}
