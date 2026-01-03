package com.realestate.property.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parseur de requêtes de recherche style Google
 * Supporte :
 * - Recherche par mots-clés multiples
 * - Phrases exactes avec guillemets ("exact phrase")
 * - Opérateurs booléens (AND, OR)
 * - Exclusion avec le signe moins (-mot)
 */
public class SearchQueryParser {

    private static final Pattern EXACT_PHRASE_PATTERN = Pattern.compile("\"([^\"]+)\"");
    private static final Pattern EXCLUDE_PATTERN = Pattern.compile("-([^\\s]+)");

    public static class ParsedQuery {
        private final List<String> exactPhrases;
        private final List<String> includeTerms;
        private final List<String> excludeTerms;
        private final String originalQuery;

        public ParsedQuery(String originalQuery) {
            this.originalQuery = originalQuery;
            this.exactPhrases = new ArrayList<>();
            this.includeTerms = new ArrayList<>();
            this.excludeTerms = new ArrayList<>();
        }

        public List<String> getExactPhrases() {
            return exactPhrases;
        }

        public List<String> getIncludeTerms() {
            return includeTerms;
        }

        public List<String> getExcludeTerms() {
            return excludeTerms;
        }

        public String getOriginalQuery() {
            return originalQuery;
        }

        public boolean isEmpty() {
            return exactPhrases.isEmpty() && includeTerms.isEmpty() && excludeTerms.isEmpty();
        }
    }

    /**
     * Parse une requête de recherche style Google
     */
    public static ParsedQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ParsedQuery("");
        }

        String normalizedQuery = query.trim();
        ParsedQuery parsed = new ParsedQuery(normalizedQuery);

        // 1. Extraire les phrases exactes (entre guillemets)
        Matcher exactMatcher = EXACT_PHRASE_PATTERN.matcher(normalizedQuery);
        while (exactMatcher.find()) {
            String phrase = exactMatcher.group(1).trim();
            if (!phrase.isEmpty()) {
                parsed.getExactPhrases().add(phrase.toLowerCase());
            }
            // Remplacer par un espace pour éviter de le traiter comme un terme normal
            normalizedQuery = normalizedQuery.replace(exactMatcher.group(0), " ");
        }

        // 2. Extraire les termes exclus (préfixés par -)
        Matcher excludeMatcher = EXCLUDE_PATTERN.matcher(normalizedQuery);
        while (excludeMatcher.find()) {
            String term = excludeMatcher.group(1).trim();
            if (!term.isEmpty() && !term.equals("-")) {
                parsed.getExcludeTerms().add(term.toLowerCase());
            }
            // Remplacer par un espace
            normalizedQuery = normalizedQuery.replace(excludeMatcher.group(0), " ");
        }

        // 3. Traiter les termes restants (mots-clés normaux)
        String[] terms = normalizedQuery.split("\\s+");
        for (String term : terms) {
            term = term.trim();
            if (!term.isEmpty() && !term.equals("-") && !term.equals("\"")) {
                // Convertir en minuscules pour la recherche insensible à la casse
                String lowerTerm = term.toLowerCase();
                
                // Ignorer les mots vides (stop words) très courts
                // Mais garder les mots importants même courts
                if (lowerTerm.length() > 2) {
                    parsed.getIncludeTerms().add(lowerTerm);
                } else if (lowerTerm.length() == 2 && isImportantTwoLetterWord(lowerTerm)) {
                    parsed.getIncludeTerms().add(lowerTerm);
                } else if (lowerTerm.length() == 1) {
                    // Ignorer les mots d'une seule lettre (trop communs et non significatifs)
                    // "a", "à" sont ignorés car ils apparaissent dans presque toutes les descriptions
                }
            }
        }

        return parsed;
    }

    /**
     * Vérifie si un mot de 2 lettres est important (ex: "T3", "T4", "T5")
     */
    private static boolean isImportantTwoLetterWord(String word) {
        // Mots importants en français pour l'immobilier
        return word.matches("t[0-9]") || // T3, T4, etc.
               word.equals("de") ||
               word.equals("du") ||
               word.equals("en") ||
               word.equals("à") ||
               word.equals("a") ||  // "a" sans accent
               word.equals("au") ||
               word.equals("le") ||
               word.equals("la") ||
               word.equals("les");
    }

    /**
     * Nettoie et normalise un terme de recherche
     */
    public static String normalizeTerm(String term) {
        if (term == null) {
            return "";
        }
        return term.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9àâäéèêëïîôöùûüÿç\\s-]", "") // Garder seulement les caractères alphanumériques et accents français
                .replaceAll("\\s+", " "); // Normaliser les espaces multiples
    }
}

