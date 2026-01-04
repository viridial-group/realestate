package com.realestate.property.service;

import com.realestate.property.entity.DVFTransaction;
import com.realestate.property.entity.DVFImportHistory;
import com.realestate.property.repository.DVFTransactionRepository;
import com.realestate.property.repository.DVFImportHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service pour télécharger et parser les fichiers DVF depuis data.gouv.fr
 * 
 * Documentation: https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/
 */
@Service
public class DVFService {

    private static final Logger logger = LoggerFactory.getLogger(DVFService.class);
    private static final String DVF_BASE_URL = "https://files.data.gouv.fr/geo-dvf/latest/csv/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DVFTransactionRepository dvfTransactionRepository;
    private final DVFImportHistoryRepository importHistoryRepository;
    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${dvf.enabled:false}")
    private boolean dvfEnabled;

    @Value("${dvf.max-transactions-per-batch:1000}")
    private int maxTransactionsPerBatch;

    public DVFService(
            DVFTransactionRepository dvfTransactionRepository,
            DVFImportHistoryRepository importHistoryRepository,
            @Autowired(required = false) WebClient.Builder webClientBuilder,
            @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.dvfTransactionRepository = dvfTransactionRepository;
        this.importHistoryRepository = importHistoryRepository;
        this.messagingTemplate = messagingTemplate;
        this.webClient = webClientBuilder != null 
            ? webClientBuilder.baseUrl(DVF_BASE_URL).build()
            : WebClient.builder().baseUrl(DVF_BASE_URL).build();
    }

    /**
     * Télécharge et importe les données DVF pour une année donnée
     * 
     * @param year Année (ex: 2024)
     * @param department Code département (ex: "75" pour Paris, "13" pour Bouches-du-Rhône)
     * @param userId ID de l'utilisateur qui a démarré l'import
     * @return Nombre de transactions importées
     */
    @Async
    @Transactional
    public CompletableFuture<Integer> importDVFData(String year, String department, Long userId) {
        if (!dvfEnabled) {
            logger.warn("DVF service is disabled. Set dvf.enabled=true to enable.");
            return CompletableFuture.completedFuture(0);
        }

        logger.info("Starting DVF import for year {} and department {}", year, department);
        
        // Créer l'entrée d'historique
        DVFImportHistory importHistory = new DVFImportHistory(year, department, "EN_COURS", userId);
        importHistory = importHistoryRepository.save(importHistory);
        
        try {
            String fileName = String.format("dvf-%s-%s.csv", year, department);
            String url = DVF_BASE_URL + fileName;
            
            logger.info("Downloading DVF file from: {}", url);
            
            byte[] fileContent = webClient.get()
                .uri(fileName)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

            if (fileContent == null || fileContent.length == 0) {
                logger.warn("Empty file downloaded from {}", url);
                return CompletableFuture.completedFuture(0);
            }

            logger.info("File downloaded, size: {} bytes. Starting parsing...", fileContent.length);
            
            List<DVFTransaction> transactions = parseDVFFile(fileContent, year);
            
            logger.info("Parsed {} transactions. Starting batch insert...", transactions.size());
            
            // Insertion par batch pour éviter les problèmes de mémoire
            int saved = 0;
            for (int i = 0; i < transactions.size(); i += maxTransactionsPerBatch) {
                int end = Math.min(i + maxTransactionsPerBatch, transactions.size());
                List<DVFTransaction> batch = transactions.subList(i, end);
                dvfTransactionRepository.saveAll(batch);
                saved += batch.size();
                logger.info("Saved batch {}/{} ({} transactions)", 
                    (i / maxTransactionsPerBatch) + 1, 
                    (transactions.size() / maxTransactionsPerBatch) + 1,
                    saved);
            }

            logger.info("Successfully imported {} DVF transactions for year {} and department {}", 
                saved, year, department);
            
            // Mettre à jour l'historique
            importHistory.setStatus("TERMINÉ");
            importHistory.setTransactionCount((long) saved);
            importHistory.setCompletedAt(java.time.LocalDateTime.now());
            importHistoryRepository.save(importHistory);
            
            // Envoyer une notification WebSocket
            if (messagingTemplate != null && userId != null) {
                messagingTemplate.convertAndSend("/topic/dvf-import/" + userId, 
                    java.util.Map.of(
                        "status", "TERMINÉ",
                        "year", year,
                        "department", department,
                        "transactionCount", saved,
                        "message", String.format("Import terminé: %d transactions importées pour %s/%s", saved, year, department)
                    ));
            }
            
            return CompletableFuture.completedFuture(saved);
            
        } catch (Exception e) {
            logger.error("Error importing DVF data for year {} and department {}: {}", 
                year, department, e.getMessage(), e);
            
            // Mettre à jour l'historique avec l'erreur
            importHistory.setStatus("ERREUR");
            importHistory.setErrorMessage(e.getMessage());
            importHistory.setCompletedAt(java.time.LocalDateTime.now());
            importHistoryRepository.save(importHistory);
            
            // Envoyer une notification d'erreur
            if (messagingTemplate != null && userId != null) {
                messagingTemplate.convertAndSend("/topic/dvf-import/" + userId,
                    java.util.Map.of(
                        "status", "ERREUR",
                        "year", year,
                        "department", department,
                        "error", e.getMessage(),
                        "message", String.format("Erreur lors de l'import pour %s/%s: %s", year, department, e.getMessage())
                    ));
            }
            
            return CompletableFuture.completedFuture(0);
        }
    }

