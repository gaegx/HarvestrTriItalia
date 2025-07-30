package com.gaegxh.harvester.service.filter.Impl;

import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.filter.SolutionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MaxChangesFilter implements SolutionFilter {


    @Override
    public boolean filter(TicketSolution solution, Task task) {
        String changeStations = solution.getChangeStation();

        if (changeStations == null || changeStations.trim().isEmpty()) {
            return true;
        }

        int changes = changeStations.split("\\|").length ;

        return changes <= task.getChanges();
    }
}
