package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.PagedResponse;
import com.example.taskmanager.taskmanager_backend.dto.ProjectResponse;
import com.example.taskmanager.taskmanager_backend.dto.UserResponse;

import com.example.taskmanager.taskmanager_backend.entity.Project;

import com.example.taskmanager.taskmanager_backend.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")

@CrossOrigin("*")

public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // =========================================
    // CREATE PROJECT
    // =========================================

    @PostMapping

    @PreAuthorize("hasRole('ADMIN')")

    public ProjectResponse createProject(

            @RequestBody Project project,

            @AuthenticationPrincipal
            UserDetails userDetails

    ) {

        return projectService.createProject(

                project,

                userDetails.getUsername()

        );
    }

    // =========================================
    // GET ALL PROJECTS WITH PAGINATION
    // =========================================

    @GetMapping

    public PagedResponse<ProjectResponse> getAllProjects(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size

    ) {

        return projectService.getAllProjects(
                page,
                size
        );
    }

    // =========================================
    // UPDATE PROJECT
    // =========================================

    @PutMapping("/{id}")

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@projectService.isOwner(#id, authentication.name)"
    )

    public ProjectResponse updateProject(

            @PathVariable Long id,

            @RequestBody Project updatedProject

    ) {

        return projectService.updateProject(
                id,
                updatedProject
        );
    }

    // =========================================
    // DELETE PROJECT
    // =========================================

    @DeleteMapping("/{id}")

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@projectService.isOwner(#id, authentication.name)"
    )

    public String deleteProject(
            @PathVariable Long id
    ) {

        return projectService.deleteProject(id);
    }

    // =========================================
    // GET PROJECT MEMBERS
    // =========================================

    @GetMapping("/{id}/members")

    public java.util.List<UserResponse> getProjectMembers(
            @PathVariable Long id
    ) {

        return projectService.getProjectMembers(id);
    }

    // =========================================
    // ADD MEMBER
    // =========================================

    @PostMapping("/{projectId}/members/{userId}")

    @PreAuthorize("hasRole('ADMIN')")

    public ProjectResponse addMember(

            @PathVariable Long projectId,

            @PathVariable Long userId

    ) {

        return projectService.addMember(
                projectId,
                userId
        );
    }

    // =========================================
    // REMOVE MEMBER
    // =========================================

    @DeleteMapping(
            "/{projectId}/members/{userId}"
    )

    @PreAuthorize("hasRole('ADMIN')")

    public ProjectResponse removeMember(

            @PathVariable Long projectId,

            @PathVariable Long userId

    ) {

        return projectService.removeMember(
                projectId,
                userId
        );
    }

    // =========================================
    // GET MEMBERS BY DESIGNATION
    // =========================================

    @GetMapping(
            "/{projectId}/members/by-designation"
    )

    public java.util.List<UserResponse> getMembersByDesignation(

            @PathVariable Long projectId,

            @RequestParam String designation

    ) {

        return projectService.getMembersByDesignation(

                projectId,

                designation

        );
    }

    // =========================================
// SEARCH PROJECTS
// =========================================

    @GetMapping("/search")
    public Page<ProjectResponse> searchProjects(

            @RequestParam(defaultValue = "")
            String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size

    ) {

        return projectService.searchProjects(

                keyword,

                page,

                size

        );

    }
}