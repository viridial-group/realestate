package com.realestate.user.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuration pour l'internationalisation (i18n) pour user-service
 * Charge les messages communs (common-lib) et les messages spécifiques au service
 */
@Configuration
public class I18nConfig {

    /**
     * Configuration du MessageSource pour charger les fichiers de traduction
     * Charge d'abord les messages spécifiques, puis les messages communs (fallback)
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Charger les messages spécifiques du service (priorité) et les messages communs (fallback)
        // classpath:messages charge les messages du service courant
        // classpath*:messages charge aussi les messages de common-lib (via dépendance)
        messageSource.setBasenames("classpath:messages", "classpath*:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache 1 heure
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(Locale.FRENCH);
        // Si un message n'est pas trouvé, utiliser le code comme message par défaut
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Configuration du LocaleResolver pour détecter la langue depuis les headers HTTP
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.FRENCH);
        localeResolver.setSupportedLocales(java.util.Arrays.asList(
            Locale.FRENCH,
            Locale.ENGLISH,
            new Locale("es"), // Espagnol
            new Locale("de"), // Allemand
            new Locale("it")  // Italien
        ));
        return localeResolver;
    }
}

