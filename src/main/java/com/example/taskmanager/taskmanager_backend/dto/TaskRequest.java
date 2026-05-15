package com.example.taskmanager.taskmanager_backend.dto;

import com.example.taskmanager.taskmanager_backend.entity.Priority;
import com.example.taskmanager.taskmanager_backend.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;
    private Long projectId;
    private Long assignedUserId;
    private Priority priority;
}