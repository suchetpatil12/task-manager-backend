package com.example.taskmanager.taskmanager_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name = "users")

@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})

public class User {

    @Id

    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )

    private Long id;

    // =========================================
    // NAME
    // =========================================

    @Column(nullable = false)

    private String name;

    // =========================================
    // EMAIL
    // =========================================

    @Column(
            nullable = false,
            unique = true
    )

    private String email;

    // =========================================
    // PASSWORD
    // =========================================

    @JsonProperty(
            access =
                    JsonProperty.Access.WRITE_ONLY
    )

    @Column(nullable = false)

    private String password;

    // =========================================
    // ROLE
    // =========================================

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)

    private Role role;

    // =========================================
    // DESIGNATION
    // =========================================

    @Column(nullable = false)

    private String designation;

}