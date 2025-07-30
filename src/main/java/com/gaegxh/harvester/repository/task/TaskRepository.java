package com.gaegxh.harvester.repository.task;

import com.gaegxh.harvester.model.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TaskRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final Gson gson;
    public TaskRepository(Gson gson) {
        this.gson = gson;
    }

    @Transactional
    public Optional<Task> fetchTask(String crawlerType, Integer harvesterServerId) {
        Query nativeQuery = entityManager.createNativeQuery(
                "CALL get_task_from_harvester_queue(:crawlerType, :harvesterServerId)");

        nativeQuery.setParameter("crawlerType", crawlerType);
        nativeQuery.setParameter("harvesterServerId", harvesterServerId);

        try {
            Object result = nativeQuery.getSingleResult();
            if (result != null) {
                String json = result.toString();
                JsonElement element = JsonParser.parseString(json);
                if (element.isJsonObject()) {
                    Task task = gson.fromJson(element, Task.class);
                    return Optional.of(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
