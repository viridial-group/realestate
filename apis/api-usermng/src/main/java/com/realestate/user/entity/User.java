package com.realestate.user.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import com.realestate.user.organization.entity.Organization;
import com.realestate.user.role.entity.Role;

@Entity
@Table(name = "users", indexes = {
    @jakarta.persistence.Index(name = "idx_user_organization_id", columnList = "organization_id"),
    @jakarta.persistence.Index(name = "idx_user_email", columnList = "email")
})
@Getter 
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "fr"; // Par défaut: français

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @jakarta.persistence.Version
    @Column(nullable = false)
    private Long version;

    // Plusieurs rôles
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @org.hibernate.annotations.BatchSize(size = 20)
    private Set<Role> roles = new HashSet<>();
}

