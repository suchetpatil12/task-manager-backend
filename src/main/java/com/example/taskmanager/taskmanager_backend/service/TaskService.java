package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.TaskRequest;
import com.example.taskmanager.taskmanager_backend.dto.TaskResponse;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import com.example.taskmanager.taskmanager_backend.entity.Status;
import com.example.taskmanager.taskmanager_backend.entity.Task;
import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.repository.ProjectRepository;
import com.example.taskmanager.taskmanager_backend.repository.TaskRepository;
import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import com.example.taskmanager.taskmanager_backend.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // CREATE TASK
    // =========================

    public TaskResponse createTask(TaskRequest request) {

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );

        User assignedUser = userRepository
                .findById(request.getAssignedUserId())
                .orElseThrow(() ->
                        new RuntimeException("Assigned user not found")
                );

        Task task = new Task();

        task.setTitle(request.getTitle());

        task.setDescription(request.getDescription());

        task.setDueDate(request.getDueDate());

        task.setStatus(request.getStatus());

        // ✅ THIS WAS MISSING
        task.setPriority(request.getPriority());

        task.setProject(project);

        task.setAssignedTo(assignedUser);

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    // =========================
    // GET ALL TASKS
    // =========================

    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()

                .stream()

                .map(this::mapToResponse)

                .toList();
    }

    // =========================
    // GET TASK BY ID
    // =========================

    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        return mapToResponse(task);
    }

    // =========================
    // UPDATE TASK
    // =========================

    public TaskResponse updateTask(
            Long id,
            TaskRequest request
    ) {

        Task task = taskRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );

        User assignedUser = userRepository
                .findById(request.getAssignedUserId())
                .orElseThrow(() ->
                        new RuntimeException("Assigned user not found")
                );

        task.setTitle(request.getTitle());

        task.setDescription(request.getDescription());

        task.setDueDate(request.getDueDate());

        task.setStatus(request.getStatus());

        // ✅ THIS WAS MISSING
        task.setPriority(request.getPriority());

        task.setProject(project);

        task.setAssignedTo(assignedUser);

        Task updatedTask =
                taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    // =========================
    // DELETE TASK
    // =========================

    public void deleteTask(Long id) {

        taskRepository.deleteById(id);
    }

    // =========================
    // SECURITY CHECK
    // =========================

    public boolean isAssigned(
            Long taskId,
            String email
    ) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        return task.getAssignedTo() != null
                &&
                task.getAssignedTo()
                        .getEmail()
                        .equals(email);
    }

    public boolean isProjectOwner(
            Long taskId,
            String email
    ) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        return task.getProject()
                .getCreatedBy()
                .getEmail()
                .equals(email);
    }

    // =========================
    // GET TASKS BY PROJECT
    // =========================

    public Page<TaskResponse> getTasksByProject(
            Long projectId,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        String email =
                SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Page<Task> tasks;

        // ADMIN

        if ("ADMIN".equals(currentUser.getRole().name())) {

            tasks =
                    taskRepository.findByProjectId(
                            projectId,
                            pageable
                    );

        }

        // MEMBER

        else {

            tasks =
                    taskRepository.findTasksForUser(
                            projectId,
                            currentUser.getId(),
                            pageable
                    );
        }

        return tasks.map(this::mapToResponse);
    }

    // =========================
    // SEARCH TASKS
    // =========================

    public Page<TaskResponse> searchTasks(

            Long projectId,

            String keyword,

            String status,

            int page,

            int size

    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Task> tasks;

        // ✅ BOTH KEYWORD + STATUS

        if (keyword != null
                && !keyword.isEmpty()
                && status != null
                && !status.isEmpty()) {

            tasks =
                    taskRepository
                            .findByProjectIdAndTitleContainingIgnoreCaseAndStatus(
                                    projectId,
                                    keyword,
                                    Status.valueOf(status),
                                    pageable
                            );
        }

        // ✅ ONLY STATUS

        else if (status != null
                && !status.isEmpty()) {

            tasks =
                    taskRepository
                            .findByProjectIdAndStatus(
                                    projectId,
                                    Status.valueOf(status),
                                    pageable
                            );
        }

        // ✅ ONLY KEYWORD

        else if (keyword != null
                && !keyword.isEmpty()) {

            tasks =
                    taskRepository
                            .findByProjectIdAndTitleContainingIgnoreCase(
                                    projectId,
                                    keyword,
                                    pageable
                            );
        }

        // ✅ NO FILTER

        else {

            tasks =
                    taskRepository
                            .findByProjectId(
                                    projectId,
                                    pageable
                            );
        }

        return tasks.map(this::mapToResponse);
    }

    // =========================
    // DTO MAPPER
    // =========================

    private TaskResponse mapToResponse(Task task) {

        return TaskResponse.builder()

                .id(task.getId())

                .title(task.getTitle())

                .description(task.getDescription())

                .dueDate(task.getDueDate())

                .status(task.getStatus())

                .priority(task.getPriority())

                .projectId(task.getProject().getId())

                .projectName(task.getProject().getName())

                .assignedTo(task.getAssignedTo().getName())

                .assignedUserId(task.getAssignedTo().getId())

                .assignedUserDesignation(
                        task.getAssignedTo()
                                .getDesignation()
                                .name()
                )

                .build();
    }
}