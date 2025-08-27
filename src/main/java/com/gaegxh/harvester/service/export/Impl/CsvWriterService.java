package com.gaegxh.harvester.service.export.Impl;

import com.gaegxh.harvester.model.TicketSolution;
import com.gaegxh.harvester.service.export.ExporterService;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CsvWriterService implements ExporterService {


    private final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Override
    public void writeTicketsToCsv(List<TicketSolution> tickets, String filePath) {
        Object fileLock = fileLocks.computeIfAbsent(filePath, key -> new Object());

        synchronized (fileLock) {
            try {
                File safeFile = getFile(filePath);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(safeFile))) {
                    writer.write("Date,Departure,Arrival,Coach Class,Train Brand,Changes,Train Number,Departure Time,Duration,Price,Currency,Fare,Change Station");
                    writer.newLine();

                    for (TicketSolution ticket : tickets) {
                        writer.write(toCsvLine(ticket));
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при записи билетов в файл: " + e.getMessage(), e);
            }
        }
    }

    private static File getFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new IOException("Не удалось создать директорию: " + parentDir.getAbsolutePath());
            }
        }

        String safeFileName = file.getName().replace(":", "-");
        return new File(parentDir, safeFileName);
    }

    private static String toCsvLine(TicketSolution t) {
        String changeStations = nullToEmpty(t.getChangeStation());

        String changeType = "direct";
        if (!"NULL".equalsIgnoreCase(changeStations) && !changeStations.isBlank()) {
            int count = changeStations.split("\\|").length;
            changeType = count == 1 ? "1 change" : count + " changes";
        }

        return String.join(",",
                nullToEmpty(t.getDate()),
                nullToEmpty(t.getDeparture()),
                nullToEmpty(t.getArrival()),
                nullToEmpty(t.getCoachClassName()),
                nullToEmpty(t.getTrainBrand()),
                changeType,
                nullToEmpty(t.getTrainNumber()),
                nullToEmpty(t.getDepartureTime()),
                nullToEmpty(t.getDuration()),
                nullToEmpty(t.getPrice()),
                nullToEmpty(t.getCurrency()),
                nullToEmpty(t.getFare()),
                changeStations
        );
    }

    private static String nullToEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? "NULL" : value;
    }
}
