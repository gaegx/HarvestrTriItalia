package com.gaegxh.harvester.repository.ticket;


import com.gaegxh.harvester.model.TicketSolution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class TicketRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void saveTickets(List<TicketSolution> solutions, String md5Checksum) {
        int batchSize = 50;
        int count = 0;

        for (TicketSolution ticket : solutions) {
            entityManager.createNativeQuery("""
                INSERT INTO tcs__queue_list (
                    file_md5checksum, departure_date, code_from, code_to,
                    coach_class, train_brand_code, train_class_code, train_number,
                    departure_time_offset_sec, duration_time_offset_sec, price_value,
                    currency_code, fare_code, change_stations
                )
                VALUES (
                    :md5, :date, :from, :to,
                    :coach, :brand, :class, :number,
                    :departureTime, :duration, :price,
                    :currency, :fare, :changeStation
                )
            """)
            .setParameter("md5", md5Checksum)
            .setParameter("date", ticket.getDate())
            .setParameter("from", ticket.getDeparture())
            .setParameter("to", ticket.getArrival())
            .setParameter("coach", ticket.getCoachClassName())
            .setParameter("brand", ticket.getTrainBrand())
            .setParameter("class", ticket.getTrainClass())
            .setParameter("number", ticket.getTrainNumber())
            .setParameter("departureTime", parseSafeInt(ticket.getDepartureTime()))
            .setParameter("duration", parseSafeInt(ticket.getDuration()))
            .setParameter("price", parseSafeDouble(ticket.getPrice()))
            .setParameter("currency", ticket.getCurrency())
            .setParameter("fare", ticket.getFare())
            .setParameter("changeStation", ticket.getChangeStation())
            .executeUpdate();

            if (++count % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();

    }

    private int parseSafeInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseSafeDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Transactional
    public void sendHarvestResult(String taskId, String resultMessage, int resultCode) {
        try {
            Query query = entityManager.createNativeQuery(
                    "CALL give_the_answer_for_harvester_queue(:taskId, :message, :code)");

            query.setParameter("taskId", taskId);
            query.setParameter("message", resultMessage);
            query.setParameter("code", resultCode);

            query.executeUpdate();


        } catch (Exception e) {

            throw e;
        }
    }
}
