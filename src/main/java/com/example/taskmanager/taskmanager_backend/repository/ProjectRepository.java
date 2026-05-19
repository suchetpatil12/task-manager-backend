package com.example.taskmanager.taskmanager_backend.repository;

import com.example.taskmanager.taskmanager_backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // =========================================
// SEARCH PROJECTS
// =========================================

    Page<Project> findByNameContainingIgnoreCase(

            String keyword,

            Pageable pageable

    );
}