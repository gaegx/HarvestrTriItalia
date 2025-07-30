package com.gaegxh.harvester.service.parse.Impl;

import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.service.filter.SolutionFilter;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class SolutionParser {

    private static final Logger log = LoggerFactory.getLogger(SolutionParser.class);

    private final List<SolutionFilter> filters;

    @Autowired
    public SolutionParser(List<SolutionFilter> filters) {
        this.filters = filters != null ? filters : Collections.emptyList();
    }

    public List<TicketSolution> parseTickets(String jsonResponse, Task task) {
        log.info("Начинается парсинг JSON-ответа");
        List<TicketSolution> tickets = new ArrayList<>();

        JsonObject root = asJsonObject(JsonParser.parseString(jsonResponse));
        if (root == null) {
            log.warn("Корневой JSON не является объектом");
            return tickets;
        }

        JsonArray solutions = getJsonArray(root, "solutions");
        if (solutions == null || solutions.isEmpty()) {
            throw new NoSuchElementException("Массив 'solutions' отсутствует или пуст");
        }
        log.info("Количество решений для парсинга: {}", solutions.size());

        for (int i = 0; i < solutions.size(); i++) {
            JsonObject solutionWrapper = asJsonObject(solutions.get(i));
            if (solutionWrapper == null) {
                log.warn("Пропущен элемент solutions[{}], т.к. он не объект", i);
                continue;
            }

            JsonObject solution = getJsonObject(solutionWrapper, "solution");
            if (solution == null) {
                log.warn("solutions[{}] не содержит объект 'solution'", i);
                continue;
            }

            List<TicketSolution> parsedSolutions = parseSingleSolution(solutionWrapper, solution, i);
            for (TicketSolution ticket : parsedSolutions) {
                if (passesFilters(ticket, task)) {
                    tickets.add(ticket);
                } else {
                    log.debug("Билет отфильтрован: {}", ticket);
                }
            }
        }

        log.info("Парсинг завершён, всего найдено билетов после фильтрации: {}", tickets.size());
        return tickets;
    }

    private boolean passesFilters(TicketSolution ticket, Task task) {
        for (SolutionFilter filter : filters) {
            if (!filter.filter(ticket, task)) {
                return false;
            }
        }
        return true;
    }

    private static List<TicketSolution> parseSingleSolution(JsonObject solutionWrapper, JsonObject solution, int index) {
        List<TicketSolution> tickets = new ArrayList<>();

        String date = substringSafe(getString(solution, "departureTime"), 0, 10);
        String departure = getString(solution, "origin");
        String arrival = getString(solution, "destination");
        String departureTime = substringSafe(getString(solution, "departureTime"), 11, 16);
        String duration = getString(solution, "duration");

        log.debug("Решение [{}]: {} → {}, дата {}, время отправления {}, длительность {}",
                index, departure, arrival, date, departureTime, duration);

        List<String> trainNumbers = new ArrayList<>();
        List<String> brands = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        JsonArray trains = getJsonArray(solution, "trains");
        if (trains != null) {
            for (JsonElement trainEl : trains) {
                JsonObject train = asJsonObject(trainEl);
                if (train == null) continue;
                trainNumbers.add(getString(train, "name"));
                brands.add(getString(train, "denomination"));
                categories.add(getString(train, "trainCategory"));
            }
            log.debug("trains: {}", trainNumbers);
        } else {
            log.debug("В решении [{}] отсутствует массив 'trains'", index);
        }

        String trainNumber = String.join("|", trainNumbers);
        String trainBrand = String.join("|", brands);
        String trainClass = String.join("|", categories);

        JsonArray nodes = getJsonArray(solution, "nodes");
        Set<String> changeStations = new LinkedHashSet<>();
        if (nodes != null) {
            for (int j = 1; j < nodes.size(); j++) { // пропускаем первый узел
                JsonObject node = asJsonObject(nodes.get(j));
                if (node != null) {
                    changeStations.add(getString(node, "origin"));
                }
            }
            log.debug("Промежуточные станции (changeStations): {}", changeStations);
        } else {
            log.debug("В решении [{}] отсутствует массив 'nodes'", index);
        }
        String changeStation = changeStations.isEmpty() ? "NULL" : String.join("|", changeStations);

        JsonObject price = getJsonObject(solution, "price");
        String priceAmount = getString(price, "amount");
        String priceCurrency = getString(price, "currency");

        JsonArray grids = getJsonArray(solutionWrapper, "grids");
        Set<TicketSolution> classTickets = new LinkedHashSet<>();

        if (grids != null) {
            for (JsonElement gridEl : grids) {
                JsonObject grid = asJsonObject(gridEl);
                if (grid == null) continue;

                JsonArray services = getJsonArray(grid, "services");
                if (services == null) continue;

                for (JsonElement serviceEl : services) {
                    JsonObject service = asJsonObject(serviceEl);
                    if (service == null) continue;

                    String coachClass = getString(service, "groupName");
                    JsonArray offers = getJsonArray(service, "offers");
                    if (offers == null) continue;

                    for (JsonElement offerEl : offers) {
                        JsonObject offer = asJsonObject(offerEl);
                        if (offer == null) continue;

                        if (!"SALEABLE".equalsIgnoreCase(getString(offer, "status"))) {
                            continue;
                        }

                        String fare = getString(offer, "name");
                        JsonObject offerPrice = getJsonObject(offer, "price");
                        String offerAmount = getString(offerPrice, "amount");
                        String offerCurrency = getString(offerPrice, "currency");

                        TicketSolution ticket = new TicketSolution(
                                date, departure, arrival,
                                safe(coachClass), safe(trainBrand), safe(trainClass), safe(trainNumber),
                                safe(departureTime), safe(duration), safe(offerAmount), safe(offerCurrency),
                                safe(fare), safe(changeStation)
                        );
                        classTickets.add(ticket);
                    }
                }
            }
        } else {
            log.debug("В решении [{}] отсутствует массив 'grids'", index);
        }

        if (classTickets.isEmpty()) {
            TicketSolution fallback = new TicketSolution(date, departure, arrival);
            fallback.setTrainBrand(safe(trainBrand));
            fallback.setTrainClass(safe(trainClass));
            fallback.setTrainNumber(safe(trainNumber));
            fallback.setDepartureTime(safe(departureTime));
            fallback.setDuration(safe(duration));
            fallback.setPrice(safe(priceAmount));
            fallback.setCurrency(safe(priceCurrency));
            fallback.setChangeStation(safe(changeStation));
            tickets.add(fallback);
            log.debug("Добавлен fallback билет для решения [{}]", index);
        } else {
            tickets.addAll(classTickets);
            log.debug("Добавлено билетов с классами: {}", classTickets.size());
        }

        return tickets;
    }

    private static String getString(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    private static JsonObject asJsonObject(JsonElement el) {
        return (el != null && el.isJsonObject()) ? el.getAsJsonObject() : null;
    }

    private static JsonObject getJsonObject(JsonObject parent, String key) {
        if (parent != null && parent.has(key)) {
            return asJsonObject(parent.get(key));
        }
        return null;
    }

    private static JsonArray getJsonArray(JsonObject obj, String key) {
        if (obj != null && obj.has(key)) {
            JsonElement el = obj.get(key);
            if (el != null && el.isJsonArray()) {
                return el.getAsJsonArray();
            }
        }
        return null;
    }

    private static String safe(String str) {
        return str == null ? "NULL" : str;
    }

    private static String substringSafe(String str, int start, int end) {
        if (str != null && str.length() >= end) {
            return str.substring(start, end);
        }
        return "NULL";
    }
}