    /**
     * Parse un fichier DVF au format CSV avec séparateur |
     * 
     * Structure du fichier DVF (colonnes principales):
     * - Date mutation, Nature mutation, Valeur foncière, No disposition, 
     * - Lot 1-5 (Numéro, Surface Carrez), Nombre de lots, Code type local, Type local,
     * - Surface réelle bati, Nombre pièces principales, Code nature culture, Nature culture,
     * - Surface terrain, Longitude, Latitude, Code voie, Type voie, Voie,
     * - Code postal, Commune, Code commune, Code département, etc.
     */
    private List<DVFTransaction> parseDVFFile(byte[] fileContent, String millesime) {
        List<DVFTransaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new java.io.ByteArrayInputStream(fileContent), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                logger.warn("Empty DVF file");
                return transactions;
            }
            
            // Parser les en-têtes pour connaître l'ordre des colonnes
            String[] headers = headerLine.split("\\|");
            int lineNumber = 1;
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    DVFTransaction transaction = parseDVFLine(line, headers, millesime);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    logger.debug("Error parsing line {}: {}", lineNumber, e.getMessage());
                    // Continue avec la ligne suivante
                }
                
                // Log progress tous les 10000 lignes
                if (lineNumber % 10000 == 0) {
                    logger.debug("Parsed {} lines, {} valid transactions so far", lineNumber, transactions.size());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing DVF file: {}", e.getMessage(), e);
        }
        
        return transactions;
    }

    /**
     * Parse une ligne du fichier DVF
     */
    private DVFTransaction parseDVFLine(String line, String[] headers, String millesime) {
        String[] values = line.split("\\|", -1); // -1 pour garder les valeurs vides
        
        if (values.length < headers.length) {
            return null;
        }
        
        DVFTransaction transaction = new DVFTransaction();
        transaction.setMillesime(millesime);
        
        try {
            // Mapper les colonnes (simplifié - à adapter selon la structure exacte du fichier DVF)
            for (int i = 0; i < headers.length && i < values.length; i++) {
                String header = headers[i].trim();
                String value = values[i].trim();
                
                if (value.isEmpty()) {
                    continue;
                }
                
                try {
                    switch (header.toLowerCase()) {
                        case "date mutation":
                            transaction.setMutationDate(parseDate(value));
                            break;
                        case "nature mutation":
                            transaction.setNatureMutation(value);
                            break;
                        case "valeur fonciere":
                            transaction.setValeurFonciere(parseDecimal(value));
                            break;
                        case "no disposition":
                            transaction.setNumeroDisposition(parseInteger(value));
                            break;
                        case "lot1_numero":
                        case "lot 1 numero":
                            transaction.setLot1Numero(value);
                            break;
                        case "lot1_surface_carrez":
                        case "lot 1 surface carrez":
                            transaction.setLot1SurfaceCarrez(parseDecimal(value));
                            break;
                        case "code_type_local":
                        case "code type local":
                            transaction.setCodeTypeLocal(parseInteger(value));
                            break;
                        case "type_local":
                        case "type local":
                            transaction.setTypeLocal(value);
                            break;
                        case "surface_reelle_bati":
                        case "surface reelle bati":
                            transaction.setSurfaceReelleBati(parseDecimal(value));
                            break;
                        case "nombre_pieces_principales":
                        case "nombre pieces principales":
                            transaction.setNombrePiecesPrincipales(parseInteger(value));
                            break;
                        case "surface_terrain":
                        case "surface terrain":
                            transaction.setSurfaceTerrain(parseDecimal(value));
                            break;
                        case "longitude":
                            transaction.setLongitude(parseDecimal(value));
                            break;
                        case "latitude":
                            transaction.setLatitude(parseDecimal(value));
                            break;
                        case "code_postal":
                        case "code postal":
                            transaction.setCodePostal(value);
                            break;
                        case "commune":
                            transaction.setCommune(value);
                            break;
                        case "code_commune":
                        case "code commune":
                            transaction.setCommuneCode(value);
                            break;
                        case "code_departement":
                        case "code departement":
                            transaction.setCodeDepartement(value);
                            break;
                    }
                } catch (Exception e) {
                    logger.debug("Error parsing field {} with value {}: {}", header, value, e.getMessage());
                }
            }
            
            // Calculer le prix au m²
            transaction.calculatePrixMetreCarre();
            
            return transaction;
            
        } catch (Exception e) {
            logger.debug("Error parsing DVF line: {}", e.getMessage());
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            if (value.length() == 10) {
                return LocalDate.parse(value, DATE_FORMATTER);
            }
            // Autres formats possibles
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseDecimal(String value) {
        try {
            // Remplacer les virgules par des points et supprimer les espaces
            String cleaned = value.replace(",", ".").replaceAll("\\s+", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Nettoie les anciennes données DVF pour une année donnée
     */
    @Transactional
    public void cleanOldData(String year) {
        logger.info("Cleaning old DVF data for year {}", year);
        long deleted = dvfTransactionRepository.findAll().stream()
            .filter(t -> year.equals(t.getMillesime()))
            .mapToLong(t -> {
                dvfTransactionRepository.delete(t);
                return 1;
            })
            .sum();
        logger.info("Deleted {} old transactions for year {}", deleted, year);
    }
}

