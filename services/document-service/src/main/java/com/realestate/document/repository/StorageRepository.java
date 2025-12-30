package com.realestate.document.repository;

import com.realestate.document.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Query("SELECT s FROM Storage s WHERE s.isDefault = true AND s.active = true")
    Optional<Storage> findDefaultStorage();

    @Query("SELECT s FROM Storage s WHERE s.type = :type AND s.active = true")
    Optional<Storage> findByType(@Param("type") String type);
}

