package com.example.taskmanager.taskmanager_backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {

    private long totalProjects;

    private long totalTasks;

    private long completedTasks;

    private long pendingTasks;

    private long inProgressTasks;

}