package com.example.taskmanager.taskmanager_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class ActivityResponse {

    private String icon;

    private String action;

    private String targetName;

    private String username;

    private String time;

}