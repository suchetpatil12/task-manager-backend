package com.example.taskmanager.taskmanager_backend.repository;

import com.example.taskmanager.taskmanager_backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    // =========================================
    // FIND BY EMAIL
    // =========================================

    Optional<User> findByEmail(String email);

    // =========================================
    // FIND BY DESIGNATION
    // =========================================

    List<User> findByDesignation(
            String designation
    );

    // =========================================
    // DISTINCT DESIGNATIONS
    // =========================================

    @Query(
            "SELECT DISTINCT u.designation FROM User u"
    )

    List<String> findDistinctDesignations();

}