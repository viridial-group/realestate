package com.realestate.document.service;

import com.realestate.common.event.DocumentUploadedEvent;
import com.realestate.document.entity.Document;
import com.realestate.document.entity.Storage;
import com.realestate.document.repository.DocumentRepository;
import com.realestate.document.repository.StorageRepository;
import com.realestate.document.specification.DocumentSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final StorageRepository storageRepository;
    private final DocumentEventProducer eventProducer;
    private final ImageOptimizationService imageOptimizationService;

    @Value("${storage.base-path:./storage}")
    private String baseStoragePath;

    @Value("${storage.allowed-types:image/*,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,text/*}")
    private String allowedTypes;

    public DocumentService(
            DocumentRepository documentRepository,
            StorageRepository storageRepository,
            DocumentEventProducer eventProducer,
            ImageOptimizationService imageOptimizationService) {
        this.documentRepository = documentRepository;
        this.storageRepository = storageRepository;
        this.eventProducer = eventProducer;
        this.imageOptimizationService = imageOptimizationService;
    }

    @Transactional
    public Document uploadDocument(
            MultipartFile file,
            Long organizationId,
            Long createdBy,
            Long propertyId,
            Long resourceId,
            String description) throws IOException {
        
        // Validation du type de fichier
        validateFileType(file.getContentType());
        
        // Validation de la taille
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB max
            throw new IllegalArgumentException("File size exceeds maximum allowed size (50MB)");
        }

        // Obtenir le stockage par défaut
        Storage storage = storageRepository.findDefaultStorage()
                .orElseGet(() -> {
                    logger.info("No default storage found, creating one with base path: {}", baseStoragePath);
                    Storage defaultStorage = new Storage("FILESYSTEM", baseStoragePath);
                    defaultStorage.setIsDefault(true);
                    defaultStorage.setActive(true);
                    defaultStorage.setDescription("Default filesystem storage");
                    try {
                        Storage saved = storageRepository.save(defaultStorage);
                        logger.info("Default storage created successfully with ID: {}", saved.getId());
                        return saved;
                    } catch (Exception e) {
                        logger.error("Failed to create default storage", e);
                        throw new RuntimeException("Failed to create default storage: " + e.getMessage(), e);
                    }
                });
        
        // Mettre à jour le chemin de base si nécessaire (pour éviter les problèmes de permissions)
        if (storage.getBasePath() != null && (storage.getBasePath().startsWith("/var/") || storage.getBasePath().startsWith("/usr/"))) {
            logger.warn("Storage base path is in system directory ({}), updating to relative path: {}", storage.getBasePath(), baseStoragePath);
            storage.setBasePath(baseStoragePath);
            storage = storageRepository.save(storage);
            logger.info("Storage base path updated successfully");
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Créer le répertoire de l'organisation si nécessaire
        // Utiliser un chemin absolu pour éviter les problèmes de permissions
        String storagePath = storage.getBasePath();
        if (storagePath == null || storagePath.isEmpty()) {
            storagePath = baseStoragePath;
        }
        // Convertir le chemin relatif en absolu si nécessaire
        Path basePath = Paths.get(storagePath);
        if (!basePath.isAbsolute()) {
            basePath = Paths.get(System.getProperty("user.dir"), storagePath).toAbsolutePath().normalize();
        }
        Path orgPath = basePath.resolve(organizationId.toString());
        try {
            Files.createDirectories(orgPath);
            logger.debug("Created directory: {}", orgPath);
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", orgPath, e);
            throw new IOException("Failed to create storage directory: " + e.getMessage() + ". Path: " + orgPath, e);
        }

        // Chemin complet du fichier
        Path filePath = orgPath.resolve(uniqueFilename);

        // Optimiser l'image si nécessaire
        String finalMimeType = file.getContentType();
        long finalFileSize = file.getSize();
        
        if (imageOptimizationService.shouldOptimize(file.getContentType(), file.getSize())) {
            try {
                logger.info("Optimizing image: {} ({} bytes)", originalFilename, file.getSize());
                byte[] optimizedBytes = imageOptimizationService.optimizeImage(file.getInputStream(), file.getContentType());
                finalFileSize = optimizedBytes.length;
                
                // Mettre à jour le type MIME si l'image a été convertie en JPEG
                // (Thumbnailator convertit souvent en JPEG pour une meilleure compression)
                if (file.getContentType() != null && file.getContentType().startsWith("image/") && 
                    !file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/jpg")) {
                    // Si l'image n'est pas déjà JPEG, on peut la convertir pour une meilleure compression
                    // Mais on garde le format original pour préserver la transparence si c'est un PNG
                    if (!file.getContentType().equals("image/png") && !file.getContentType().equals("image/gif")) {
                        // Pour les autres formats, on peut convertir en JPEG
                        fileExtension = ".jpg";
                        uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                        filePath = orgPath.resolve(uniqueFilename);
                        finalMimeType = "image/jpeg";
                    }
                }
                
                // Sauvegarder le fichier optimisé
                Files.write(filePath, optimizedBytes);
                logger.info("Image optimized and saved: {} bytes -> {} bytes", file.getSize(), finalFileSize);
            } catch (Exception e) {
                logger.error("Error optimizing image, saving original: {}", e.getMessage(), e);
                // En cas d'erreur, sauvegarder le fichier original
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ioException) {
                    logger.error("Failed to save original file after optimization error: {}", ioException.getMessage(), ioException);
                    throw new IOException("Failed to save file: " + ioException.getMessage(), ioException);
                }
            }
        } else {
            // Sauvegarder le fichier original sans optimisation
            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("File saved successfully: {}", filePath);
            } catch (IOException e) {
                logger.error("Failed to save file: {}", filePath, e);
                throw new IOException("Failed to save file: " + e.getMessage(), e);
            }
        }

        // Déterminer le type de document
        String documentType = determineDocumentType(finalMimeType);

        // Créer l'entité Document
        Document document = new Document();
        document.setName(originalFilename);
        document.setDescription(description);
        document.setType(documentType);
        document.setMimeType(finalMimeType);
        document.setFileSize(finalFileSize);
        // Stocker le chemin relatif au répertoire de base
        String relativePath = basePath.relativize(filePath).toString().replace("\\", "/");
        document.setFilePath(relativePath);
        document.setOrganizationId(organizationId);
        document.setCreatedBy(createdBy);
        document.setPropertyId(propertyId);
        document.setResourceId(resourceId);
        document.setActive(true);

        Document saved = documentRepository.save(document);
        
        // Publish event to Kafka
        String targetType = propertyId != null ? "PROPERTY" : (resourceId != null ? "RESOURCE" : "OTHER");
        Long targetId = propertyId != null ? propertyId : resourceId;
        DocumentUploadedEvent event = new DocumentUploadedEvent(
                organizationId,
                createdBy,
                saved.getId(),
                saved.getName(),
                saved.getType(),
                saved.getFileSize(),
                targetType,
                targetId
        );
        eventProducer.publishDocumentUploaded(event);
        
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByOrganizationId(Long organizationId) {
        return documentRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByPropertyId(Long propertyId) {
        return documentRepository.findActiveByPropertyId(propertyId);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByResourceId(Long resourceId) {
        return documentRepository.findActiveByResourceId(resourceId);
    }

    /**
     * Récupérer les documents avec filtres et permissions utilisateur
     * Filtre automatiquement selon les organisations accessibles (incluant sous-organisations)
     */
    @Transactional(readOnly = true)
    public List<Document> getDocumentsWithPermissions(
            Long userId,
            Set<Long> accessibleOrganizationIds,
            Long organizationId,
            Long propertyId,
            Long resourceId) {
        
        Specification<Document> spec = Specification.where(null);

        // Si l'utilisateur n'est pas super admin, appliquer les filtres de permissions
        if (userId != null && accessibleOrganizationIds != null && !accessibleOrganizationIds.isEmpty()) {
            // Filtrer selon les permissions : documents créés par l'utilisateur OU dans ses organisations
            spec = spec.and(DocumentSpecification.accessibleByUser(userId, accessibleOrganizationIds));
        } else if (userId != null) {
            // Si pas d'organisations, seulement les documents créés par l'utilisateur
            spec = spec.and(DocumentSpecification.hasCreatedBy(userId));
        }

        // Appliquer les autres filtres
        if (organizationId != null) {
            spec = spec.and(DocumentSpecification.hasOrganization(organizationId));
        }
        if (propertyId != null) {
            spec = spec.and(DocumentSpecification.hasProperty(propertyId));
        }
        if (resourceId != null) {
            spec = spec.and(DocumentSpecification.hasResource(resourceId));
        }

        // Seulement les documents actifs
        spec = spec.and(DocumentSpecification.isActive(true));

        return documentRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public Path getDocumentPath(Document document) {
        Storage storage = storageRepository.findDefaultStorage()
                .orElseThrow(() -> new RuntimeException("Default storage not found"));
        
        String storagePath = storage.getBasePath();
        if (storagePath == null || storagePath.isEmpty()) {
            storagePath = baseStoragePath;
        }
        
        // Convertir le chemin relatif en absolu si nécessaire
        Path basePath = Paths.get(storagePath);
        if (!basePath.isAbsolute()) {
            basePath = Paths.get(System.getProperty("user.dir"), storagePath).toAbsolutePath().normalize();
        }
        
        return basePath.resolve(document.getFilePath());
    }

    @Transactional
    public void deleteDocument(Long id) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        // Supprimer le fichier physique
        Path filePath = getDocumentPath(document);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Supprimer l'entité
        documentRepository.delete(document);
    }

    @Transactional
    public Document updateDocument(Long id, Document documentDetails) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        if (documentDetails.getName() != null) {
            document.setName(documentDetails.getName());
        }
        if (documentDetails.getDescription() != null) {
            document.setDescription(documentDetails.getDescription());
        }
        if (documentDetails.getActive() != null) {
            document.setActive(documentDetails.getActive());
        }
        if (documentDetails.getMetadata() != null) {
            document.setMetadata(documentDetails.getMetadata());
        }

        return documentRepository.save(document);
    }

    private void validateFileType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("File type is required");
        }

        String[] allowed = allowedTypes.split(",");
        boolean isAllowed = false;
        for (String allowedType : allowed) {
            String trimmed = allowedType.trim();
            // Support for wildcards like "image/*"
            if (trimmed.endsWith("/*")) {
                String prefix = trimmed.substring(0, trimmed.length() - 2);
                if (contentType.startsWith(prefix + "/")) {
                    isAllowed = true;
                    break;
                }
            } else if (contentType.trim().equals(trimmed)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed: " + contentType + ". Allowed types: " + allowedTypes);
        }
    }

    private String determineDocumentType(String mimeType) {
        if (mimeType == null) {
            return "UNKNOWN";
        }

        if (mimeType.startsWith("image/")) {
            return "IMAGE";
        } else if (mimeType.equals("application/pdf")) {
            return "PDF";
        } else if (mimeType.contains("word") || mimeType.contains("document")) {
            return "DOCUMENT";
        } else {
            return "OTHER";
        }
    }
}

