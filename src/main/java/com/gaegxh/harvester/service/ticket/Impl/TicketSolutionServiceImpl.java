package com.gaegxh.harvester.service.ticket.Impl;

import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.repository.TicketSolutionRepository;
import com.gaegxh.harvester.service.ticket.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketSolutionServiceImpl implements TicketService {

    private final TicketSolutionRepository repository;

    public TicketSolutionServiceImpl(TicketSolutionRepository repository) {
        this.repository = repository;
    }

    public void saveAll(List<TicketSolution> tickets) {
        repository.saveAll(tickets);
    }
}
