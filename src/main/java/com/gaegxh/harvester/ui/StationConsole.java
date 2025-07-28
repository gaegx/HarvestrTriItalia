package com.gaegxh.harvester.ui;

import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.model.Task;
import com.gaegxh.harvester.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StationConsole implements CommandLineRunner {
    private final TrainHarvester trainHarvester;
    private final TaskRepository taskRepository;

    public StationConsole(TrainHarvester trainHarvester, TaskRepository taskRepository) {
        this.trainHarvester = trainHarvester;
        this.taskRepository= taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Task> tasks = taskRepository.findByStatus("Ready to Start");
        System.out.println(tasks.toString());

        if (!tasks.isEmpty()) {
            trainHarvester.harvestSolutions(tasks.get(0));
        } else {
            System.out.println("Нет задач со статусом 'Ready to Start'");
        }
    }

}