package com.college.taskmanagement.repository;

import com.college.taskmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedEmployeeId(Long employeeId);
    List<Task> findByAssignedByLeaderIdOrderByDueDateAscIdAsc(Long leaderId);
    Optional<Task> findByIdAndAssignedByLeaderId(Long id, Long leaderId);
}
