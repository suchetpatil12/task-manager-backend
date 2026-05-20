package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.PagedResponse;
import com.example.taskmanager.taskmanager_backend.dto.ProjectResponse;
import com.example.taskmanager.taskmanager_backend.dto.UserResponse;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import com.example.taskmanager.taskmanager_backend.entity.Status;
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

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ActivityService activityService;

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

        project.setCreatedBy(user);

        project.getMembers().add(user);

        Project savedProject =
                projectRepository.save(project);

        // SAVE ACTIVITY

        activityService.saveActivity(

                "📁",

                "Created Project",

                savedProject.getName(),

                email

        );

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

        return projects.map(this::mapToProjectResponse);

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

        // SAVE ACTIVITY

        activityService.saveActivity(

                "✏️",

                "Updated Project",

                savedProject.getName(),

                SecurityUtil.getCurrentUserEmail()

        );

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

        String projectName = project.getName();

        projectRepository.delete(project);

        // SAVE ACTIVITY

        activityService.saveActivity(

                "🗑️",

                "Deleted Project",

                projectName,

                SecurityUtil.getCurrentUserEmail()

        );

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

        // SAVE ACTIVITY

        activityService.saveActivity(

                "👥",

                "Added Member",

                user.getName(),

                SecurityUtil.getCurrentUserEmail()

        );

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
    // PROJECT DTO MAPPER
    // =========================================

    private ProjectResponse mapToProjectResponse(
            Project project
    ) {

        long totalTasks =
                taskRepository.countByProjectId(
                        project.getId()
                );

        long completedTasks =
                taskRepository.countByProjectIdAndStatus(
                        project.getId(),
                        Status.DONE
                );

        double progressPercentage =
                totalTasks == 0

                        ? 0

                        : ((double) completedTasks
                        / totalTasks) * 100;

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

                .totalTasks(totalTasks)

                .completedTasks(completedTasks)

                .progressPercentage(progressPercentage)

                .build();
    }
}