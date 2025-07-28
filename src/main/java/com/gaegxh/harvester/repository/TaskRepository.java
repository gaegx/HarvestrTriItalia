package com.gaegxh.harvester.repository;

import com.gaegxh.harvester.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String>
{
    Task getTaskByTaskUuid(String taskUuid);

}
