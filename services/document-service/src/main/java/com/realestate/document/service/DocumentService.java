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

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageRepository storageRepository;
    private final DocumentEventProducer eventProducer;

    @Value("${storage.base-path:/var/realestate/storage}")
    private String baseStoragePath;

    @Value("${storage.allowed-types:image/jpeg,image/png,image/gif,image/webp,application/pdf}")
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
                    Storage defaultStorage = new Storage("FILESYSTEM", baseStoragePath);
                    defaultStorage.setIsDefault(true);
                    return storageRepository.save(defaultStorage);
                });

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Créer le répertoire de l'organisation si nécessaire
        Path orgPath = Paths.get(storage.getBasePath(), organizationId.toString());
        Files.createDirectories(orgPath);

        // Chemin complet du fichier
        Path filePath = orgPath.resolve(uniqueFilename);

        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Déterminer le type de document
        String documentType = determineDocumentType(file.getContentType());

        // Créer l'entité Document
        Document document = new Document();
        document.setName(originalFilename);
        document.setDescription(description);
        document.setType(documentType);
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(filePath.toString().replace(storage.getBasePath() + "/", ""));
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
        return Paths.get(storage.getBasePath(), document.getFilePath());
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
            if (contentType.trim().equals(allowedType.trim()) || 
                contentType.startsWith(allowedType.trim().split("/")[0] + "/")) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
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

