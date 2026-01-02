package com.realestate.property.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques complètes du dashboard
 * Optimisé avec requêtes SQL natives pour performance maximale
 */
public class DashboardStatsDTO {
    
    // Statistiques des propriétés
    private PropertyStats propertyStats;
    
    // Statistiques des utilisateurs
    private UserStats userStats;
    
    // Statistiques des organisations
    private OrganizationStats organizationStats;
    
    public DashboardStatsDTO() {
    }
    
    public DashboardStatsDTO(PropertyStats propertyStats, UserStats userStats, OrganizationStats organizationStats) {
        this.propertyStats = propertyStats;
        this.userStats = userStats;
        this.organizationStats = organizationStats;
    }
    
    // Getters and Setters
    public PropertyStats getPropertyStats() {
        return propertyStats;
    }
    
    public void setPropertyStats(PropertyStats propertyStats) {
        this.propertyStats = propertyStats;
    }
    
    public UserStats getUserStats() {
        return userStats;
    }
    
    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }
    
    public OrganizationStats getOrganizationStats() {
        return organizationStats;
    }
    
    public void setOrganizationStats(OrganizationStats organizationStats) {
        this.organizationStats = organizationStats;
    }
    
    public static class PropertyStats {
        private Long total;
        private Long available;
        private Long sold;
        private Long rented;
        private Long published;
        private Long draft;
        private Long pending;
        private BigDecimal averagePrice;
        private BigDecimal averageSurface;
        private Long newThisMonth;
        private Long newThisWeek;
        private Map<String, Long> byType;
        private Map<String, Long> byStatus;
        private Map<String, Long> byCity;
        
        public PropertyStats() {
        }
        
        public PropertyStats(Long total, Long available, Long sold, Long rented, Long published, Long draft, Long pending,
                            BigDecimal averagePrice, BigDecimal averageSurface, Long newThisMonth, Long newThisWeek,
                            Map<String, Long> byType, Map<String, Long> byStatus, Map<String, Long> byCity) {
            this.total = total;
            this.available = available;
            this.sold = sold;
            this.rented = rented;
            this.published = published;
            this.draft = draft;
            this.pending = pending;
            this.averagePrice = averagePrice;
            this.averageSurface = averageSurface;
            this.newThisMonth = newThisMonth;
            this.newThisWeek = newThisWeek;
            this.byType = byType;
            this.byStatus = byStatus;
            this.byCity = byCity;
        }
        
        // Getters and Setters
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public Long getAvailable() { return available; }
        public void setAvailable(Long available) { this.available = available; }
        
        public Long getSold() { return sold; }
        public void setSold(Long sold) { this.sold = sold; }
        
        public Long getRented() { return rented; }
        public void setRented(Long rented) { this.rented = rented; }
        
        public Long getPublished() { return published; }
        public void setPublished(Long published) { this.published = published; }
        
        public Long getDraft() { return draft; }
        public void setDraft(Long draft) { this.draft = draft; }
        
        public Long getPending() { return pending; }
        public void setPending(Long pending) { this.pending = pending; }
        
        public BigDecimal getAveragePrice() { return averagePrice; }
        public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }
        
        public BigDecimal getAverageSurface() { return averageSurface; }
        public void setAverageSurface(BigDecimal averageSurface) { this.averageSurface = averageSurface; }
        
        public Long getNewThisMonth() { return newThisMonth; }
        public void setNewThisMonth(Long newThisMonth) { this.newThisMonth = newThisMonth; }
        
        public Long getNewThisWeek() { return newThisWeek; }
        public void setNewThisWeek(Long newThisWeek) { this.newThisWeek = newThisWeek; }
        
        public Map<String, Long> getByType() { return byType; }
        public void setByType(Map<String, Long> byType) { this.byType = byType; }
        
        public Map<String, Long> getByStatus() { return byStatus; }
        public void setByStatus(Map<String, Long> byStatus) { this.byStatus = byStatus; }
        
        public Map<String, Long> getByCity() { return byCity; }
        public void setByCity(Map<String, Long> byCity) { this.byCity = byCity; }
    }
    
    public static class UserStats {
        private Long total;
        private Long active;
        private Long inactive;
        private Long newThisMonth;
        private Long newThisWeek;
        
        public UserStats() {
        }
        
        public UserStats(Long total, Long active, Long inactive, Long newThisMonth, Long newThisWeek) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
            this.newThisMonth = newThisMonth;
            this.newThisWeek = newThisWeek;
        }
        
        // Getters and Setters
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public Long getActive() { return active; }
        public void setActive(Long active) { this.active = active; }
        
        public Long getInactive() { return inactive; }
        public void setInactive(Long inactive) { this.inactive = inactive; }
        
        public Long getNewThisMonth() { return newThisMonth; }
        public void setNewThisMonth(Long newThisMonth) { this.newThisMonth = newThisMonth; }
        
        public Long getNewThisWeek() { return newThisWeek; }
        public void setNewThisWeek(Long newThisWeek) { this.newThisWeek = newThisWeek; }
    }
    
    public static class OrganizationStats {
        private Long total;
        private Long active;
        private Long newThisMonth;
        private Long newThisWeek;
        
        public OrganizationStats() {
        }
        
        public OrganizationStats(Long total, Long active, Long newThisMonth, Long newThisWeek) {
            this.total = total;
            this.active = active;
            this.newThisMonth = newThisMonth;
            this.newThisWeek = newThisWeek;
        }
        
        // Getters and Setters
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public Long getActive() { return active; }
        public void setActive(Long active) { this.active = active; }
        
        public Long getNewThisMonth() { return newThisMonth; }
        public void setNewThisMonth(Long newThisMonth) { this.newThisMonth = newThisMonth; }
        
        public Long getNewThisWeek() { return newThisWeek; }
        public void setNewThisWeek(Long newThisWeek) { this.newThisWeek = newThisWeek; }
    }
}

