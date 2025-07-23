package com.gaegxh.harvester.service.parse.Impl;

import com.gaegxh.harvester.model.TicketSolution;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SolutionParser {

    private static final Logger log = LoggerFactory.getLogger(SolutionParser.class);

    public static List<TicketSolution> parseTickets(String jsonResponse) {
        log.info("Начинается парсинг JSON-ответа");
        List<TicketSolution> result = new ArrayList<>();

        JsonObject root = safeObject(JsonParser.parseString(jsonResponse));
        JsonArray solutions = safeArray(root, "solutions");
        if (solutions == null) {
            log.warn("В JSON отсутствует массив 'solutions'");
            return result;
        }

        log.info("Количество решений для парсинга: {}", solutions.size());

        for (int i = 0; i < solutions.size(); i++) {
            JsonElement solutionElement = solutions.get(i);
            JsonObject solutionObj = safeObject(solutionElement);
            if (solutionObj == null) {
                log.warn("Пропущен элемент solutions[{}], т.к. он не объект", i);
                continue;
            }

            JsonObject sol = safeObject(solutionObj, "solution");
            if (sol == null) {
                log.warn("solutions[{}] не содержит объект 'solution'", i);
                continue;
            }

            String date = safeSubstring(getAsString(sol, "departureTime"), 0, 10);
            String departure = getAsString(sol, "origin");
            String arrival = getAsString(sol, "destination");
            String departureTime = safeSubstring(getAsString(sol, "departureTime"), 11, 16);
            String duration = getAsString(sol, "duration");

            log.debug("Решение [{}]: {} → {}, дата {}, время отправления {}, длительность {}",
                    i, departure, arrival, date, departureTime, duration);

            JsonArray trains = safeArray(sol, "trains");
            List<String> trainNumbers = new ArrayList<>();
            List<String> brands = new ArrayList<>();
            List<String> categories = new ArrayList<>();
            if (trains != null) {
                for (JsonElement train : trains) {
                    JsonObject trainObj = safeObject(train);
                    if (trainObj == null) continue;
                    trainNumbers.add(getAsString(trainObj, "name"));
                    brands.add(getAsString(trainObj, "denomination"));
                    categories.add(getAsString(trainObj, "trainCategory"));
                }
                log.debug("trains: {}", trainNumbers);
            } else {
                log.debug("В решении [{}] отсутствует массив 'trains'", i);
            }

            String trainNumber = String.join("|", trainNumbers);
            String trainBrand = String.join("|", brands);
            String trainClass = String.join("|", categories);

            JsonArray nodes = safeArray(sol, "nodes");
            Set<String> changeStations = new LinkedHashSet<>();
            if (nodes != null) {
                for (int j = 1; j < nodes.size(); j++) {
                    JsonObject node = safeObject(nodes.get(j));
                    if (node != null) {
                        changeStations.add(getAsString(node, "origin"));
                    }
                }
                log.debug("Промежуточные станции (changeStations): {}", changeStations);
            } else {
                log.debug("В решении [{}] отсутствует массив 'nodes'", i);
            }
            String changeStation = changeStations.isEmpty() ? "NULL" : String.join("|", changeStations);

            JsonObject priceObj = safeObject(sol, "price");
            String price = getAsString(priceObj, "amount");
            String currency = getAsString(priceObj, "currency");

            JsonArray grids = safeArray(solutionObj, "grids");
            Set<TicketSolution> classTickets = new LinkedHashSet<>();

            if (grids != null) {
                for (JsonElement gridElement : grids) {
                    JsonObject grid = safeObject(gridElement);
                    if (grid == null) continue;

                    JsonArray services = safeArray(grid, "services");
                    if (services == null) continue;

                    for (JsonElement serviceElement : services) {
                        JsonObject service = safeObject(serviceElement);
                        if (service == null) continue;

                        String coachClass = getAsString(service, "groupName");

                        JsonArray offers = safeArray(service, "offers");
                        if (offers == null || offers.isEmpty()) continue;

                        for (JsonElement offerElement : offers) {
                            JsonObject offer = safeObject(offerElement);
                            if (offer == null) continue;

                            String status = getAsString(offer, "status");
                            if (status == null || !"SALEABLE".equalsIgnoreCase(status)) {
                                continue;
                            }

                            String fare = getAsString(offer, "name");
                            JsonObject offerPrice = safeObject(offer, "price");
                            String offerAmount = getAsString(offerPrice, "amount");
                            String offerCurrency = getAsString(offerPrice, "currency");

                            TicketSolution ticket = new TicketSolution(
                                    date,
                                    departure,
                                    arrival,
                                    safe(coachClass),
                                    safe(trainBrand),
                                    safe(trainClass),
                                    safe(trainNumber),
                                    safe(departureTime),
                                    safe(duration),
                                    safe(offerAmount),
                                    safe(offerCurrency),
                                    safe(fare),
                                    safe(changeStation)
                            );
                            classTickets.add(ticket);
                        }
                    }
                }
            } else {
                log.debug("В решении [{}] отсутствует массив 'grids'", i);
            }

            if (classTickets.isEmpty()) {
                TicketSolution fallback = new TicketSolution(date, departure, arrival);
                fallback.setTrainBrand(safe(trainBrand));
                fallback.setTrainClass(safe(trainClass));
                fallback.setTrainNumber(safe(trainNumber));
                fallback.setDepartureTime(safe(departureTime));
                fallback.setDuration(safe(duration));
                fallback.setPrice(safe(price));
                fallback.setCurrency(safe(currency));
                fallback.setChangeStation(safe(changeStation));
                result.add(fallback);
                log.debug("Добавлен fallback билет для решения [{}]", i);
            } else {
                result.addAll(classTickets);
                log.debug("Добавлено билетов с классами: {}", classTickets.size());
            }
        }

        log.info("Парсинг завершён, всего найдено билетов: {}", result.size());
        return result;
    }



    private static String getAsString(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    private static JsonObject safeObject(JsonElement el) {
        return (el != null && el.isJsonObject()) ? el.getAsJsonObject() : null;
    }

    private static JsonObject safeObject(JsonObject parent, String key) {
        if (parent != null && parent.has(key)) {
            JsonElement el = parent.get(key);
            return safeObject(el);
        }
        return null;
    }

    private static JsonArray safeArray(JsonObject obj, String key) {
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

    private static String safeSubstring(String str, int start, int end) {
        if (str != null && str.length() >= end) {
            return str.substring(start, end);
        }
        return "NULL";
    }
}
