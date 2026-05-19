package com.example.taskmanager.taskmanager_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // =========================================
    // PROJECT OWNER
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(
            name = "created_by",
            nullable = false
    )

    @JsonIgnoreProperties({
            "password",
            "email",
            "hibernateLazyInitializer",
            "handler"
    })

    private User createdBy;

    // =========================================
    // PROJECT TASKS
    // =========================================

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @JsonIgnore
    private List<Task> tasks;

    // =========================================
    // PROJECT MEMBERS
    // =========================================

    @ManyToMany

    @JoinTable(

            name = "project_members",

            joinColumns =
            @JoinColumn(name = "project_id"),

            inverseJoinColumns =
            @JoinColumn(name = "user_id")

    )

    @JsonIgnoreProperties({
            "password",
            "hibernateLazyInitializer",
            "handler"
    })

    private List<User> members = new ArrayList<>();

}