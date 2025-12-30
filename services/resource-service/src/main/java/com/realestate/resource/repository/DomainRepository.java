package com.realestate.resource.repository;

import com.realestate.resource.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {

    Optional<Domain> findByName(String name);

    @Query("SELECT d FROM Domain d WHERE d.active = true")
    List<Domain> findAllActive();

    @Query("SELECT d FROM Domain d JOIN FETCH d.resources WHERE d.id = :id")
    Optional<Domain> findByIdWithResources(@Param("id") Long id);
}

