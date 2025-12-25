package com.realestate.common.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.realestate.common.service.CurrentUserService;

/**
 * Service pour la gestion de l'internationalisation (i18n)
 * Récupère les messages traduits selon la langue préférée de l'utilisateur
 */
@Service
public class I18nService {

    private final MessageSource messageSource;
    private final CurrentUserService currentUserService;

    public I18nService(MessageSource messageSource, CurrentUserService currentUserService) {
        this.messageSource = messageSource;
        this.currentUserService = currentUserService;
    }

    /**
     * Récupère un message traduit selon la langue de l'utilisateur connecté
     * 
     * @param code Code du message (clé dans les fichiers messages.properties)
     * @param args Arguments pour le message (optionnel)
     * @return Message traduit
     */
    public String getMessage(String code, Object... args) {
        Locale locale = getCurrentUserLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * Récupère un message traduit avec une valeur par défaut
     * 
     * @param code Code du message
     * @param defaultMessage Message par défaut si la clé n'existe pas
     * @param args Arguments pour le message
     * @return Message traduit
     */
    public String getMessage(String code, String defaultMessage, Object... args) {
        Locale locale = getCurrentUserLocale();
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    /**
     * Récupère la locale de l'utilisateur connecté
     * Priorité :
     * 1. Langue préférée de l'utilisateur (depuis le JWT)
     * 2. Locale du contexte HTTP (Accept-Language header)
     * 3. Locale par défaut (fr)
     */
    private Locale getCurrentUserLocale() {
        try {
            // Essayer de récupérer la langue préférée depuis le JWT
            com.realestate.common.security.JwtPrincipal principal = currentUserService.getCurrentPrincipal();
            if (principal != null && principal.getPreferredLanguage() != null 
                    && !principal.getPreferredLanguage().isEmpty()) {
                Locale userLocale = Locale.forLanguageTag(principal.getPreferredLanguage());
                // Valider que la locale est supportée
                if (isLocaleSupported(userLocale)) {
                    return userLocale;
                }
            }
        } catch (Exception e) {
            // Si l'utilisateur n'est pas connecté ou erreur, utiliser la locale du contexte
            // Ignorer l'exception et continuer avec la locale du contexte
        }

        // Utiliser la locale du contexte HTTP (Accept-Language header)
        Locale contextLocale = LocaleContextHolder.getLocale();
        if (contextLocale != null && !contextLocale.getLanguage().isEmpty() 
                && isLocaleSupported(contextLocale)) {
            return contextLocale;
        }

        // Par défaut: français
        return Locale.FRENCH;
    }

    /**
     * Vérifie si une locale est supportée
     */
    private boolean isLocaleSupported(Locale locale) {
        String lang = locale.getLanguage();
        return lang.equals("fr") || lang.equals("en") || lang.equals("es") 
                || lang.equals("de") || lang.equals("it");
    }

    /**
     * Récupère la locale depuis une chaîne de langue (ex: "fr", "en", "es")
     */
    public static Locale getLocaleFromLanguage(String language) {
        if (language == null || language.isEmpty()) {
            return Locale.FRENCH;
        }
        return Locale.forLanguageTag(language);
    }
}

