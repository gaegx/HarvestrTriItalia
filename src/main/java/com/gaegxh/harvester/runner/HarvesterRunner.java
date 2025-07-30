package com.gaegxh.harvester.runner;

import com.gaegxh.harvester.component.TaskInverser;
import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.repository.task.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.util.concurrent.*;

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
        taskRepository.fetchTask("trenitalia", null)
                .ifPresentOrElse(
                        task -> {
                            log.info("Основная задача: {} -> {}", task.getDepartureStation(), task.getArrivalStation());


                            CompletableFuture<Void> mainFuture = CompletableFuture.runAsync(() -> safeHarvest(task));


                            CompletableFuture<Void> reversedFuture = (task.getReverse() == 1)
                                    ? CompletableFuture.runAsync(() -> {
                                Task reversed = trainInverser.getInversedTask(task);
                                log.info("Обратная задача: {} -> {}", reversed.getDepartureStation(), reversed.getArrivalStation());
                                safeHarvest(reversed);
                            })
                                    : CompletableFuture.completedFuture(null);


                            CompletableFuture<Void> combined = CompletableFuture.allOf(mainFuture, reversedFuture);


                            combined.exceptionally(ex -> {
                                log.error("Ошибка в процессе выполнения задач", ex);
                                return null;
                            }).join();
                        },
                        () -> log.warn("Задача не найдена.")
                );
    }

    private void safeHarvest(Task task) {
        try {
            trainHarvester.harvestSolutions(task);
        } catch (Exception e) {
            log.error("Ошибка при обработке задачи: {} -> {}", task.getDepartureStation(), task.getArrivalStation(), e);
        }
    }
}


