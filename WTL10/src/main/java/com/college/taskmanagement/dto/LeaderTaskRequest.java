package com.college.taskmanagement.dto;

import com.college.taskmanagement.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class LeaderTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    @NotNull(message = "Subordinate id is required")
    private Long subordinateId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getSubordinateId() {
        return subordinateId;
    }

    public void setSubordinateId(Long subordinateId) {
        this.subordinateId = subordinateId;
    }
}
