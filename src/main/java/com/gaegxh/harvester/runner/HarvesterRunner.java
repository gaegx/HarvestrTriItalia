package com.gaegxh.harvester.runner;

import com.gaegxh.harvester.component.TaskInverser;
import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.repository.task.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class HarvesterRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HarvesterRunner.class);

    private final TrainHarvester trainHarvester;
    private final TaskRepository taskRepository;
    private final TaskInverser trainInverser;

    public HarvesterRunner(TrainHarvester trainHarvester, TaskRepository taskRepository, TaskInverser trainInverser) {
        this.trainHarvester = trainHarvester;
        this.taskRepository = taskRepository;
        this.trainInverser = trainInverser;
    }

    @Override
    public void run(String... args) {
        Optional<Task> taskOptional = taskRepository.fetchTask("trenitalia", null);

        if (taskOptional.isEmpty()) {
            log.warn("Задача не найдена.");
            return;
        }

        Task task = taskOptional.get();
        log.info("Основная задача: {} -> {}", task.getDepartureStation(), task.getArrivalStation());




            trainHarvester.harvestSolutions(task);





    }
}
