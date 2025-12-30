package com.realestate.billing.repository;

import com.realestate.billing.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);

    List<Plan> findByActiveTrue();

    Optional<Plan> findByIsDefaultTrue();

    List<Plan> findByActiveTrueOrderByPriceAsc();
}

