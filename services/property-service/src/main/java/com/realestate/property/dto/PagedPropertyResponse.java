package com.realestate.property.dto;

import java.util.List;

/**
 * Réponse paginée sérialisable pour Redis
 * Utilisé pour remplacer Page<PropertyDTO> qui n'est pas facilement sérialisable
 */
public class PagedPropertyResponse {
    private List<PropertyDTO> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;

    public PagedPropertyResponse() {
    }

    public PagedPropertyResponse(List<PropertyDTO> content, int currentPage, int totalPages, long totalElements, int size, boolean first, boolean last) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.first = first;
        this.last = last;
    }

    // Getters and Setters
    public List<PropertyDTO> getContent() {
        return content;
    }

    public void setContent(List<PropertyDTO> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}

