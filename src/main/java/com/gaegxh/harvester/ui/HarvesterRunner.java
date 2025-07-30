package com.gaegxh.harvester.ui;

import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.model.Task;

import com.gaegxh.harvester.repository.task.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.*;

@Component
public class HarvesterRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HarvesterRunner.class);

    private final TrainHarvester trainHarvester;

    private final TaskRepository taskRepository;

    public HarvesterRunner(TrainHarvester trainHarvester,  TaskRepository taskRepository) {
        this.trainHarvester = trainHarvester;

        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        Optional<Task> task = taskRepository.fetchTask("trenitalia",null);
        System.out.println(task);
        trainHarvester.harvestSolutions(task.orElse(null));


        log.info("Все задачи завершены.");
    }
}
