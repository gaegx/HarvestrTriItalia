package com.gaegxh.harvester.component;



import com.gaegxh.harvester.model.Station;

import com.gaegxh.harvester.service.search.Impl.StationSearchService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class StationSelector {

    private final StationSearchService stationSearchService;

    public StationSelector(StationSearchService stationSearchService) {
        this.stationSearchService = stationSearchService;
    }

    public Station selectStationByName(String prompt) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(prompt);
        String input = scanner.nextLine();

        Station[] stations = stationSearchService.searchStations(input, 10);

        if (stations.length == 0) {
            System.out.println("Станции не найдены.");
            return null;
        }

        System.out.println("\nНайдено станций:");
        for (int i = 0; i < stations.length; i++) {
            System.out.println((i + 1) + ") " + stations[i]);
        }

        System.out.print("\nВыберите номер станции: ");
        int choice = scanner.nextInt();

        if (choice < 1 || choice > stations.length) {
            System.out.println("Неверный выбор.");
            return null;
        }

        return stations[choice - 1];
    }


}
