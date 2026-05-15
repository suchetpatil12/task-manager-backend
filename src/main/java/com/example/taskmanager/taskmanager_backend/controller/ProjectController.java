package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import com.example.taskmanager.taskmanager_backend.entity.User;
import com.example.taskmanager.taskmanager_backend.repository.ProjectRepository;
import com.example.taskmanager.taskmanager_backend.repository.UserRepository;
import com.example.taskmanager.taskmanager_backend.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    // ✅ CREATE PROJECT (ADMIN ONLY)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Project createProject(@RequestBody Project project,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        // 🔥 Get logged-in user
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 Set owner from JWT (NOT from request)
        project.setCreatedBy(user);

        return projectRepository.save(project);
    }

    // ✅ GET ALL PROJECTS (ANY LOGGED-IN USER)
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // ✅ UPDATE PROJECT (OWNER OR ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @projectService.isOwner(#id, authentication.name)")
    public Project updateProject(@PathVariable Long id,
                                 @RequestBody Project updatedProject) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());

        return projectRepository.save(project);
    }

    // ✅ DELETE PROJECT (OWNER OR ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @projectService.isOwner(#id, authentication.name)")
    public String deleteProject(@PathVariable Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);

        return "Project deleted successfully";
    }
}