package com.gaegxh.harvester.ui;

import com.gaegxh.harvester.component.TrainHarvester;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StationConsole implements CommandLineRunner {
    private final TrainHarvester trainHarvester;

    public StationConsole(TrainHarvester trainHarvester) {
        this.trainHarvester = trainHarvester;
    }

    @Override
    public void run(String... args) throws Exception {
        trainHarvester.harvestSolutions();
    }
}