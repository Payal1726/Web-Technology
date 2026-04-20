package com.college.taskmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    private String department;
    private String designation;

    @JsonBackReference(value = "manager-subordinates")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @JsonManagedReference(value = "manager-subordinates")
    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates = new ArrayList<>();

    @JsonManagedReference(value = "employee-tasks")
    @OneToMany(mappedBy = "assignedEmployee")
    private List<Task> tasks = new ArrayList<>();

    public Employee() {
    }

    public Employee(String name, String email, String department, String designation) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setAssignedEmployee(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setAssignedEmployee(null);
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    public void addSubordinate(Employee subordinate) {
        subordinates.add(subordinate);
        subordinate.setManager(this);
    }

    public void removeSubordinate(Employee subordinate) {
        subordinates.remove(subordinate);
        subordinate.setManager(null);
    }
}
