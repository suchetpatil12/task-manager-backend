package com.example.taskmanager.taskmanager_backend.repository;

import com.example.taskmanager.taskmanager_backend.entity.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity>
    findTop10ByOrderByCreatedAtDesc();

}