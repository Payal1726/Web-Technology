# Employee Task Management System

A Spring Boot application to manage faculty employees and the tasks assigned to them in a college engineering department.

## Features

- Employee CRUD operations
- Task CRUD operations
- Task assignment to employees
- Task tracking with status and due dates
- Leader to team-member assignment workflow
- Leader-specific task CRUD endpoints
- H2 database file stored in the project workspace for persistence
- REST API for easy integration
- Leader/subordinate relationships with safe reassignment behavior

## Run

1. Build:
   ```bash
   mvn clean package
   ```
2. Run:
   ```bash
   mvn spring-boot:run
   ```
3. API base URL:
   `http://localhost:8080/api`

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/taskdb`

## Leader Task CRUD

- `GET /api/leaders/{leaderId}/tasks`
- `GET /api/leaders/{leaderId}/tasks/{taskId}`
- `POST /api/leaders/{leaderId}/tasks`
- `PUT /api/leaders/{leaderId}/tasks/{taskId}`
- `DELETE /api/leaders/{leaderId}/tasks/{taskId}`

Example request body:

```json
{
  "title": "Prepare Weekly Report",
  "description": "Collect updates and send the report before Friday.",
  "dueDate": "2026-04-24",
  "status": "PENDING",
  "subordinateId": 2
}
```
