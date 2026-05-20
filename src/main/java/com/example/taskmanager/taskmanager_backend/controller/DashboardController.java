package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.ActivityResponse;
import com.example.taskmanager.taskmanager_backend.dto.DashboardResponse;

import com.example.taskmanager.taskmanager_backend.service.ActivityService;
import com.example.taskmanager.taskmanager_backend.service.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")

public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ActivityService activityService;

    // =========================================
    // DASHBOARD DATA
    // =========================================

    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")

    @GetMapping

    public DashboardResponse getDashboardData() {

        return dashboardService.getDashboardData();
    }

    // =========================================
    // RECENT ACTIVITIES
    // =========================================

    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")

    @GetMapping("/activities")

    public List<ActivityResponse> getRecentActivities() {

        return activityService.getRecentActivities();
    }

}