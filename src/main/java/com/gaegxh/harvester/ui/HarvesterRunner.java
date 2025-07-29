package com.gaegxh.harvester.ui;

import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
public class HarvesterRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HarvesterRunner.class);

    private final TrainHarvester trainHarvester;
    private final TaskRepository taskRepository;

    public HarvesterRunner(TrainHarvester trainHarvester, TaskRepository taskRepository) {
        this.trainHarvester = trainHarvester;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        List<Task> tasks = taskRepository.findByStatus("Ready to Start");

        int availableThreads = Runtime.getRuntime().availableProcessors();
        System.out.println(availableThreads);
        int poolSize = Math.min(availableThreads, tasks.size());

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        CompletionService<Task> completionService = new ExecutorCompletionService<>(executor);

        log.info("Запуск {} задач в {} потоках", tasks.size(), poolSize);

        for (Task task : tasks) {
            completionService.submit(() -> {
                try {
                    log.info("Старт задачи {}", task.getTaskUuid());
                    trainHarvester.harvestSolutions(task);


                    task.setStatus("Completed");
                    task.setLastUpdated(java.time.LocalDateTime.now());
                    taskRepository.save(task);

                    log.info("Задача завершена: {}", task.getTaskUuid());
                } catch (Exception e) {
                    log.error("Ошибка при обработке задачи {}: {}", task.getTaskUuid(), e.getMessage(), e);
                    task.setStatus("Failed");
                    task.setLastUpdated(java.time.LocalDateTime.now());
                    taskRepository.save(task);
                }
                return task;
            });
        }


        for (int i = 0; i < tasks.size(); i++) {
            try {
                Future<Task> future = completionService.take();
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Ошибка при ожидании завершения задач: {}", e.getMessage(), e);
            }
        }

        executor.shutdown();
        log.info("Все задачи завершены.");
    }
}
