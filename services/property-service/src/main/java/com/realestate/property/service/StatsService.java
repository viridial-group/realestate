package com.realestate.property.service;

import com.realestate.property.dto.DashboardStatsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service optimisé pour les statistiques avec requêtes SQL natives
 * Utilise des requêtes SQL directes pour performance maximale
 */
@Service
@Transactional(readOnly = true)
public class StatsService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Helper method to safely convert COUNT(*) results to Long
     * PostgreSQL can return either Long or BigInteger
     */
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * Récupère toutes les statistiques du dashboard en une seule requête optimisée
     */
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Statistiques des propriétés
        stats.setPropertyStats(getPropertyStats());
        
        // Statistiques des utilisateurs (depuis la base de données)
        stats.setUserStats(getUserStats());
        
        // Statistiques des organisations (depuis la base de données)
        stats.setOrganizationStats(getOrganizationStats());
        
        return stats;
    }

    /**
     * Statistiques des propriétés avec requêtes SQL optimisées
     */
    private DashboardStatsDTO.PropertyStats getPropertyStats() {
        DashboardStatsDTO.PropertyStats stats = new DashboardStatsDTO.PropertyStats();
        
        // Requête SQL native optimisée pour toutes les statistiques de base
        String sql = """
            SELECT 
                COUNT(*) as total,
                COUNT(*) FILTER (WHERE status = 'AVAILABLE') as available,
                COUNT(*) FILTER (WHERE status = 'SOLD') as sold,
                COUNT(*) FILTER (WHERE status = 'RENTED') as rented,
                COUNT(*) FILTER (WHERE status = 'PUBLISHED') as published,
                COUNT(*) FILTER (WHERE status = 'DRAFT') as draft,
                COUNT(*) FILTER (WHERE status = 'PENDING') as pending,
                COALESCE(AVG(price), 0) as avg_price,
                COALESCE(AVG(surface), 0) as avg_surface,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE)) as new_this_month,
                COUNT(*) FILTER (WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)) as new_this_week
            FROM properties
            WHERE active = true
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        stats.setTotal(toLong(result[0]));
        stats.setAvailable(toLong(result[1]));
        stats.setSold(toLong(result[2]));
        stats.setRented(toLong(result[3]));
        stats.setPublished(toLong(result[4]));
        stats.setDraft(toLong(result[5]));
        stats.setPending(toLong(result[6]));
        stats.setAveragePrice((BigDecimal) result[7]);
        stats.setAverageSurface((BigDecimal) result[8]);
        stats.setNewThisMonth(toLong(result[9]));
        stats.setNewThisWeek(toLong(result[10]));
        
        // Statistiques par type
        stats.setByType(getPropertyStatsByType());
        
        // Statistiques par statut
        stats.setByStatus(getPropertyStatsByStatus());
        
        // Statistiques par ville (top 10)
        stats.setByCity(getPropertyStatsByCity());
        
        return stats;
    }

    /**
     * Statistiques des propriétés par type
     */
    private Map<String, Long> getPropertyStatsByType() {
        String sql = """
            SELECT type, COUNT(*) as count
            FROM properties
            WHERE active = true AND type IS NOT NULL
            GROUP BY type
            ORDER BY count DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> toLong(row[1])
                ));
    }

    /**
     * Statistiques des propriétés par statut
     */
    private Map<String, Long> getPropertyStatsByStatus() {
        String sql = """
            SELECT status, COUNT(*) as count
            FROM properties
            WHERE active = true AND status IS NOT NULL
            GROUP BY status
            ORDER BY count DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> toLong(row[1])
                ));
    }

    /**
     * Statistiques des propriétés par ville (top 10)
     */
    private Map<String, Long> getPropertyStatsByCity() {
        String sql = """
            SELECT city, COUNT(*) as count
            FROM properties
            WHERE active = true AND city IS NOT NULL
            GROUP BY city
            ORDER BY count DESC
            LIMIT 10
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> toLong(row[1])
                ));
    }

    /**
     * Statistiques des utilisateurs depuis la base de données
     */
    private DashboardStatsDTO.UserStats getUserStats() {
        DashboardStatsDTO.UserStats stats = new DashboardStatsDTO.UserStats();
        
        String sql = """
            SELECT 
                COUNT(*) as total,
                COUNT(*) FILTER (WHERE enabled = true) as active,
                COUNT(*) FILTER (WHERE enabled = false) as inactive,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE)) as new_this_month,
                COUNT(*) FILTER (WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)) as new_this_week
            FROM users
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        stats.setTotal(toLong(result[0]));
        stats.setActive(toLong(result[1]));
        stats.setInactive(toLong(result[2]));
        stats.setNewThisMonth(toLong(result[3]));
        stats.setNewThisWeek(toLong(result[4]));
        
        return stats;
    }

    /**
     * Statistiques des organisations depuis la base de données
     */
    private DashboardStatsDTO.OrganizationStats getOrganizationStats() {
        DashboardStatsDTO.OrganizationStats stats = new DashboardStatsDTO.OrganizationStats();
        
        String sql = """
            SELECT 
                COUNT(*) as total,
                COUNT(*) FILTER (WHERE active = true) as active,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE)) as new_this_month,
                COUNT(*) FILTER (WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)) as new_this_week
            FROM organizations
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        stats.setTotal(toLong(result[0]));
        stats.setActive(toLong(result[1]));
        stats.setNewThisMonth(toLong(result[2]));
        stats.setNewThisWeek(toLong(result[3]));
        
        return stats;
    }
}

