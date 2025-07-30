package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.model.TicketSearchRequest;
import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.repository.ticket.TicketRepository;
import com.gaegxh.harvester.service.export.Impl.CsvWriterService;
import com.gaegxh.harvester.service.parse.Impl.SolutionParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
    private final TicketRepository ticketRepository;



    public TrainHarvester(TicketApiClient apiClient,
                          SolutionParser solutionParser, CsvWriterService csvWriterService,
                          LastTrainChecker lastTrainChecker, TicketSearchRequestFactory requestFactory, BatchTicketHarvester batchTicketHarvester,
                          TicketRepository ticketRepository
                          ) {
        this.apiClient = apiClient;
        this.solutionParser = solutionParser;
        this.csvWriterService = csvWriterService;
        this.lastTrainChecker = lastTrainChecker;
        this.requestFactory = requestFactory;
        this.batchTicketHarvester = batchTicketHarvester;
        this.ticketRepository = ticketRepository;

    }

 public void harvestSolutions(Task task){
        TicketSearchRequest initialRequest = requestFactory.createInitialRequest(
                Long.parseLong(task.getDepartureStation()),
                Long.parseLong(task.getArrivalStation()),
                task.getDepartureDate()
        );


        String initialResponse = apiClient.fetchSolutions(initialRequest);
        List<TicketSolution> solutions = solutionParser.parseTickets(initialResponse, task);

        if (solutions.isEmpty()) {
            logger.warn("Все билеты проданы или ничего не найдено для {} -> {}", task.getDepartureStation(), task.getArrivalStation());
            return;
        }

        String filepath = "output/" + task.getDepartureStation() + "_" + task.getDepartureDate() + ".csv";


        if (lastTrainChecker.isLastTrainBeforeMidnight(initialResponse)) {
            logger.info("Последний поезд до полуночи — запускаем батч-операцию со смещением");

            List<TicketSolution> batchSolutions = batchTicketHarvester.executeBatchOperation(initialRequest,task);
            solutions.addAll(batchSolutions);
        } else {
            logger.info("Все поезда получены в первом запросе — батч не требуется");
        }


        solutions = new ArrayList<>(new LinkedHashSet<>(solutions));
        long timestamp = System.currentTimeMillis()/1000;
        csvWriterService.writeTicketsToCsv(solutions, filepath);
        String md5Checksum = ticketRepository.sendShortDataInfo(filepath,timestamp,solutions.size(),initialResponse);
        ticketRepository.getShortDataStatus(md5Checksum);
        ticketRepository.sendHarvestResult(task.getTaskUuid(),"null",200);
        ticketRepository.saveTickets(solutions,md5Checksum);
        logger.info("Итоговое количество решений: {}", solutions.size());
    }

}