package com.example.taskmanager.taskmanager_backend.dto;

import com.example.taskmanager.taskmanager_backend.entity.Priority;
import com.example.taskmanager.taskmanager_backend.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    private Status status;

    private Long projectId;

    private String projectName;

    private String assignedTo;

    private Long assignedUserId;

    private String assignedUserDesignation;
    private Priority priority;
}