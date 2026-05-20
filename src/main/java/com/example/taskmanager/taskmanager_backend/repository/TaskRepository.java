package com.example.taskmanager.taskmanager_backend.repository;

import com.example.taskmanager.taskmanager_backend.entity.Status;
import com.example.taskmanager.taskmanager_backend.entity.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    // =========================================
    // GET TASKS BY PROJECT
    // =========================================

    Page<Task> findByProjectId(
            Long projectId,
            Pageable pageable
    );

    // =========================================
    // MEMBER TASKS
    // =========================================

    @Query("""

        SELECT t FROM Task t

        WHERE t.project.id = :projectId

        AND (

            t.assignedTo.id = :userId

            OR t.project.createdBy.id = :userId

        )

    """)
    Page<Task> findTasksForUser(

            @Param("projectId")
            Long projectId,

            @Param("userId")
            Long userId,

            Pageable pageable

    );

    // =========================================
    // DASHBOARD COUNTS
    // =========================================

    long countByStatus(
            Status status
    );

    long countByProjectId(
            Long projectId
    );

    long countByProjectIdAndStatus(

            Long projectId,

            Status status

    );

    // =========================================
    // SEARCH TASKS
    // =========================================

    Page<Task> findByProjectIdAndTitleContainingIgnoreCase(

            Long projectId,

            String title,

            Pageable pageable

    );

    Page<Task> findByProjectIdAndStatus(

            Long projectId,

            Status status,

            Pageable pageable

    );

    Page<Task> findByProjectIdAndTitleContainingIgnoreCaseAndStatus(

            Long projectId,

            String title,

            Status status,

            Pageable pageable

    );
}