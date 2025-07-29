package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.export.Impl.CsvWriterService;
import com.gaegxh.harvester.service.parse.Impl.SolutionParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BatchTicketHarvester {
    private static final Logger logger = LoggerFactory.getLogger(BatchTicketHarvester.class);
    private final TicketApiClient apiClient;
    private final SolutionParser solutionParser;
    private final CsvWriterService csvWriterService;
    private final Gson gson;

    public BatchTicketHarvester(TicketApiClient apiClient, SolutionParser solutionParser,
                                CsvWriterService csvWriterService) {
        this.apiClient = apiClient;
        this.solutionParser = solutionParser;
        this.csvWriterService = csvWriterService;
        this.gson = new Gson();
    }

    public List<TicketSolution> executeBatchOperation(TicketSearchRequest initialRequest, String filepath) {
        logger.info("Начало батч-операции с пошаговым перебором до полуночи");

        String solutionsResponse = apiClient.fetchSolutions(initialRequest);
        List<TicketSolution> allSolutions = new ArrayList<>(solutionParser.parseTickets(solutionsResponse));

        String cartId = extractCartId(solutionsResponse);
        if (cartId == null) {
            logger.warn("cartId не найден. Батч-операция завершена на первом шаге.");
            csvWriterService.writeTicketsToCsv(allSolutions, filepath);
            return allSolutions;
        }

        TicketSearchRequest currentRequest = createBatchRequest(initialRequest, cartId);

        boolean continueFetching = true;
        while (continueFetching) {
            String response = apiClient.fetchSolutions(currentRequest);
            List<TicketSolution> batchSolutions = solutionParser.parseTickets(response);

            if (batchSolutions.isEmpty()) {
                logger.info("Нет новых решений. Останавливаем.");
                break;
            }

            allSolutions.addAll(batchSolutions);

            if (isLastTrainBeforeMidnight(response)) {
                String lastDepartureTime = extractLastDepartureTime(response);
                if (lastDepartureTime == null) {
                    logger.warn("Не удалось извлечь время последнего поезда. Завершаем.");
                    break;
                }

                logger.info("Следующий запрос с отправлением после {}", lastDepartureTime);

                currentRequest = TicketSearchRequest.builder()
                        .cartId(cartId)
                        .departureLocationId(initialRequest.getDepartureLocationId())
                        .arrivalLocationId(initialRequest.getArrivalLocationId())
                        .departureTime(lastDepartureTime)
                        .adults(initialRequest.getAdults())
                        .children(initialRequest.getChildren())
                        .criteria(initialRequest.getCriteria())
                        .advancedSearchRequest(initialRequest.getAdvancedSearchRequest())
                        .build();
            } else {
                logger.info("Последний поезд после полуночи. Завершаем.");
                continueFetching = false;
            }
        }


        logger.info("Батч-операция завершена, найдено {} решений", allSolutions.size());
        return allSolutions;
    }

    private String extractCartId(String response) {
        try {
            JsonObject responseJson = gson.fromJson(response, JsonObject.class);
            if (responseJson.has("cartId")) {
                String cartId = responseJson.get("cartId").getAsString();
                logger.info("Извлечен cartId: {}", cartId);
                return cartId;
            }
            return null;
        } catch (Exception e) {
            logger.warn("Ошибка при извлечении cartId: {}", e.getMessage());
            return null;
        }
    }

    private boolean isLastTrainBeforeMidnight(String response) {
        try {
            String lastDepartureTime = extractLastDepartureTime(response);
            if (lastDepartureTime == null) {
                return false;
            }
            OffsetDateTime time = OffsetDateTime.parse(lastDepartureTime);
            return time.getHour() < 23 || (time.getHour() == 23 && time.getMinute() < 59);
        } catch (Exception e) {
            logger.warn("Ошибка при определении времени последнего поезда: {}", e.getMessage());
            return false;
        }
    }

    private String extractLastDepartureTime(String response) {
        try {
            JsonObject responseJson = gson.fromJson(response, JsonObject.class);
            if (!responseJson.has("solutions")) {
                return null;
            }

            JsonArray solutionsArray = responseJson.getAsJsonArray("solutions");
            if (solutionsArray.size() == 0) {
                return null;
            }

            JsonObject lastSolution = solutionsArray.get(solutionsArray.size() - 1).getAsJsonObject();
            if (lastSolution.has("departureDateTime")) {
                String departureTime = lastSolution.get("departureDateTime").getAsString();
                logger.info("Последнее время отправления в ответе: {}", departureTime);
                return departureTime;
            }

            return null;
        } catch (Exception e) {
            logger.warn("Ошибка при извлечении времени последнего поезда: {}", e.getMessage());
            return null;
        }
    }

    private TicketSearchRequest createBatchRequest(TicketSearchRequest initialRequest, String cartId) {
        return TicketSearchRequest.builder()
                .cartId(cartId)
                .departureLocationId(initialRequest.getDepartureLocationId())
                .arrivalLocationId(initialRequest.getArrivalLocationId())
                .departureTime(initialRequest.getDepartureTime())
                .adults(initialRequest.getAdults())
                .children(initialRequest.getChildren())
                .criteria(initialRequest.getCriteria())
                .advancedSearchRequest(initialRequest.getAdvancedSearchRequest())
                .build();
    }
}
