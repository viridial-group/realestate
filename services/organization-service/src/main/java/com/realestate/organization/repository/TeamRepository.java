package com.realestate.organization.repository;

import com.realestate.organization.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByOrganizationId(Long organizationId);

    Optional<Team> findByNameAndOrganizationId(String name, Long organizationId);

    @Query("SELECT t FROM Team t WHERE t.organization.id = :organizationId AND t.active = true")
    List<Team> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT t FROM Team t JOIN FETCH t.members WHERE t.id = :id")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);
}

