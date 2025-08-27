package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Task;
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
    private final CriteriaFactory criteriaFactory;


    public BatchTicketHarvester(TicketApiClient apiClient, SolutionParser solutionParser,
                                CsvWriterService csvWriterService, Gson gson, CriteriaFactory criteriaFactory) {
        this.apiClient = apiClient;
        this.solutionParser = solutionParser;
        this.csvWriterService = csvWriterService;
        this.gson =gson;
        this.criteriaFactory = criteriaFactory;
    }


   public List<TicketSolution> executeBatchOperation(TicketSearchRequest initialRequest, Task task) {
        logger.info("Начало батч-операции с постраничным смещением");

        int offset = 10;
        int limit = 10;
        int maxAttempts = 100;

        List<TicketSolution> allSolutions = new ArrayList<>();

        String initialCartResponse = apiClient.fetchSolutions(initialRequest);
        String cartId = extractCartId(initialCartResponse);
        if (cartId == null) {
            logger.warn("cartId не найден. Батч-операция завершена.");
            return allSolutions;
        }

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            logger.info("Попытка {}: offset={}, limit={}", attempt + 1, offset, limit);

            TicketSearchRequest paginatedRequest = TicketSearchRequest.builder()
                    .cartId(cartId)
                    .departureLocationId(initialRequest.getDepartureLocationId())
                    .arrivalLocationId(initialRequest.getArrivalLocationId())
                    .departureTime(initialRequest.getDepartureTime())
                    .adults(initialRequest.getAdults())
                    .children(initialRequest.getChildren())
                    .criteria(criteriaFactory.create("DEPARTURE_DATE", offset, limit))
                    .build();

            String response;
            try {
                response = apiClient.fetchSolutions(paginatedRequest);
                logger.debug("Ответ получен: {} символов", response.length());
                System.out.println(response);
            } catch (Exception e) {
                logger.error("Ошибка при получении ответа от API: {}", e.getMessage());
                break;
            }

            if (!containsSolutionsArray(response)) {
                logger.info("Ответ не содержит массива solutions. Завершаем цикл.");
                break;
            }

            List<TicketSolution> batchSolutions;
            try {
                batchSolutions = solutionParser.parseTickets(response,task);
            } catch (Exception e) {
                logger.warn("Ошибка при парсинге JSON: {}", e.getMessage());
                break;
            }

            if (batchSolutions.isEmpty()) {
                logger.info("Получено пустое решение — завершение.");
                break;
            }

            allSolutions.addAll(batchSolutions);
            logger.info("Добавлено {} решений на этом шаге. Всего накоплено: {}", batchSolutions.size(), allSolutions.size());

            offset += limit;


        }

        logger.info("Батч-операция завершена. Всего решений: {}", allSolutions.size());
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

    private boolean containsSolutionsArray(String response) {
        try {
            JsonObject json = gson.fromJson(response, JsonObject.class);
            return json.has("solutions") && json.get("solutions").isJsonArray();
        } catch (Exception e) {
            logger.warn("Ошибка при проверке solutions: {}", e.getMessage());
            return false;
        }
    }

}
