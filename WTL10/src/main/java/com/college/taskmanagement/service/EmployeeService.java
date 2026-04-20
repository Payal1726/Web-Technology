package com.college.taskmanagement.service;

import com.college.taskmanagement.exception.ResourceNotFoundException;
import com.college.taskmanagement.model.Employee;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.repository.EmployeeRepository;
import com.college.taskmanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;

    public EmployeeService(EmployeeRepository employeeRepository, TaskRepository taskRepository) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee update(Long id, Employee details) {
        Employee employee = findById(id);
        employee.setName(details.getName());
        employee.setEmail(details.getEmail());
        employee.setDepartment(details.getDepartment());
        employee.setDesignation(details.getDesignation());
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        Employee employee = findById(id);
        if (employee.getManager() != null) {
            employee.getManager().getSubordinates().remove(employee);
            employee.setManager(null);
        }

        for (Employee subordinate : new ArrayList<>(employee.getSubordinates())) {
            subordinate.setManager(null);
        }
        employee.getSubordinates().clear();

        List<Task> assignedTasks = taskRepository.findByAssignedEmployeeId(id);
        for (Task task : assignedTasks) {
            task.setAssignedEmployee(null);
        }

        List<Task> leaderTasks = taskRepository.findByAssignedByLeaderIdOrderByDueDateAscIdAsc(id);
        for (Task task : leaderTasks) {
            task.setAssignedByLeader(null);
        }

        employeeRepository.delete(employee);
    }

    public List<Task> findTasks(Long employeeId) {
        Employee employee = findById(employeeId);
        return employee.getTasks();
    }

    public List<Employee> getSubordinates(Long leaderId) {
        Employee leader = findById(leaderId);
        return leader.getSubordinates();
    }

    public Employee assignSubordinate(Long leaderId, Long subordinateId) {
        Employee leader = findById(leaderId);
        Employee subordinate = findById(subordinateId);
        if (leader.getId().equals(subordinate.getId())) {
            throw new IllegalArgumentException("An employee cannot be their own leader");
        }
        if (createsCycle(leader, subordinate)) {
            throw new IllegalArgumentException("This assignment would create a circular reporting structure");
        }
        if (subordinate.getManager() != null && subordinate.getManager().getId().equals(leaderId)) {
            return subordinate;
        }
        if (subordinate.getManager() != null) {
            subordinate.getManager().getSubordinates().remove(subordinate);
        }
        subordinate.setManager(leader);
        return employeeRepository.save(subordinate);
    }

    public void removeSubordinate(Long leaderId, Long subordinateId) {
        Employee leader = findById(leaderId);
        Employee subordinate = findById(subordinateId);
        if (subordinate.getManager() == null || !subordinate.getManager().getId().equals(leaderId)) {
            throw new IllegalArgumentException("Subordinate is not assigned to this leader");
        }
        leader.getSubordinates().remove(subordinate);
        subordinate.setManager(null);
        employeeRepository.save(subordinate);
    }

    private boolean createsCycle(Employee leader, Employee subordinate) {
        Employee current = leader;
        while (current != null) {
            if (current.getId().equals(subordinate.getId())) {
                return true;
            }
            current = current.getManager();
        }
        return false;
    }
}
