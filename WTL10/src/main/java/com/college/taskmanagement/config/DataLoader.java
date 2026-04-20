package com.college.taskmanagement.config;

import com.college.taskmanagement.model.Employee;
import com.college.taskmanagement.model.Task;
import com.college.taskmanagement.model.TaskStatus;
import com.college.taskmanagement.repository.EmployeeRepository;
import com.college.taskmanagement.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, TaskRepository taskRepository) {
        return args -> {
            if (employeeRepository.count() > 0 || taskRepository.count() > 0) {
                return;
            }

            Employee leader = employeeRepository.save(
                    new Employee("Dr. Priya Singh", "priya.singh@college.edu", "Computer Science", "Team Leader"));
            Employee teamMemberOne = new Employee("Anita Verma", "anita.verma@college.edu", "Computer Science", "Assistant Professor");
            teamMemberOne.setManager(leader);
            teamMemberOne = employeeRepository.save(teamMemberOne);

            Employee teamMemberTwo = new Employee("Rohan Mehta", "rohan.mehta@college.edu", "Computer Science", "Lab Coordinator");
            teamMemberTwo.setManager(leader);
            teamMemberTwo = employeeRepository.save(teamMemberTwo);

            Task reviewSyllabus = new Task(
                    "Review Syllabus",
                    "Prepare the syllabus revision proposal for the next term.",
                    LocalDate.now().plusDays(7),
                    TaskStatus.PENDING);
            reviewSyllabus.setAssignedEmployee(teamMemberOne);
            reviewSyllabus.setAssignedByLeader(leader);

            Task labInventory = new Task(
                    "Lab Inventory Update",
                    "Audit the lab equipment and update the stock register.",
                    LocalDate.now().plusDays(10),
                    TaskStatus.IN_PROGRESS);
            labInventory.setAssignedEmployee(teamMemberTwo);
            labInventory.setAssignedByLeader(leader);

            taskRepository.save(reviewSyllabus);
            taskRepository.save(labInventory);
        };
    }
}
