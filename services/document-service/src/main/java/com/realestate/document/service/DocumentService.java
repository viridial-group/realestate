package com.realestate.document.service;

import com.realestate.common.event.DocumentUploadedEvent;
import com.realestate.document.entity.Document;
import com.realestate.document.entity.Storage;
import com.realestate.document.repository.DocumentRepository;
import com.realestate.document.repository.StorageRepository;
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
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final StorageRepository storageRepository;
    private final DocumentEventProducer eventProducer;

    @Value("${storage.base-path:./storage}")
    private String baseStoragePath;

    @Value("${storage.allowed-types:image/*,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,text/*}")
    private String allowedTypes;

    public DocumentService(
            DocumentRepository documentRepository,
            StorageRepository storageRepository,
            DocumentEventProducer eventProducer) {
        this.documentRepository = documentRepository;
        this.storageRepository = storageRepository;
        this.eventProducer = eventProducer;
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

        // Sauvegarder le fichier
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("File saved successfully: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save file: {}", filePath, e);
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        // Déterminer le type de document
        String documentType = determineDocumentType(file.getContentType());

        // Créer l'entité Document
        Document document = new Document();
        document.setName(originalFilename);
        document.setDescription(description);
        document.setType(documentType);
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
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

