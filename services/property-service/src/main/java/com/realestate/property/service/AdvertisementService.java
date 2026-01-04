package com.realestate.property.service;

import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.property.dto.AdvertisementAnalyticsDTO;
import com.realestate.property.dto.AdvertisementCreateDTO;
import com.realestate.property.dto.AdvertisementDTO;
import com.realestate.property.dto.AdvertisementStatsDTO;
import com.realestate.property.dto.AdvertisementUpdateDTO;
import com.realestate.property.entity.Advertisement;
import com.realestate.property.entity.AdvertisementClick;
import com.realestate.property.entity.AdvertisementImpression;
import com.realestate.property.mapper.AdvertisementMapper;
import com.realestate.property.repository.AdvertisementClickRepository;
import com.realestate.property.repository.AdvertisementImpressionRepository;
import com.realestate.property.repository.AdvertisementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementClickRepository clickRepository;
    private final AdvertisementImpressionRepository impressionRepository;
    private final AdvertisementMapper advertisementMapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    public AdvertisementService(
            AdvertisementRepository advertisementRepository,
            AdvertisementClickRepository clickRepository,
            AdvertisementImpressionRepository impressionRepository,
            AdvertisementMapper advertisementMapper) {
        this.advertisementRepository = advertisementRepository;
        this.clickRepository = clickRepository;
        this.impressionRepository = impressionRepository;
        this.advertisementMapper = advertisementMapper;
    }
    
    /**
     * Helper method to safely convert COUNT(*) results to Long
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

    @Transactional
    public AdvertisementDTO createAdvertisement(AdvertisementCreateDTO dto) {
        Advertisement advertisement = advertisementMapper.toEntity(dto);
        advertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toDTO(advertisement);
    }

    @Transactional(readOnly = true)
    public AdvertisementDTO getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement", id));
        return advertisementMapper.toDTO(advertisement);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementDTO> getAllAdvertisements(Long organizationId, String status) {
        List<Advertisement> advertisements;
        if (organizationId != null && status != null) {
            advertisements = advertisementRepository.findByOrganizationIdAndStatusOrderByCreatedAtDesc(organizationId, status);
        } else if (organizationId != null) {
            advertisements = advertisementRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
        } else {
            advertisements = advertisementRepository.findAll();
        }
        return advertisements.stream()
                .map(advertisementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementDTO updateAdvertisement(Long id, AdvertisementUpdateDTO dto) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement", id));
        advertisementMapper.updateEntity(advertisement, dto);
        advertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toDTO(advertisement);
    }

    @Transactional
    public void deleteAdvertisement(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement", id));
        advertisementRepository.delete(advertisement);
    }

    @Transactional
    public AdvertisementDTO updateStatus(Long id, String status) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement", id));
        advertisement.setStatus(status);
        advertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toDTO(advertisement);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementDTO> getActiveAdvertisements(String adType, String position, String city, String postalCode, String propertyType) {
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> advertisements;

        if (adType != null && position != null) {
            advertisements = advertisementRepository.findActiveByTypeAndPosition(adType, position, now);
        } else if (city != null || postalCode != null) {
            advertisements = advertisementRepository.findActiveByLocation(city != null ? city : "", postalCode != null ? postalCode : "", now);
        } else if (propertyType != null) {
            advertisements = advertisementRepository.findActiveByPropertyType(propertyType, now);
        } else {
            // Retourner toutes les annonces actives
            advertisements = advertisementRepository.findAll().stream()
                    .filter(ad -> ad.getActive() && "ACTIVE".equals(ad.getStatus())
                            && (ad.getStartDate() == null || ad.getStartDate().isBefore(now) || ad.getStartDate().isEqual(now))
                            && (ad.getEndDate() == null || ad.getEndDate().isAfter(now) || ad.getEndDate().isEqual(now)))
                    .collect(Collectors.toList());
        }

        // Filtrer par limite d'impressions quotidiennes
        return advertisements.stream()
                .filter(ad -> {
                    if (ad.getMaxImpressionsPerDay() != null) {
                        try {
                            Long todayImpressions = impressionRepository.countByAdvertisementIdAndDate(ad.getId(), now);
                            return todayImpressions != null && todayImpressions < ad.getMaxImpressionsPerDay();
                        } catch (Exception e) {
                            logger.warn("Error checking daily impressions limit for ad {}: {}", ad.getId(), e.getMessage());
                            return true; // En cas d'erreur, on autorise l'affichage
                        }
                    }
                    return true;
                })
                .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority())) // Trier par priorité décroissante
                .limit(5) // Limiter à 5 annonces maximum par slot
                .map(advertisementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordImpression(Long advertisementId, String ipAddress, String userAgent, Long propertyId, String pageType, String city, String postalCode) {
        try {
            AdvertisementImpression impression = new AdvertisementImpression();
            impression.setAdvertisementId(advertisementId);
            impression.setIpAddress(ipAddress);
            impression.setUserAgent(userAgent);
            impression.setPropertyId(propertyId);
            impression.setPageType(pageType);
            impression.setCity(city);
            impression.setPostalCode(postalCode);
            impressionRepository.save(impression);

            // Mettre à jour le compteur d'impressions
            Advertisement advertisement = advertisementRepository.findById(advertisementId).orElse(null);
            if (advertisement != null) {
                advertisement.setImpressions(advertisement.getImpressions() + 1);
                
                // Calculer le coût si CPM
                if (advertisement.getCostPerImpression() != null) {
                    BigDecimal cost = advertisement.getCostPerImpression()
                            .divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP);
                    advertisement.setTotalSpent(advertisement.getTotalSpent().add(cost));
                }
                
                advertisementRepository.save(advertisement);
            }
        } catch (Exception e) {
            logger.error("Error recording impression for advertisement {}: {}", advertisementId, e.getMessage());
        }
    }

    @Transactional
    public void recordClick(Long advertisementId, String ipAddress, String userAgent, String referrer, Long propertyId, String city, String postalCode) {
        try {
            AdvertisementClick click = new AdvertisementClick();
            click.setAdvertisementId(advertisementId);
            click.setIpAddress(ipAddress);
            click.setUserAgent(userAgent);
            click.setReferrer(referrer);
            click.setPropertyId(propertyId);
            click.setCity(city);
            click.setPostalCode(postalCode);
            clickRepository.save(click);

            // Mettre à jour le compteur de clics
            Advertisement advertisement = advertisementRepository.findById(advertisementId).orElse(null);
            if (advertisement != null) {
                advertisement.setClicks(advertisement.getClicks() + 1);
                
                // Calculer le coût si CPC
                if (advertisement.getCostPerClick() != null) {
                    advertisement.setTotalSpent(advertisement.getTotalSpent().add(advertisement.getCostPerClick()));
                }
                
                advertisementRepository.save(advertisement);
            }
        } catch (Exception e) {
            logger.error("Error recording click for advertisement {}: {}", advertisementId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AdvertisementStatsDTO getAdvertisementStats(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement", id));
        
        AdvertisementStatsDTO stats = new AdvertisementStatsDTO();
        stats.setAdvertisementId(advertisement.getId());
        stats.setTitle(advertisement.getTitle());
        stats.setImpressions(advertisement.getImpressions());
        stats.setClicks(advertisement.getClicks());
        stats.setConversions(advertisement.getConversions());
        stats.setTotalSpent(advertisement.getTotalSpent());
        stats.setStartDate(advertisement.getStartDate());
        stats.setEndDate(advertisement.getEndDate());
        stats.setStatus(advertisement.getStatus());

        // Calculer les métriques
        if (advertisement.getImpressions() != null && advertisement.getImpressions() > 0) {
            double ctr = (advertisement.getClicks() != null ? advertisement.getClicks().doubleValue() : 0.0) 
                         / advertisement.getImpressions().doubleValue() * 100.0;
            stats.setClickThroughRate(Math.round(ctr * 100.0) / 100.0);
        }

        if (advertisement.getClicks() != null && advertisement.getClicks() > 0) {
            double cr = (advertisement.getConversions() != null ? advertisement.getConversions().doubleValue() : 0.0) 
                       / advertisement.getClicks().doubleValue() * 100.0;
            stats.setConversionRate(Math.round(cr * 100.0) / 100.0);
        }

        if (advertisement.getClicks() != null && advertisement.getClicks() > 0 && advertisement.getTotalSpent() != null) {
            BigDecimal cpc = advertisement.getTotalSpent()
                    .divide(BigDecimal.valueOf(advertisement.getClicks()), 2, java.math.RoundingMode.HALF_UP);
            stats.setCostPerClick(cpc);
        }

        if (advertisement.getConversions() != null && advertisement.getConversions() > 0 && advertisement.getTotalSpent() != null) {
            BigDecimal cpa = advertisement.getTotalSpent()
                    .divide(BigDecimal.valueOf(advertisement.getConversions()), 2, java.math.RoundingMode.HALF_UP);
            stats.setCostPerConversion(cpa);
        }

        return stats;
    }

    // Tâche planifiée pour expirer les annonces
    @Scheduled(cron = "0 0 * * * ?") // Toutes les heures
    @Transactional
    public void expireAdvertisements() {
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> expired = advertisementRepository.findExpired(now);
        for (Advertisement ad : expired) {
            ad.setStatus("EXPIRED");
            advertisementRepository.save(ad);
            logger.info("Expired advertisement: {}", ad.getId());
        }
    }
    
    /**
     * Récupère les statistiques analytics complètes pour toutes les annonces
     */
    @Transactional(readOnly = true)
    public AdvertisementAnalyticsDTO getAnalytics(Long organizationId, LocalDate startDate, LocalDate endDate) {
        AdvertisementAnalyticsDTO analytics = new AdvertisementAnalyticsDTO();
        
        // Statistiques globales
        String globalStatsSql = """
            SELECT 
                COUNT(*) as total,
                COUNT(*) FILTER (WHERE status = 'ACTIVE' AND active = true) as active,
                COALESCE(SUM(impressions), 0) as total_impressions,
                COALESCE(SUM(clicks), 0) as total_clicks,
                COALESCE(SUM(conversions), 0) as total_conversions,
                COALESCE(SUM(total_spent), 0) as total_spent
            FROM advertisements
            WHERE active = true
            """ + (organizationId != null ? " AND organization_id = :organizationId" : "");
        
        Query globalQuery = entityManager.createNativeQuery(globalStatsSql);
        if (organizationId != null) {
            globalQuery.setParameter("organizationId", organizationId);
        }
        Object[] globalResult = (Object[]) globalQuery.getSingleResult();
        
        analytics.setTotalAdvertisements(toLong(globalResult[0]));
        analytics.setActiveAdvertisements(toLong(globalResult[1]));
        analytics.setTotalImpressions(toLong(globalResult[2]));
        analytics.setTotalClicks(toLong(globalResult[3]));
        analytics.setTotalConversions(toLong(globalResult[4]));
        analytics.setTotalSpent((BigDecimal) globalResult[5]);
        
        // Calculer les moyennes
        Long totalAds = analytics.getTotalAdvertisements();
        if (totalAds > 0) {
            Long totalImpressions = analytics.getTotalImpressions();
            Long totalClicks = analytics.getTotalClicks();
            Long totalConversions = analytics.getTotalConversions();
            BigDecimal totalSpent = analytics.getTotalSpent();
            
            if (totalImpressions > 0) {
                analytics.setAverageCTR((totalClicks.doubleValue() / totalImpressions.doubleValue()) * 100.0);
            }
            if (totalClicks > 0) {
                analytics.setAverageCVR((totalConversions.doubleValue() / totalClicks.doubleValue()) * 100.0);
            }
            if (totalClicks > 0 && totalSpent != null) {
                analytics.setAverageCPC(totalSpent.divide(BigDecimal.valueOf(totalClicks), 2, java.math.RoundingMode.HALF_UP));
            }
            if (totalConversions > 0 && totalSpent != null) {
                analytics.setAverageCPA(totalSpent.divide(BigDecimal.valueOf(totalConversions), 2, java.math.RoundingMode.HALF_UP));
            }
        }
        
        // Statistiques par jour (30 derniers jours)
        LocalDate defaultStartDate = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate defaultEndDate = endDate != null ? endDate : LocalDate.now();
        
        analytics.setDailyStats(getDailyStats(organizationId, defaultStartDate, defaultEndDate));
        analytics.setWeeklyStats(getWeeklyStats(organizationId, defaultStartDate, defaultEndDate));
        analytics.setMonthlyStats(getMonthlyStats(organizationId, defaultStartDate, defaultEndDate));
        analytics.setStatsByType(getStatsByType(organizationId));
        analytics.setStatsByPosition(getStatsByPosition(organizationId));
        analytics.setTopByImpressions(getTopAdvertisements(organizationId, "impressions", 10));
        analytics.setTopByClicks(getTopAdvertisements(organizationId, "clicks", 10));
        analytics.setTopByConversions(getTopAdvertisements(organizationId, "conversions", 10));
        
        return analytics;
    }
    
    private List<AdvertisementAnalyticsDTO.DailyStats> getDailyStats(Long organizationId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                DATE(impressed_at) as date,
                COUNT(*) as impressions,
                (SELECT COUNT(*) FROM advertisement_clicks 
                 WHERE advertisement_id IN (SELECT id FROM advertisements WHERE active = true 
                 """ + (organizationId != null ? " AND organization_id = :organizationId" : "") + """
                 )
                 AND DATE(clicked_at) = DATE(impressed_at)) as clicks,
                COALESCE(SUM(a.total_spent), 0) as spent
            FROM advertisement_impressions i
            LEFT JOIN advertisements a ON a.id = i.advertisement_id
            WHERE DATE(impressed_at) BETWEEN :startDate AND :endDate
            """ + (organizationId != null ? " AND a.organization_id = :organizationId" : "") + """
            GROUP BY DATE(impressed_at)
            ORDER BY date ASC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        
        List<Object[]> results = query.getResultList();
        List<AdvertisementAnalyticsDTO.DailyStats> dailyStats = new ArrayList<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.DailyStats stats = new AdvertisementAnalyticsDTO.DailyStats();
            stats.setDate(((java.sql.Date) row[0]).toLocalDate());
            stats.setImpressions(toLong(row[1]));
            stats.setClicks(toLong(row[2]));
            stats.setSpent((BigDecimal) row[3]);
            
            if (stats.getImpressions() > 0) {
                stats.setCtr((stats.getClicks().doubleValue() / stats.getImpressions().doubleValue()) * 100.0);
            }
            
            dailyStats.add(stats);
        }
        
        return dailyStats;
    }
    
    private List<AdvertisementAnalyticsDTO.WeeklyStats> getWeeklyStats(Long organizationId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                DATE_TRUNC('week', impressed_at)::date as week_start,
                (DATE_TRUNC('week', impressed_at) + INTERVAL '6 days')::date as week_end,
                COUNT(*) as impressions,
                (SELECT COUNT(*) FROM advertisement_clicks 
                 WHERE advertisement_id IN (SELECT id FROM advertisements WHERE active = true 
                 """ + (organizationId != null ? " AND organization_id = :organizationId" : "") + """
                 )
                 AND DATE_TRUNC('week', clicked_at) = DATE_TRUNC('week', impressed_at)) as clicks,
                COALESCE(SUM(a.total_spent), 0) as spent
            FROM advertisement_impressions i
            LEFT JOIN advertisements a ON a.id = i.advertisement_id
            WHERE DATE(impressed_at) BETWEEN :startDate AND :endDate
            """ + (organizationId != null ? " AND a.organization_id = :organizationId" : "") + """
            GROUP BY DATE_TRUNC('week', impressed_at)
            ORDER BY week_start ASC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        
        List<Object[]> results = query.getResultList();
        List<AdvertisementAnalyticsDTO.WeeklyStats> weeklyStats = new ArrayList<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.WeeklyStats stats = new AdvertisementAnalyticsDTO.WeeklyStats();
            stats.setWeekStart(((java.sql.Date) row[0]).toLocalDate());
            stats.setWeekEnd(((java.sql.Date) row[1]).toLocalDate());
            stats.setImpressions(toLong(row[2]));
            stats.setClicks(toLong(row[3]));
            stats.setSpent((BigDecimal) row[4]);
            
            if (stats.getImpressions() > 0) {
                stats.setCtr((stats.getClicks().doubleValue() / stats.getImpressions().doubleValue()) * 100.0);
            }
            
            weeklyStats.add(stats);
        }
        
        return weeklyStats;
    }
    
    private List<AdvertisementAnalyticsDTO.MonthlyStats> getMonthlyStats(Long organizationId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                EXTRACT(YEAR FROM impressed_at)::int as year,
                EXTRACT(MONTH FROM impressed_at)::int as month,
                COUNT(*) as impressions,
                (SELECT COUNT(*) FROM advertisement_clicks 
                 WHERE advertisement_id IN (SELECT id FROM advertisements WHERE active = true 
                 """ + (organizationId != null ? " AND organization_id = :organizationId" : "") + """
                 )
                 AND EXTRACT(YEAR FROM clicked_at) = EXTRACT(YEAR FROM impressed_at)
                 AND EXTRACT(MONTH FROM clicked_at) = EXTRACT(MONTH FROM impressed_at)) as clicks,
                COALESCE(SUM(a.total_spent), 0) as spent
            FROM advertisement_impressions i
            LEFT JOIN advertisements a ON a.id = i.advertisement_id
            WHERE DATE(impressed_at) BETWEEN :startDate AND :endDate
            """ + (organizationId != null ? " AND a.organization_id = :organizationId" : "") + """
            GROUP BY EXTRACT(YEAR FROM impressed_at), EXTRACT(MONTH FROM impressed_at)
            ORDER BY year ASC, month ASC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        
        List<Object[]> results = query.getResultList();
        List<AdvertisementAnalyticsDTO.MonthlyStats> monthlyStats = new ArrayList<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.MonthlyStats stats = new AdvertisementAnalyticsDTO.MonthlyStats();
            stats.setYear(((Number) row[0]).intValue());
            stats.setMonth(((Number) row[1]).intValue());
            stats.setImpressions(toLong(row[2]));
            stats.setClicks(toLong(row[3]));
            stats.setSpent((BigDecimal) row[4]);
            
            if (stats.getImpressions() > 0) {
                stats.setCtr((stats.getClicks().doubleValue() / stats.getImpressions().doubleValue()) * 100.0);
            }
            
            monthlyStats.add(stats);
        }
        
        return monthlyStats;
    }
    
    private Map<String, AdvertisementAnalyticsDTO.TypeStats> getStatsByType(Long organizationId) {
        String sql = """
            SELECT 
                ad_type,
                COUNT(*) as count,
                COALESCE(SUM(impressions), 0) as impressions,
                COALESCE(SUM(clicks), 0) as clicks,
                COALESCE(SUM(conversions), 0) as conversions,
                COALESCE(SUM(total_spent), 0) as spent
            FROM advertisements
            WHERE active = true
            """ + (organizationId != null ? " AND organization_id = :organizationId" : "") + """
            GROUP BY ad_type
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        
        List<Object[]> results = query.getResultList();
        Map<String, AdvertisementAnalyticsDTO.TypeStats> statsByType = new HashMap<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.TypeStats stats = new AdvertisementAnalyticsDTO.TypeStats();
            stats.setType((String) row[0]);
            stats.setCount(toLong(row[1]));
            stats.setImpressions(toLong(row[2]));
            stats.setClicks(toLong(row[3]));
            stats.setConversions(toLong(row[4]));
            stats.setSpent((BigDecimal) row[5]);
            
            if (stats.getImpressions() > 0) {
                stats.setCtr((stats.getClicks().doubleValue() / stats.getImpressions().doubleValue()) * 100.0);
            }
            if (stats.getClicks() > 0) {
                stats.setCvr((stats.getConversions().doubleValue() / stats.getClicks().doubleValue()) * 100.0);
            }
            
            statsByType.put(stats.getType(), stats);
        }
        
        return statsByType;
    }
    
    private Map<String, AdvertisementAnalyticsDTO.PositionStats> getStatsByPosition(Long organizationId) {
        String sql = """
            SELECT 
                position,
                COUNT(*) as count,
                COALESCE(SUM(impressions), 0) as impressions,
                COALESCE(SUM(clicks), 0) as clicks,
                COALESCE(SUM(conversions), 0) as conversions,
                COALESCE(SUM(total_spent), 0) as spent
            FROM advertisements
            WHERE active = true
            """ + (organizationId != null ? " AND organization_id = :organizationId" : "") + """
            GROUP BY position
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        
        List<Object[]> results = query.getResultList();
        Map<String, AdvertisementAnalyticsDTO.PositionStats> statsByPosition = new HashMap<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.PositionStats stats = new AdvertisementAnalyticsDTO.PositionStats();
            stats.setPosition((String) row[0]);
            stats.setCount(toLong(row[1]));
            stats.setImpressions(toLong(row[2]));
            stats.setClicks(toLong(row[3]));
            stats.setConversions(toLong(row[4]));
            stats.setSpent((BigDecimal) row[5]);
            
            if (stats.getImpressions() > 0) {
                stats.setCtr((stats.getClicks().doubleValue() / stats.getImpressions().doubleValue()) * 100.0);
            }
            if (stats.getClicks() > 0) {
                stats.setCvr((stats.getConversions().doubleValue() / stats.getClicks().doubleValue()) * 100.0);
            }
            
            statsByPosition.put(stats.getPosition(), stats);
        }
        
        return statsByPosition;
    }
    
    private List<AdvertisementAnalyticsDTO.TopAdvertisement> getTopAdvertisements(Long organizationId, String orderBy, int limit) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("id, ");
        sqlBuilder.append("title, ");
        sqlBuilder.append("COALESCE(impressions, 0) as impressions, ");
        sqlBuilder.append("COALESCE(clicks, 0) as clicks, ");
        sqlBuilder.append("COALESCE(conversions, 0) as conversions, ");
        sqlBuilder.append("COALESCE(total_spent, 0) as spent ");
        sqlBuilder.append("FROM advertisements ");
        sqlBuilder.append("WHERE active = true");
        if (organizationId != null) {
            sqlBuilder.append(" AND organization_id = :organizationId");
        }
        sqlBuilder.append(" ORDER BY ").append(orderBy).append(" DESC ");
        sqlBuilder.append("LIMIT :limit");
        String sql = sqlBuilder.toString();
        
        Query query = entityManager.createNativeQuery(sql);
        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
        }
        query.setParameter("limit", limit);
        
        List<Object[]> results = query.getResultList();
        List<AdvertisementAnalyticsDTO.TopAdvertisement> topAds = new ArrayList<>();
        
        for (Object[] row : results) {
            AdvertisementAnalyticsDTO.TopAdvertisement top = new AdvertisementAnalyticsDTO.TopAdvertisement();
            top.setId(toLong(row[0]));
            top.setTitle((String) row[1]);
            top.setImpressions(toLong(row[2]));
            top.setClicks(toLong(row[3]));
            top.setConversions(toLong(row[4]));
            top.setSpent((BigDecimal) row[5]);
            
            if (top.getImpressions() > 0) {
                top.setCtr((top.getClicks().doubleValue() / top.getImpressions().doubleValue()) * 100.0);
            }
            if (top.getClicks() > 0) {
                top.setCvr((top.getConversions().doubleValue() / top.getClicks().doubleValue()) * 100.0);
            }
            
            topAds.add(top);
        }
        
        return topAds;
    }
}

