package com.realestate.document.controller;

import com.realestate.document.dto.DocumentDTO;
import com.realestate.document.entity.Document;
import com.realestate.document.mapper.DocumentMapper;
import com.realestate.document.service.DocumentService;
import com.realestate.document.service.ImageOptimizationService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document and media file management API")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;
    private final ImageOptimizationService imageOptimizationService;

    public DocumentController(
            DocumentService documentService, 
            DocumentMapper documentMapper,
            ImageOptimizationService imageOptimizationService) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
        this.imageOptimizationService = imageOptimizationService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload document", description = "Uploads a new document or media file")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam("createdBy") Long createdBy,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long resourceId,
            @RequestParam(required = false) String description) {
        try {
            Document document = documentService.uploadDocument(
                    file, organizationId, createdBy, propertyId, resourceId, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(documentMapper.toDTO(document));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Returns document metadata for a specific document ID")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        return ResponseEntity.ok(documentMapper.toDTO(document));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download document", description = "Downloads the actual file for a specific document ID")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            HttpServletResponse response) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Document", id));

            Path filePath = documentService.getDocumentPath(document);
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = document.getMimeType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + document.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "List documents", description = "Returns a list of documents filtered by organization, property, or resource")
    public ResponseEntity<List<DocumentDTO>> getDocuments(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long resourceId) {
        List<Document> documents;

        if (propertyId != null) {
            documents = documentService.getDocumentsByPropertyId(propertyId);
        } else if (resourceId != null) {
            documents = documentService.getDocumentsByResourceId(resourceId);
        } else if (organizationId != null) {
            documents = documentService.getDocumentsByOrganizationId(organizationId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<DocumentDTO> documentDTOs = documents.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documentDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Updates document metadata for a specific document ID")
    public ResponseEntity<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @RequestBody DocumentDTO documentDTO) {
        Document document = documentMapper.toEntity(documentDTO);
        Document updated = documentService.updateDocument(id, document);
        return ResponseEntity.ok(documentMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes a document and its file from the system")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/webp")
    @Operation(summary = "Get document as WebP", description = "Returns the document converted to WebP format if it's an image")
    public ResponseEntity<byte[]> getDocumentAsWebP(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Document", id));

            // Vérifier si c'est une image
            if (document.getMimeType() == null || !document.getMimeType().startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            // Lire le fichier original
            Path filePath = documentService.getDocumentPath(document);
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Convertir en WebP
            try (java.io.InputStream inputStream = java.nio.file.Files.newInputStream(filePath)) {
                byte[] webpBytes = imageOptimizationService.convertToWebP(
                        inputStream, 
                        document.getMimeType()
                );

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("image/webp"))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // Cache 1 an
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline; filename=\"" + document.getName().replaceFirst("\\.[^.]+$", ".webp") + "\"")
                        .body(webpBytes);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/optimized")
    @Operation(summary = "Get optimized image", description = "Returns an optimized version of the image with optional width parameter")
    public ResponseEntity<byte[]> getOptimizedImage(
            @PathVariable Long id,
            @RequestParam(required = false) Integer width) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Document", id));

            if (document.getMimeType() == null || !document.getMimeType().startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = documentService.getDocumentPath(document);
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Lire et optimiser l'image
            try (java.io.InputStream inputStream = java.nio.file.Files.newInputStream(filePath)) {
                byte[] optimizedBytes;
                
                if (width != null && width > 0) {
                    // Optimiser avec une largeur spécifique
                    optimizedBytes = imageOptimizationService.optimizeImageWithWidth(
                            inputStream, 
                            document.getMimeType(), 
                            width
                    );
                } else {
                    // Optimisation standard
                    optimizedBytes = imageOptimizationService.optimizeImage(
                            inputStream, 
                            document.getMimeType()
                    );
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(document.getMimeType()))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline; filename=\"" + document.getName() + "\"")
                        .body(optimizedBytes);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
