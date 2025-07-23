package com.gaegxh.harvester.service.filter.Impl;

import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.filter.SolutionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MaxChangesFilter implements SolutionFilter {

    private final int maxChanges;

    public MaxChangesFilter(@Value("${filter.maxChanges:2}") int maxChanges) {
        this.maxChanges = maxChanges;
    }

    @Override
    public boolean filter(TicketSolution solution) {
        String changeStations = solution.getChangeStation();

        if (changeStations == null || changeStations.trim().isEmpty()) {
            return true;
        }

        int changes = changeStations.split("\\|").length - 1;

        return changes <= maxChanges;
    }
}
