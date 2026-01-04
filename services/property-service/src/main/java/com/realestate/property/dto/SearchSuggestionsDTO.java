package com.realestate.property.dto;

import java.util.List;

/**
 * DTO pour les suggestions de recherche automatiques
 */
public class SearchSuggestionsDTO {
    private List<String> cities;
    private List<String> types;
    private List<String> addresses;
    private List<String> titles;
    private List<String> popularSearches;

    public SearchSuggestionsDTO() {
    }

    public SearchSuggestionsDTO(List<String> cities, List<String> types, List<String> addresses, List<String> titles, List<String> popularSearches) {
        this.cities = cities;
        this.types = types;
        this.addresses = addresses;
        this.titles = titles;
        this.popularSearches = popularSearches;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getPopularSearches() {
        return popularSearches;
    }

    public void setPopularSearches(List<String> popularSearches) {
        this.popularSearches = popularSearches;
    }
}

