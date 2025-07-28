package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Station;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.export.Impl.CsvWriterService;
import com.gaegxh.harvester.service.parse.Impl.SolutionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.getLong;

@Component
public class TrainHarvester {
    private static final Logger logger = LoggerFactory.getLogger(TrainHarvester.class);
    private final TicketApiClient apiClient;
    private final SolutionParser solutionParser;
    private final CsvWriterService csvWriterService;
    private final LastTrainChecker lastTrainChecker;
    private final TicketSearchRequestFactory requestFactory;
    private final BatchTicketHarvester batchTicketHarvester;


    public TrainHarvester(StationSelector stationSelector, TicketApiClient apiClient,
                          SolutionParser solutionParser, CsvWriterService csvWriterService,
                          LastTrainChecker lastTrainChecker, TicketSearchRequestFactory requestFactory,BatchTicketHarvester batchTicketHarvester) {
        this.apiClient = apiClient;
        this.solutionParser = solutionParser;
        this.csvWriterService = csvWriterService;
        this.lastTrainChecker = lastTrainChecker;
        this.requestFactory = requestFactory;
        this.batchTicketHarvester = batchTicketHarvester;

    }

    public void harvestSolutions(Task task) throws Exception {
        boolean batch = true;

        TicketSearchRequest initialRequest = requestFactory.createInitialRequest(
                Long.parseLong(task.getDepartureStation()), Long.parseLong(task.getArrivalStation()),task.getDepartureDate());
        String initialResponse = apiClient.fetchSolutions(initialRequest);
        List<TicketSolution> solutions = solutionParser.parseTickets(
                initialResponse);



        if (!solutions.isEmpty() && lastTrainChecker.isLastTrainBeforeMidnight(initialResponse)) {
            logger.info("Последний поезд отправляется до полуночи, выполняем повторный запрос");
            Optional<OffsetDateTime> lastDepartureTime = lastTrainChecker.getLastTrainDepartureTime(initialResponse);
            TicketSearchRequest nextRequest = requestFactory.createRequestWithTime(
                    Long.parseLong(task.getDepartureStation()), Long.parseLong(task.getArrivalStation()),
                    lastDepartureTime.map(OffsetDateTime::toString).orElse(null));
            String nextResponse = apiClient.fetchSolutions(nextRequest);
            solutions.addAll(solutionParser.parseTickets(
                    nextResponse));
            batch = false;


        }

        if (solutions.isEmpty()) {
            logger.error("Все билеты проданы");
            System.out.println("Все билеты проданы");
        }
        String filepath = "output/" + (task.getDepartureStation())+solutions.get(0).getDepartureTime() +".csv";

        if (batch) {
            List<TicketSolution> batchSolutions = batchTicketHarvester.executeBatchOperation(initialRequest,filepath);
            solutions.addAll(batchSolutions);
            solutions = new ArrayList<>(new LinkedHashSet<>(solutions));
        }

        csvWriterService.writeTicketsToCsv(solutions, filepath);
        logger.info("Итоговое количество решений: {}", solutions.size());
    }
}