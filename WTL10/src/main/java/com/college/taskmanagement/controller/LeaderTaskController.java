package com.college.taskmanagement.controller;

import com.college.taskmanagement.dto.LeaderTaskRequest;
import com.college.taskmanagement.dto.LeaderTaskResponse;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaders/{leaderId}/tasks")
@Validated
public class LeaderTaskController {
    private final TaskService taskService;

    public LeaderTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<LeaderTaskResponse> getLeaderTasks(@PathVariable Long leaderId) {
        return taskService.findTasksAssignedByLeader(leaderId).stream()
                .map(LeaderTaskResponse::from)
                .toList();
    }

    @GetMapping("/{taskId}")
    public LeaderTaskResponse getLeaderTask(@PathVariable Long leaderId, @PathVariable Long taskId) {
        Task task = taskService.findLeaderTask(leaderId, taskId);
        return LeaderTaskResponse.from(task);
    }

    @PostMapping
    public ResponseEntity<LeaderTaskResponse> createLeaderTask(@PathVariable Long leaderId,
                                                               @Valid @RequestBody LeaderTaskRequest request) {
        Task task = taskService.createTaskAsLeader(leaderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LeaderTaskResponse.from(task));
    }

    @PutMapping("/{taskId}")
    public LeaderTaskResponse updateLeaderTask(@PathVariable Long leaderId,
                                               @PathVariable Long taskId,
                                               @Valid @RequestBody LeaderTaskRequest request) {
        Task task = taskService.updateTaskAsLeader(leaderId, taskId, request);
        return LeaderTaskResponse.from(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteLeaderTask(@PathVariable Long leaderId, @PathVariable Long taskId) {
        taskService.deleteTaskAsLeader(leaderId, taskId);
        return ResponseEntity.noContent().build();
    }
}
