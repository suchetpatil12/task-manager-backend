package com.example.taskmanager.taskmanager_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponse {

    private Long id;

    private String name;

    private String description;

    private String createdBy;

    private int totalMembers;
}