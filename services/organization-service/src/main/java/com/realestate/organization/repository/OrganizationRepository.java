package com.realestate.organization.repository;

import com.realestate.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);

    List<Organization> findByParentId(Long parentId);

    List<Organization> findByParentIsNull();

    @Query("SELECT o FROM Organization o WHERE o.active = true")
    List<Organization> findAllActive();

    @Query("SELECT o FROM Organization o JOIN o.users u WHERE u.userId = :userId AND u.active = true")
    List<Organization> findByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Organization o JOIN FETCH o.users WHERE o.id = :id")
    Optional<Organization> findByIdWithUsers(@Param("id") Long id);

    @Query("SELECT o FROM Organization o JOIN FETCH o.teams WHERE o.id = :id")
    Optional<Organization> findByIdWithTeams(@Param("id") Long id);
}

