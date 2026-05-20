package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.ActivityResponse;

import com.example.taskmanager.taskmanager_backend.entity.Activity;

import com.example.taskmanager.taskmanager_backend.repository.ActivityRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    // =========================================
    // SAVE ACTIVITY
    // =========================================

    public void saveActivity(

            String icon,

            String action,

            String targetName,

            String username

    ) {

        Activity activity = Activity.builder()

                .icon(icon)

                .action(action)

                .targetName(targetName)

                .username(username)

                .createdAt(LocalDateTime.now())

                .build();

        activityRepository.save(activity);

    }

    // =========================================
    // GET RECENT ACTIVITIES
    // =========================================

    public List<ActivityResponse> getRecentActivities() {

        return activityRepository

                .findTop10ByOrderByCreatedAtDesc()

                .stream()

                .map(this::mapToResponse)

                .toList();
    }

    // =========================================
    // DTO MAPPER
    // =========================================

    private ActivityResponse mapToResponse(
            Activity activity
    ) {

        return ActivityResponse.builder()

                .icon(activity.getIcon())

                .action(activity.getAction())

                .targetName(activity.getTargetName())

                .username(activity.getUsername())

                .time(getTimeAgo(
                        activity.getCreatedAt()
                ))

                .build();
    }

    // =========================================
    // TIME AGO
    // =========================================

    private String getTimeAgo(
            LocalDateTime time
    ) {

        long minutes =
                Duration.between(
                        time,
                        LocalDateTime.now()
                ).toMinutes();

        if (minutes < 1) {

            return "Just now";

        }

        if (minutes < 60) {

            return minutes + " mins ago";

        }

        long hours = minutes / 60;

        if (hours < 24) {

            return hours + " hours ago";

        }

        long days = hours / 24;

        return days + " days ago";
    }
}