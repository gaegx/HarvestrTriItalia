package com.gaegxh.harvester.service.filter.Impl;

import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.filter.SolutionFilter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FareCodeFilter implements SolutionFilter {

    @Override
    public boolean filter(TicketSolution solution, Task task) {
        String allowed = task.getFareCodes();
        if (allowed == null || allowed.isBlank()) return true;

        String brandField = solution.getFare();
        if (brandField == null || brandField.isBlank()) return false;


        Set<String> actualBrands = Arrays.stream(brandField.split("\\|"))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return Arrays.stream(allowed.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .anyMatch(actualBrands::contains);
    }
}