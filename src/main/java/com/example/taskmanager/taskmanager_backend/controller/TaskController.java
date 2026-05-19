package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.PagedResponse;
import com.example.taskmanager.taskmanager_backend.dto.TaskRequest;
import com.example.taskmanager.taskmanager_backend.dto.TaskResponse;
import com.example.taskmanager.taskmanager_backend.entity.Status;
import com.example.taskmanager.taskmanager_backend.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/tasks")
//@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ✅ CREATE TASK
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@projectService.isOwner(#request.projectId, authentication.name)"
    )
    @PostMapping
    public TaskResponse createTask(
            @RequestBody TaskRequest request
    ) {

        return taskService.createTask(request);

    }

    // ✅ GET ALL TASKS
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @GetMapping
    public List<TaskResponse> getAllTasks() {

        return taskService.getAllTasks();

    }

    // ✅ GET TASK BY ID
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id
    ) {

        return taskService.getTaskById(id);

    }

    @GetMapping("/statuses")
    public List<String> getStatuses() {

        return Arrays.stream(Status.values())
                .map(Enum::name)
                .toList();
    }

    // ✅ UPDATE TASK
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@taskService.isAssigned(#id, authentication.name) or " +
                    "@taskService.isProjectOwner(#id, authentication.name)"
    )
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request
    ) {

        return taskService.updateTask(id, request);

    }

    // ✅ DELETE TASK
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@taskService.isProjectOwner(#id, authentication.name)"
    )
    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id
    ) {

        taskService.deleteTask(id);

        return "Task deleted successfully";

    }

    // ✅ GET TASKS BY PROJECT WITH PAGINATION
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @GetMapping("/project/{projectId}")
    public PagedResponse<TaskResponse> getTasksByProject(

            @PathVariable Long projectId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size

    ) {

        return taskService.getTasksByProject(
                projectId,
                page,
                size
        );

    }

    @GetMapping("/search/{projectId}")

    public PagedResponse<TaskResponse> searchTasks(

            @PathVariable Long projectId,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        return taskService.searchTasks(

                projectId,

                keyword,

                status,

                page,

                size
        );
    }

}