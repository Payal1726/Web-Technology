package com.college.taskmanagement.controller;

import com.college.taskmanagement.model.Employee;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/{id}/tasks")
    public List<Task> getEmployeeTasks(@PathVariable Long id) {
        return employeeService.findTasks(id);
    }

    @GetMapping("/{id}/subordinates")
    public List<Employee> getLeaderSubordinates(@PathVariable Long id) {
        return employeeService.getSubordinates(id);
    }

    @PutMapping("/{leaderId}/subordinates/{subordinateId}")
    public Employee assignSubordinate(@PathVariable Long leaderId, @PathVariable Long subordinateId) {
        return employeeService.assignSubordinate(leaderId, subordinateId);
    }

    @DeleteMapping("/{leaderId}/subordinates/{subordinateId}")
    public ResponseEntity<Void> removeSubordinate(@PathVariable Long leaderId, @PathVariable Long subordinateId) {
        employeeService.removeSubordinate(leaderId, subordinateId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee saved = employeeService.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
