package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.PagedResponse;
import com.example.taskmanager.taskmanager_backend.dto.ProjectResponse;
import com.example.taskmanager.taskmanager_backend.dto.UserResponse;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.repository.ProjectRepository;
import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================================
    // CREATE PROJECT
    // =========================================

    public ProjectResponse createProject(

            Project project,

            String email

    ) {

        User user =
                userRepository.findByEmail(email)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        // SET OWNER

        project.setCreatedBy(user);

        // ADD OWNER AS MEMBER

        project.getMembers().add(user);

        Project savedProject =
                projectRepository.save(project);

        return mapToProjectResponse(savedProject);
    }

    // =========================================
    // GET ALL PROJECTS
    // =========================================

    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> getAllProjects(

            int page,

            int size

    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Project> projects =
                projectRepository.findAll(pageable);

        return PagedResponse.<ProjectResponse>builder()

                .content(

                        projects.getContent()

                                .stream()

                                .map(this::mapToProjectResponse)

                                .toList()

                )

                .page(projects.getNumber())

                .size(projects.getSize())

                .totalElements(
                        projects.getTotalElements()
                )

                .totalPages(
                        projects.getTotalPages()
                )

                .last(projects.isLast())

                .build();
    }

    // =========================================
// SEARCH PROJECTS
// =========================================

    @Transactional(readOnly = true)
    public Page<ProjectResponse> searchProjects(

            String keyword,

            int page,

            int size

    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Project> projects;

        if (keyword != null && !keyword.isEmpty()) {

            projects =
                    projectRepository
                            .findByNameContainingIgnoreCase(
                                    keyword,
                                    pageable
                            );

        }

        else {

            projects =
                    projectRepository.findAll(
                            pageable
                    );

        }

        return projects.map(this::mapToResponse);

    }

    // =========================================
    // UPDATE PROJECT
    // =========================================

    public ProjectResponse updateProject(

            Long id,

            Project updatedProject

    ) {

        Project project =
                projectRepository.findById(id)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        project.setName(updatedProject.getName());

        project.setDescription(
                updatedProject.getDescription()
        );

        Project savedProject =
                projectRepository.save(project);

        return mapToProjectResponse(savedProject);
    }

    // =========================================
    // DELETE PROJECT
    // =========================================

    public String deleteProject(Long id) {

        Project project =
                projectRepository.findById(id)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        projectRepository.delete(project);

        return "Project deleted successfully";
    }

    // =========================================
    // GET PROJECT MEMBERS
    // =========================================

    @Transactional(readOnly = true)
    public List<UserResponse> getProjectMembers(
            Long id
    ) {

        Project project =
                projectRepository.findById(id)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        return project.getMembers()

                .stream()

                .map(this::mapToUserResponse)

                .toList();
    }

    // =========================================
    // ADD MEMBER
    // =========================================

    public ProjectResponse addMember(

            Long projectId,

            Long userId

    ) {

        Project project =
                projectRepository.findById(projectId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        User user =
                userRepository.findById(userId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        boolean alreadyExists =

                project.getMembers()

                        .stream()

                        .anyMatch(member ->

                                member.getId().equals(userId)
                        );

        if (!alreadyExists) {

            project.getMembers().add(user);
        }

        Project savedProject =
                projectRepository.save(project);

        return mapToProjectResponse(savedProject);
    }

    // =========================================
    // REMOVE MEMBER
    // =========================================

    public ProjectResponse removeMember(

            Long projectId,

            Long userId

    ) {

        Project project =
                projectRepository.findById(projectId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        project.getMembers()

                .removeIf(member ->

                        member.getId().equals(userId)
                );

        Project savedProject =
                projectRepository.save(project);

        return mapToProjectResponse(savedProject);
    }

    // =========================================
    // GET MEMBERS BY DESIGNATION
    // =========================================

    @Transactional(readOnly = true)
    public List<UserResponse> getMembersByDesignation(

            Long projectId,

            String designation

    ) {

        Project project =
                projectRepository.findById(projectId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        return project.getMembers()

                .stream()

                .filter(user ->

                        user.getDesignation()

                                .equalsIgnoreCase(designation)
                )

                .map(this::mapToUserResponse)

                .toList();
    }

    // =========================================
    // CHECK PROJECT OWNER
    // =========================================

    @Transactional(readOnly = true)
    public boolean isOwner(

            Long projectId,

            String email

    ) {

        Project project =
                projectRepository.findById(projectId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                )
                        );

        return project

                .getCreatedBy()

                .getEmail()

                .equals(email);
    }

    // =========================================
    // PROJECT DTO MAPPER
    // =========================================

    private ProjectResponse mapToProjectResponse(
            Project project
    ) {

        return ProjectResponse.builder()

                .id(project.getId())

                .name(project.getName())

                .description(project.getDescription())

                .createdBy(
                        project.getCreatedBy().getName()
                )

                .totalMembers(
                        project.getMembers().size()
                )

                .build();
    }

    // =========================================
    // USER DTO MAPPER
    // =========================================

    private UserResponse mapToUserResponse(
            User user
    ) {

        return UserResponse.builder()

                .id(user.getId())

                .name(user.getName())

                .email(user.getEmail())

                .designation(
                        user.getDesignation()
                )

                .build();
    }

    // =========================================
// DTO MAPPER
// =========================================

    private ProjectResponse mapToResponse(
            Project project
    ) {

        return ProjectResponse.builder()

                .id(project.getId())

                .name(project.getName())

                .description(project.getDescription())

                .createdBy(

                        project.getCreatedBy() != null

                                ?

                                project.getCreatedBy().getName()

                                :

                                null

                )

                .totalMembers(

                        project.getMembers() != null

                                ?

                                project.getMembers().size()

                                :

                                0

                )

                .build();

    }
}