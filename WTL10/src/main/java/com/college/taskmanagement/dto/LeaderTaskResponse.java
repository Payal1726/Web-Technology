package com.college.taskmanagement.dto;

import com.college.taskmanagement.model.Employee;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.model.TaskStatus;

import java.time.LocalDate;

public record LeaderTaskResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        TaskStatus status,
        Long leaderId,
        String leaderName,
        Long teamMemberId,
        String teamMemberName
) {
    public static LeaderTaskResponse from(Task task) {
        Employee leader = task.getAssignedByLeader();
        Employee teamMember = task.getAssignedEmployee();

        return new LeaderTaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                leader != null ? leader.getId() : null,
                leader != null ? leader.getName() : null,
                teamMember != null ? teamMember.getId() : null,
                teamMember != null ? teamMember.getName() : null
        );
    }
}
