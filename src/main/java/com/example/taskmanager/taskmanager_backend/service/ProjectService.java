package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import com.example.taskmanager.taskmanager_backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // 🔥 Check if logged-in user is owner
    public boolean isOwner(Long projectId, String email) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return project.getCreatedBy().getEmail().equals(email);
    }
}