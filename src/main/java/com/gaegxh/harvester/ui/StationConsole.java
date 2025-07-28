package com.gaegxh.harvester.ui;

import com.gaegxh.harvester.component.TrainHarvester;
import com.gaegxh.harvester.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        var tasks = taskRepository. getTaskByTaskUuid("");
        trainHarvester.harvestSolutions();
    }
}