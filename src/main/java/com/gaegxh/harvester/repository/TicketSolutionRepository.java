package com.gaegxh.harvester.repository;

import com.gaegxh.harvester.model.TicketSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketSolutionRepository extends JpaRepository<TicketSolution, Long> {


}
