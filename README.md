# Enterprise Task Manager Backend

Spring Boot backend for the Enterprise Task Management System.

Provides secure REST APIs for authentication, project management, task management, analytics, and role-based access control.

---

## Features

- JWT Authentication
- Spring Security
- Role Based Authorization
- REST APIs
- Project Management APIs
- Task Management APIs
- Task Priority System
- Search & Filtering
- Pagination
- Dashboard Analytics APIs
- Designation Based User Assignment
- Global Exception Handling
- Secure API Access

---

## Tech Stack

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT Authentication
- Maven

### Database
- MySQL

---

## Modules

### Authentication Module
- Login API
- Register API
- JWT Token Generation

### Project Module
- Create Project
- Update Project
- Delete Project
- Project Listing

### Task Module
- Create Task
- Update Task
- Delete Task
- Status Management
- Priority Management
- Search & Filtering
- Pagination

### Dashboard Module
- Analytics APIs
- Task Statistics
- Project Statistics

---

## API Security

Protected APIs secured using:

- JWT Token Authentication
- Spring Security
- Role Based Authorization

---

## Installation

### Clone Repository

```bash
git clone <BACKEND_REPO_URL>
```

---

## Configure Database

Update:

```properties
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## Run Application

```bash
mvn spring-boot:run
```

Backend will run on:

```text
http://localhost:8080
```

---

## API Base URL

```text
http://localhost:8080
```

---

## Database

Database used:

```text
MySQL
```

---

## Future Enhancements

- Docker Support
- WebSocket Notifications
- Audit Logs
- Email Notifications
- File Upload Support
- Kubernetes Deployment

---

## Author

Suchet Patil
