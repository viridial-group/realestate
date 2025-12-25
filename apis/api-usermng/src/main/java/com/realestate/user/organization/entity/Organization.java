package com.realestate.user.organization.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "organizations", indexes = {
    @jakarta.persistence.Index(name = "idx_organization_parent_id", columnList = "parent_id")
})
@Getter 
@Setter
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @jakarta.persistence.Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 50)
    private Set<Organization> children = new HashSet<>();
}

