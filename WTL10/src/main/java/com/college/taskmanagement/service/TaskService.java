package com.college.taskmanagement.service;

import com.college.taskmanagement.dto.LeaderTaskRequest;
import com.college.taskmanagement.exception.ResourceNotFoundException;
import com.college.taskmanagement.model.Employee;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.model.TaskStatus;
import com.college.taskmanagement.repository.EmployeeRepository;
import com.college.taskmanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public TaskService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    public Task create(Task task) {
        task.setAssignedByLeader(null);
        return taskRepository.save(task);
    }

    public Task update(Long id, Task details) {
        Task task = findById(id);
        task.setTitle(details.getTitle());
        task.setDescription(details.getDescription());
        task.setDueDate(details.getDueDate());
        task.setStatus(details.getStatus());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    public Task assignToEmployee(Long taskId, Long employeeId) {
        Task task = findById(taskId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        if (task.getAssignedEmployee() != null) {
            task.getAssignedEmployee().removeTask(task);
        }
        employee.addTask(task);
        task.setAssignedByLeader(null);
        return taskRepository.save(task);
    }

    public Task assignTaskAsLeader(Long leaderId, Long taskId, Long subordinateId) {
        Task task = findById(taskId);
        Employee leader = findEmployee(leaderId);
        Employee subordinate = validateDirectReport(leader, subordinateId);
        if (task.getAssignedEmployee() != null) {
            task.getAssignedEmployee().removeTask(task);
        }
        subordinate.addTask(task);
        task.setAssignedByLeader(leader);
        return taskRepository.save(task);
    }

    public List<Task> findByEmployeeId(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        return taskRepository.findByAssignedEmployeeId(employeeId);
    }

    public List<Task> findTasksAssignedByLeader(Long leaderId) {
        findEmployee(leaderId);
        List<Task> tasks = taskRepository.findByAssignedByLeaderIdOrderByDueDateAscIdAsc(leaderId);
        tasks.forEach(this::initializeTaskDetails);
        return tasks;
    }

    public Task findLeaderTask(Long leaderId, Long taskId) {
        findEmployee(leaderId);
        Task task = taskRepository.findByIdAndAssignedByLeaderId(taskId, leaderId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        initializeTaskDetails(task);
        return task;
    }

    public Task createTaskAsLeader(Long leaderId, LeaderTaskRequest request) {
        Employee leader = findEmployee(leaderId);
        Employee subordinate = validateDirectReport(leader, request.getSubordinateId());

        Task task = new Task();
        applyLeaderTaskDetails(task, request);
        subordinate.addTask(task);
        task.setAssignedByLeader(leader);

        Task savedTask = taskRepository.save(task);
        initializeTaskDetails(savedTask);
        return savedTask;
    }

    public Task updateTaskAsLeader(Long leaderId, Long taskId, LeaderTaskRequest request) {
        Task task = findLeaderTask(leaderId, taskId);
        Employee leader = task.getAssignedByLeader();
        Employee subordinate = validateDirectReport(leader, request.getSubordinateId());

        applyLeaderTaskDetails(task, request);
        if (task.getAssignedEmployee() == null) {
            subordinate.addTask(task);
        } else if (!task.getAssignedEmployee().getId().equals(subordinate.getId())) {
            task.getAssignedEmployee().removeTask(task);
            subordinate.addTask(task);
        }
        task.setAssignedByLeader(leader);

        Task savedTask = taskRepository.save(task);
        initializeTaskDetails(savedTask);
        return savedTask;
    }

    public void deleteTaskAsLeader(Long leaderId, Long taskId) {
        Task task = findLeaderTask(leaderId, taskId);
        taskRepository.delete(task);
    }

    private void applyLeaderTaskDetails(Task task, LeaderTaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
    }

    private Employee findEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
    }

    private Employee validateDirectReport(Employee leader, Long subordinateId) {
        Employee subordinate = findEmployee(subordinateId);
        if (subordinate.getManager() == null || !subordinate.getManager().getId().equals(leader.getId())) {
            throw new IllegalArgumentException("Leader can assign tasks only to direct team members");
        }
        return subordinate;
    }

    private void initializeTaskDetails(Task task) {
        if (task.getAssignedEmployee() != null) {
            task.getAssignedEmployee().getName();
        }
        if (task.getAssignedByLeader() != null) {
            task.getAssignedByLeader().getName();
        }
    }
}
