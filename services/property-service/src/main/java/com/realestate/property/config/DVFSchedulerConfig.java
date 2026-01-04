package com.realestate.property.config;

import com.realestate.property.service.DVFService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration pour la synchronisation automatique des données DVF
 * 
 * Les fichiers DVF sont mis à jour semestriellement (avril et octobre)
 * selon https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/
 */
@Component
@ConditionalOnProperty(name = "dvf.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class DVFSchedulerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DVFSchedulerConfig.class);

    private final DVFService dvfService;

    @Value("${dvf.scheduler.departments:75,13,69,33,31,59,44,67,92,93,94}")
    private String departmentsConfig;

    @Autowired
    public DVFSchedulerConfig(DVFService dvfService) {
        this.dvfService = dvfService;
    }

    /**
     * Synchronise les données DVF tous les 6 mois (avril et octobre)
     * Exécuté le 15 avril et le 15 octobre à 2h du matin
     */
    @Scheduled(cron = "0 0 2 15 4,10 *")
    public void syncDVFData() {
        logger.info("Starting scheduled DVF data synchronization");

        int currentYear = LocalDate.now().getYear();
        List<String> departments = Arrays.asList(departmentsConfig.split(","));

        for (String dept : departments) {
            final String department = dept.trim();
            final String year = String.valueOf(currentYear);
            logger.info("Syncing DVF data for department {} and year {}", department, currentYear);
            
            try {
                // Nettoyer les anciennes données
                dvfService.cleanOldData(year);
                
                // Importer les nouvelles données (userId = null pour imports automatiques)
                dvfService.importDVFData(year, department, null)
                    .thenAccept(count -> logger.info("Imported {} transactions for department {}", count, department))
                    .exceptionally(e -> {
                        logger.error("Error importing DVF data for department {}: {}", department, e.getMessage());
                        return null;
                    });
                
            } catch (Exception e) {
                logger.error("Error syncing DVF data for department {}: {}", department, e.getMessage(), e);
            }
        }

        logger.info("DVF data synchronization completed");
    }

    /**
     * Synchronise les données pour les 5 dernières années (une seule fois au démarrage)
     * Peut être déclenché manuellement via un endpoint admin
     */
    public void syncHistoricalData(int years) {
        logger.info("Starting historical DVF data synchronization for {} years", years);

        int currentYear = LocalDate.now().getYear();
        List<String> departments = Arrays.asList(departmentsConfig.split(","));

        for (int y = currentYear - years; y <= currentYear; y++) {
            final int year = y;
            final String yearStr = String.valueOf(year);
            for (String dept : departments) {
                final String department = dept.trim();
                logger.info("Syncing historical DVF data for department {} and year {}", department, year);
                
                try {
                    dvfService.importDVFData(yearStr, department, null)
                        .thenAccept(count -> logger.info("Imported {} transactions for department {} and year {}", 
                            count, department, year))
                        .exceptionally(e -> {
                            logger.error("Error importing historical DVF data for department {} and year {}: {}", 
                                department, year, e.getMessage());
                            return null;
                        });
                } catch (Exception e) {
                    logger.error("Error syncing historical DVF data for department {} and year {}: {}", 
                        department, year, e.getMessage(), e);
                }
            }
        }

        logger.info("Historical DVF data synchronization completed");
    }
}

