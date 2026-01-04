package com.realestate.property.repository;

import com.realestate.property.entity.DVFImportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DVFImportHistoryRepository extends JpaRepository<DVFImportHistory, Long> {

    Page<DVFImportHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<DVFImportHistory> findByYearAndDepartment(String year, String department);

    List<DVFImportHistory> findByStatus(String status);

    @Query("SELECT COUNT(DISTINCT h.department) FROM DVFImportHistory h WHERE h.status = 'TERMINÉ'")
    Long countDistinctDepartments();

    @Query("SELECT DISTINCT h.year FROM DVFImportHistory h WHERE h.status = 'TERMINÉ' ORDER BY h.year DESC")
    List<String> findDistinctYears();

    Long countByStatus(String status);
}

