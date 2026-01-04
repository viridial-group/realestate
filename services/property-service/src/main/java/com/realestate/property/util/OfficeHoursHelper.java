package com.realestate.property.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire pour gérer les horaires du bureau
 * Fournit des horaires par défaut et des méthodes pour les gérer
 */
public class OfficeHoursHelper {

    private static final Logger logger = LoggerFactory.getLogger(OfficeHoursHelper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Horaires par défaut pour une organisation immobilière
     * Format: {"monday": "9:00-18:00", "tuesday": "9:00-18:00", ...}
     */
    public static final String DEFAULT_OFFICE_HOURS_JSON = """
        {
            "monday": "9:00-18:00",
            "tuesday": "9:00-18:00",
            "wednesday": "9:00-18:00",
            "thursday": "9:00-18:00",
            "friday": "9:00-18:00",
            "saturday": "10:00-16:00",
            "sunday": "closed"
        }
        """;

    /**
     * Retourne les horaires par défaut au format JSON
     */
    public static String getDefaultOfficeHours() {
        return DEFAULT_OFFICE_HOURS_JSON;
    }

    /**
     * Parse les horaires depuis une chaîne JSON
     * Retourne null si le parsing échoue
     */
    public static Map<String, String> parseOfficeHours(String officeHoursJson) {
        if (officeHoursJson == null || officeHoursJson.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(officeHoursJson, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            logger.warn("Error parsing office hours JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Convertit une Map d'horaires en JSON
     */
    public static String toJson(Map<String, String> officeHours) {
        if (officeHours == null || officeHours.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(officeHours);
        } catch (Exception e) {
            logger.error("Error converting office hours to JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valide le format des horaires
     * Format attendu: "HH:mm-HH:mm" ou "closed"
     */
    public static boolean isValidHoursFormat(String hours) {
        if (hours == null) {
            return false;
        }
        if ("closed".equalsIgnoreCase(hours.trim())) {
            return true;
        }
        // Format: "9:00-18:00"
        return hours.matches("^\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}$");
    }

    /**
     * Valide une Map complète d'horaires
     */
    public static boolean validateOfficeHours(Map<String, String> officeHours) {
        if (officeHours == null) {
            return false;
        }
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String day : days) {
            String hours = officeHours.get(day);
            if (hours != null && !isValidHoursFormat(hours)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Crée une Map d'horaires avec des valeurs par défaut
     */
    public static Map<String, String> createDefaultOfficeHours() {
        Map<String, String> hours = new HashMap<>();
        hours.put("monday", "9:00-18:00");
        hours.put("tuesday", "9:00-18:00");
        hours.put("wednesday", "9:00-18:00");
        hours.put("thursday", "9:00-18:00");
        hours.put("friday", "9:00-18:00");
        hours.put("saturday", "10:00-16:00");
        hours.put("sunday", "closed");
        return hours;
    }
}

