package com.gaegxh.harvester.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class LastTrainChecker {
    private static final Logger logger = LoggerFactory.getLogger(LastTrainChecker.class);

    public Optional<OffsetDateTime> getLastTrainDepartureTime(String jsonResponse) {
        try {
            JsonObject response = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray solutions = response.getAsJsonArray("solutions");
            if (solutions != null && !solutions.isEmpty()) {
                JsonArray nodes = solutions.get(solutions.size() - 1).getAsJsonObject()
                        .getAsJsonObject("solution").getAsJsonArray("nodes");
                if (nodes != null && !nodes.isEmpty()) {
                    String departureTimeStr = nodes.get(nodes.size() - 1).getAsJsonObject()
                            .get("departureTime").getAsString();
                    return Optional.of(OffsetDateTime.parse(departureTimeStr));
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка при извлечении времени отправления последнего поезда: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public boolean isLastTrainBeforeMidnight(String jsonResponse) {
        Optional<OffsetDateTime> departureTime = getLastTrainDepartureTime(jsonResponse);
        return departureTime.map(dt -> dt.toLocalTime().isBefore(LocalTime.of(23, 59, 59))).orElse(false);
    }
}