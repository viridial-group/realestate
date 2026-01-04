package com.realestate.property.service;

import com.realestate.property.dto.StatsHistoryPointDTO;
import com.realestate.property.entity.PropertyEvent;
import com.realestate.property.repository.PropertyEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour gérer les événements de propriétés
 * Permet de tracker les vues, contacts, favoris, partages
 */
@Service
@Transactional
public class PropertyEventService {

    private final PropertyEventRepository propertyEventRepository;

    public PropertyEventService(PropertyEventRepository propertyEventRepository) {
        this.propertyEventRepository = propertyEventRepository;
    }

    /**
     * Enregistre un événement de vue
     */
    @Transactional
    public PropertyEvent trackView(Long propertyId, Long userId, String metadata) {
        PropertyEvent event = new PropertyEvent(propertyId, PropertyEvent.EVENT_TYPE_VIEW, userId, metadata);
        return propertyEventRepository.save(event);
    }

    /**
     * Enregistre un événement de contact
     */
    @Transactional
    public PropertyEvent trackContact(Long propertyId, Long userId, String metadata) {
        PropertyEvent event = new PropertyEvent(propertyId, PropertyEvent.EVENT_TYPE_CONTACT, userId, metadata);
        return propertyEventRepository.save(event);
    }

    /**
     * Enregistre un événement de favori
     */
    @Transactional
    public PropertyEvent trackFavorite(Long propertyId, Long userId, String metadata) {
        PropertyEvent event = new PropertyEvent(propertyId, PropertyEvent.EVENT_TYPE_FAVORITE, userId, metadata);
        return propertyEventRepository.save(event);
    }

    /**
     * Enregistre un événement de partage
     */
    @Transactional
    public PropertyEvent trackShare(Long propertyId, Long userId, String platform, String metadata) {
        String shareMetadata = metadata != null ? metadata : "{\"platform\":\"" + platform + "\"}";
        PropertyEvent event = new PropertyEvent(propertyId, PropertyEvent.EVENT_TYPE_SHARE, userId, shareMetadata);
        return propertyEventRepository.save(event);
    }

    /**
     * Enregistre un événement générique
     */
    @Transactional
    public PropertyEvent trackEvent(Long propertyId, String eventType, Long userId, String metadata) {
        PropertyEvent event = new PropertyEvent(propertyId, eventType, userId, metadata);
        return propertyEventRepository.save(event);
    }

    /**
     * Récupère l'historique des statistiques pour une propriété
     */
    @Transactional(readOnly = true)
    public List<StatsHistoryPointDTO> getPropertyStatsHistory(Long propertyId, Integer days) {
        int daysToFetch = (days != null && days > 0 && days <= 90) ? days : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(daysToFetch - 1);

        List<PropertyEvent> events = propertyEventRepository.findByPropertyIdAndDateRange(
                propertyId, startDate, endDate
        );

        return aggregateEventsByDate(events, startDate, endDate);
    }

    /**
     * Récupère l'historique des statistiques globales
     */
    @Transactional(readOnly = true)
    public List<StatsHistoryPointDTO> getGlobalStatsHistory(Integer days) {
        int daysToFetch = (days != null && days > 0 && days <= 90) ? days : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(daysToFetch - 1);

        List<PropertyEvent> events = propertyEventRepository.findByDateRange(startDate, endDate);

        return aggregateEventsByDate(events, startDate, endDate);
    }

    /**
     * Agrège les événements par date
     */
    private List<StatsHistoryPointDTO> aggregateEventsByDate(
            List<PropertyEvent> events, LocalDate startDate, LocalDate endDate) {
        
        // Grouper les événements par date
        Map<LocalDate, List<PropertyEvent>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(event -> 
                    event.getCreatedAt().toLocalDate()
                ));

        // Créer les points de données
        List<StatsHistoryPointDTO> history = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            List<PropertyEvent> dayEvents = eventsByDate.getOrDefault(currentDate, new ArrayList<>());
            
            long views = dayEvents.stream()
                    .filter(e -> PropertyEvent.EVENT_TYPE_VIEW.equals(e.getEventType()))
                    .count();
            
            long contacts = dayEvents.stream()
                    .filter(e -> PropertyEvent.EVENT_TYPE_CONTACT.equals(e.getEventType()))
                    .count();
            
            long favorites = dayEvents.stream()
                    .filter(e -> PropertyEvent.EVENT_TYPE_FAVORITE.equals(e.getEventType()))
                    .count();
            
            long shares = dayEvents.stream()
                    .filter(e -> PropertyEvent.EVENT_TYPE_SHARE.equals(e.getEventType()))
                    .count();

            history.add(new StatsHistoryPointDTO(currentDate, views, contacts, favorites, shares));
            currentDate = currentDate.plusDays(1);
        }

        return history;
    }

    /**
     * Récupère les statistiques actuelles pour une propriété
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getPropertyCurrentStats(Long propertyId) {
        List<Object[]> counts = propertyEventRepository.countByEventTypeForProperty(propertyId);
        
        long views = 0;
        long contacts = 0;
        long favorites = 0;
        long shares = 0;

        for (Object[] row : counts) {
            String eventType = (String) row[0];
            Long count = (Long) row[1];

            switch (eventType) {
                case PropertyEvent.EVENT_TYPE_VIEW:
                    views = count;
                    break;
                case PropertyEvent.EVENT_TYPE_CONTACT:
                    contacts = count;
                    break;
                case PropertyEvent.EVENT_TYPE_FAVORITE:
                    favorites = count;
                    break;
                case PropertyEvent.EVENT_TYPE_SHARE:
                    shares = count;
                    break;
            }
        }

        return Map.of(
                "views", views,
                "contacts", contacts,
                "favorites", favorites,
                "shares", shares
        );
    }
}

