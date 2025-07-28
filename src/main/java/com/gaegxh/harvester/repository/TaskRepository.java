package com.gaegxh.harvester.repository;

import com.gaegxh.harvester.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {


    List<Task> findByStatus(String status);

}
