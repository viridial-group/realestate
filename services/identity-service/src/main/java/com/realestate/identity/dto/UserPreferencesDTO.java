package com.realestate.identity.dto;

import jakarta.validation.constraints.Size;
import java.util.Map;

public class UserPreferencesDTO {

    @Size(max = 10)
    private String language;

    @Size(max = 50)
    private String timezone;

    // Préférences de notifications (email, push, SMS, etc.)
    private Map<String, Boolean> notificationPreferences;

    // Constructors
    public UserPreferencesDTO() {
    }

    // Getters and Setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Map<String, Boolean> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(Map<String, Boolean> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}

