package com.realestate.property.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilitaire pour générer des slugs SEO-friendly
 * Convertit les titres en URLs lisibles et optimisées pour le SEO
 */
@Component
public class SlugGenerator {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDASHES = Pattern.compile("(^-|-$)");

    /**
     * Génère un slug SEO-friendly à partir d'un titre
     * Exemple: "Appartement 3 pièces Paris" -> "appartement-3-pieces-paris"
     * 
     * @param title Le titre à convertir en slug
     * @return Le slug généré
     */
    public String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "";
        }

        // Normaliser les caractères Unicode (é -> e, à -> a, etc.)
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        
        // Supprimer les accents
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        // Convertir en minuscules
        normalized = normalized.toLowerCase(Locale.FRENCH);
        
        // Remplacer les espaces par des tirets
        normalized = WHITESPACE.matcher(normalized).replaceAll("-");
        
        // Supprimer les caractères non alphanumériques (sauf tirets)
        normalized = NONLATIN.matcher(normalized).replaceAll("");
        
        // Supprimer les tirets multiples
        normalized = normalized.replaceAll("-+", "-");
        
        // Supprimer les tirets en début et fin
        normalized = EDGESDASHES.matcher(normalized).replaceAll("");
        
        // Limiter la longueur (max 100 caractères pour le SEO)
        if (normalized.length() > 100) {
            normalized = normalized.substring(0, 100);
            // S'assurer qu'on ne coupe pas au milieu d'un mot
            int lastDash = normalized.lastIndexOf('-');
            if (lastDash > 80) {
                normalized = normalized.substring(0, lastDash);
            }
        }
        
        return normalized;
    }

    /**
     * Génère un slug unique en ajoutant un identifiant si nécessaire
     * 
     * @param title Le titre
     * @param id L'identifiant de la propriété (optionnel)
     * @return Le slug avec ID si nécessaire
     */
    public String generateUniqueSlug(String title, Long id) {
        String baseSlug = generateSlug(title);
        
        if (baseSlug.isEmpty()) {
            return id != null ? "property-" + id : "property";
        }
        
        // Ajouter l'ID à la fin pour garantir l'unicité
        return baseSlug + "-" + id;
    }

    /**
     * Génère un slug pour une propriété avec toutes les informations pertinentes
     * Format: type-ville-pieces-slug
     * Exemple: "appartement-paris-3-pieces-luxe-123"
     * 
     * @param type Type de propriété (APARTMENT, HOUSE, etc.)
     * @param city Ville
     * @param bedrooms Nombre de chambres
     * @param title Titre de la propriété
     * @param id ID de la propriété
     * @return Slug complet optimisé pour le SEO
     */
    public String generatePropertySlug(String type, String city, Integer bedrooms, String title, Long id) {
        StringBuilder slug = new StringBuilder();
        
        // Ajouter le type
        if (type != null && !type.isEmpty()) {
            slug.append(generateSlug(type)).append("-");
        }
        
        // Ajouter la ville
        if (city != null && !city.isEmpty()) {
            slug.append(generateSlug(city)).append("-");
        }
        
        // Ajouter le nombre de chambres si disponible
        if (bedrooms != null && bedrooms > 0) {
            slug.append(bedrooms).append("-pieces-");
        }
        
        // Ajouter le slug du titre (limité)
        String titleSlug = generateSlug(title);
        if (titleSlug.length() > 30) {
            titleSlug = titleSlug.substring(0, 30);
            int lastDash = titleSlug.lastIndexOf('-');
            if (lastDash > 20) {
                titleSlug = titleSlug.substring(0, lastDash);
            }
        }
        slug.append(titleSlug);
        
        // Ajouter l'ID pour l'unicité
        if (id != null) {
            slug.append("-").append(id);
        }
        
        String finalSlug = slug.toString();
        
        // Nettoyer les tirets multiples et en début/fin
        finalSlug = finalSlug.replaceAll("-+", "-");
        finalSlug = EDGESDASHES.matcher(finalSlug).replaceAll("");
        
        // Limiter à 150 caractères max
        if (finalSlug.length() > 150) {
            finalSlug = finalSlug.substring(0, 150);
            int lastDash = finalSlug.lastIndexOf('-');
            if (lastDash > 120) {
                finalSlug = finalSlug.substring(0, lastDash);
            }
        }
        
        return finalSlug;
    }
}

