package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.DashboardResponse;
import com.example.taskmanager.taskmanager_backend.entity.Status;
import com.example.taskmanager.taskmanager_backend.repository.ProjectRepository;
import com.example.taskmanager.taskmanager_backend.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    public DashboardResponse getDashboardData() {

        long totalProjects =
                projectRepository.count();

        long totalTasks =
                taskRepository.count();

        long completedTasks =
                taskRepository.countByStatus(
                        Status.DONE
                );

        long pendingTasks =
                taskRepository.countByStatus(
                        Status.TODO
                );

        long inProgressTasks =
                taskRepository.countByStatus(
                        Status.IN_PROGRESS
                );

        return DashboardResponse.builder()

                .totalProjects(totalProjects)

                .totalTasks(totalTasks)

                .completedTasks(completedTasks)

                .pendingTasks(pendingTasks)

                .inProgressTasks(inProgressTasks)

                .build();

    }

}