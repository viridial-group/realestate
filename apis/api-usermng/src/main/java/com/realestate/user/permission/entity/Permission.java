package com.realestate.user.permission.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import com.realestate.user.organization.entity.Organization;

@Entity
@Table(name = "permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code", "organization_id"}))
@Getter 
@Setter
public class Permission {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}

