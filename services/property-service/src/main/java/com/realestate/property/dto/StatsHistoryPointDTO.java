package com.realestate.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO pour un point de données dans l'historique des statistiques
 */
@Schema(description = "Statistics history data point")
public class StatsHistoryPointDTO {

    @Schema(description = "Date of the statistics point", example = "2026-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Number of views", example = "42")
    private Long views;

    @Schema(description = "Number of contacts", example = "5")
    private Long contacts;

    @Schema(description = "Number of favorites", example = "8")
    private Long favorites;

    @Schema(description = "Number of shares", example = "3")
    private Long shares;

    public StatsHistoryPointDTO() {
    }

    public StatsHistoryPointDTO(LocalDate date, Long views, Long contacts, Long favorites, Long shares) {
        this.date = date;
        this.views = views;
        this.contacts = contacts;
        this.favorites = favorites;
        this.shares = shares;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getContacts() {
        return contacts;
    }

    public void setContacts(Long contacts) {
        this.contacts = contacts;
    }

    public Long getFavorites() {
        return favorites;
    }

    public void setFavorites(Long favorites) {
        this.favorites = favorites;
    }

    public Long getShares() {
        return shares;
    }

    public void setShares(Long shares) {
        this.shares = shares;
    }
}

