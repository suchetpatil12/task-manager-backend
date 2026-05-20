package com.example.taskmanager.taskmanager_backend.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@Builder

public class Activity {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String icon;

    private String action;

    private String targetName;

    private String username;

    private LocalDateTime createdAt;

}