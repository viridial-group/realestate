package com.realestate.property.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques analytics détaillées des annonces
 */
public class AdvertisementAnalyticsDTO {
    
    // Statistiques globales
    private Long totalAdvertisements;
    private Long activeAdvertisements;
    private Long totalImpressions;
    private Long totalClicks;
    private Long totalConversions;
    private BigDecimal totalSpent;
    private Double averageCTR; // Click-Through Rate moyen
    private Double averageCVR; // Conversion Rate moyen
    private BigDecimal averageCPC; // Cost Per Click moyen
    private BigDecimal averageCPA; // Cost Per Acquisition moyen
    
    // Statistiques par période (pour graphiques)
    private List<DailyStats> dailyStats; // Statistiques par jour
    private List<WeeklyStats> weeklyStats; // Statistiques par semaine
    private List<MonthlyStats> monthlyStats; // Statistiques par mois
    
    // Statistiques par type d'annonce
    private Map<String, TypeStats> statsByType;
    
    // Statistiques par position
    private Map<String, PositionStats> statsByPosition;
    
    // Top annonces (par impressions, clics, conversions)
    private List<TopAdvertisement> topByImpressions;
    private List<TopAdvertisement> topByClicks;
    private List<TopAdvertisement> topByConversions;
    
    // Getters and Setters
    public Long getTotalAdvertisements() {
        return totalAdvertisements;
    }
    
    public void setTotalAdvertisements(Long totalAdvertisements) {
        this.totalAdvertisements = totalAdvertisements;
    }
    
    public Long getActiveAdvertisements() {
        return activeAdvertisements;
    }
    
    public void setActiveAdvertisements(Long activeAdvertisements) {
        this.activeAdvertisements = activeAdvertisements;
    }
    
    public Long getTotalImpressions() {
        return totalImpressions;
    }
    
    public void setTotalImpressions(Long totalImpressions) {
        this.totalImpressions = totalImpressions;
    }
    
    public Long getTotalClicks() {
        return totalClicks;
    }
    
    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }
    
    public Long getTotalConversions() {
        return totalConversions;
    }
    
    public void setTotalConversions(Long totalConversions) {
        this.totalConversions = totalConversions;
    }
    
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public Double getAverageCTR() {
        return averageCTR;
    }
    
    public void setAverageCTR(Double averageCTR) {
        this.averageCTR = averageCTR;
    }
    
    public Double getAverageCVR() {
        return averageCVR;
    }
    
    public void setAverageCVR(Double averageCVR) {
        this.averageCVR = averageCVR;
    }
    
    public BigDecimal getAverageCPC() {
        return averageCPC;
    }
    
    public void setAverageCPC(BigDecimal averageCPC) {
        this.averageCPC = averageCPC;
    }
    
    public BigDecimal getAverageCPA() {
        return averageCPA;
    }
    
    public void setAverageCPA(BigDecimal averageCPA) {
        this.averageCPA = averageCPA;
    }
    
    public List<DailyStats> getDailyStats() {
        return dailyStats;
    }
    
    public void setDailyStats(List<DailyStats> dailyStats) {
        this.dailyStats = dailyStats;
    }
    
    public List<WeeklyStats> getWeeklyStats() {
        return weeklyStats;
    }
    
    public void setWeeklyStats(List<WeeklyStats> weeklyStats) {
        this.weeklyStats = weeklyStats;
    }
    
    public List<MonthlyStats> getMonthlyStats() {
        return monthlyStats;
    }
    
    public void setMonthlyStats(List<MonthlyStats> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }
    
    public Map<String, TypeStats> getStatsByType() {
        return statsByType;
    }
    
    public void setStatsByType(Map<String, TypeStats> statsByType) {
        this.statsByType = statsByType;
    }
    
    public Map<String, PositionStats> getStatsByPosition() {
        return statsByPosition;
    }
    
    public void setStatsByPosition(Map<String, PositionStats> statsByPosition) {
        this.statsByPosition = statsByPosition;
    }
    
    public List<TopAdvertisement> getTopByImpressions() {
        return topByImpressions;
    }
    
    public void setTopByImpressions(List<TopAdvertisement> topByImpressions) {
        this.topByImpressions = topByImpressions;
    }
    
    public List<TopAdvertisement> getTopByClicks() {
        return topByClicks;
    }
    
    public void setTopByClicks(List<TopAdvertisement> topByClicks) {
        this.topByClicks = topByClicks;
    }
    
    public List<TopAdvertisement> getTopByConversions() {
        return topByConversions;
    }
    
    public void setTopByConversions(List<TopAdvertisement> topByConversions) {
        this.topByConversions = topByConversions;
    }
    
    // Classes internes pour les statistiques détaillées
    public static class DailyStats {
        private LocalDate date;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public LocalDate getDate() {
            return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
    
    public static class WeeklyStats {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public LocalDate getWeekStart() {
            return weekStart;
        }
        
        public void setWeekStart(LocalDate weekStart) {
            this.weekStart = weekStart;
        }
        
        public LocalDate getWeekEnd() {
            return weekEnd;
        }
        
        public void setWeekEnd(LocalDate weekEnd) {
            this.weekEnd = weekEnd;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
    
    public static class MonthlyStats {
        private Integer year;
        private Integer month;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public Integer getYear() {
            return year;
        }
        
        public void setYear(Integer year) {
            this.year = year;
        }
        
        public Integer getMonth() {
            return month;
        }
        
        public void setMonth(Integer month) {
            this.month = month;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
    
    public static class TypeStats {
        private String type;
        private Long count;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Long getCount() {
            return count;
        }
        
        public void setCount(Long count) {
            this.count = count;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
    
    public static class PositionStats {
        private String position;
        private Long count;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public String getPosition() {
            return position;
        }
        
        public void setPosition(String position) {
            this.position = position;
        }
        
        public Long getCount() {
            return count;
        }
        
        public void setCount(Long count) {
            this.count = count;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
    
    public static class TopAdvertisement {
        private Long id;
        private String title;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal spent;
        private Double ctr;
        private Double cvr;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public Long getImpressions() {
            return impressions;
        }
        
        public void setImpressions(Long impressions) {
            this.impressions = impressions;
        }
        
        public Long getClicks() {
            return clicks;
        }
        
        public void setClicks(Long clicks) {
            this.clicks = clicks;
        }
        
        public Long getConversions() {
            return conversions;
        }
        
        public void setConversions(Long conversions) {
            this.conversions = conversions;
        }
        
        public BigDecimal getSpent() {
            return spent;
        }
        
        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }
        
        public Double getCtr() {
            return ctr;
        }
        
        public void setCtr(Double ctr) {
            this.ctr = ctr;
        }
        
        public Double getCvr() {
            return cvr;
        }
        
        public void setCvr(Double cvr) {
            this.cvr = cvr;
        }
    }
}

