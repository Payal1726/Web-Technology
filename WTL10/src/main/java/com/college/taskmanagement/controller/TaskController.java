package com.college.taskmanagement.controller;

import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Task> getTasksByEmployee(@PathVariable Long employeeId) {
        return taskService.findByEmployeeId(employeeId);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task saved = taskService.create(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.update(id, task);
    }

    @PutMapping("/{id}/assign/{employeeId}")
    public Task assignTask(@PathVariable Long id, @PathVariable Long employeeId) {
        return taskService.assignToEmployee(id, employeeId);
    }

    @PutMapping("/{taskId}/assign/{leaderId}/subordinate/{subordinateId}")
    public Task assignTaskByLeader(@PathVariable Long taskId,
                                   @PathVariable Long leaderId,
                                   @PathVariable Long subordinateId) {
        return taskService.assignTaskAsLeader(leaderId, taskId, subordinateId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
