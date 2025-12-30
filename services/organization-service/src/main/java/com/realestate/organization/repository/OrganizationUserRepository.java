package com.realestate.organization.repository;

import com.realestate.organization.entity.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {

    Optional<OrganizationUser> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    List<OrganizationUser> findByUserId(Long userId);

    List<OrganizationUser> findByOrganizationId(Long organizationId);

    List<OrganizationUser> findByTeamId(Long teamId);

    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.userId = :userId AND ou.organization.id = :organizationId AND ou.active = true")
    Optional<OrganizationUser> findActiveByUserIdAndOrganizationId(
            @Param("userId") Long userId,
            @Param("organizationId") Long organizationId
    );

    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.organization.id = :organizationId AND ou.active = true")
    List<OrganizationUser> findActiveByOrganizationId(@Param("organizationId") Long organizationId);
}

