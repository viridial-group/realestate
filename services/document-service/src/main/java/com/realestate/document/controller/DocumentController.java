package com.realestate.document.controller;

import com.realestate.document.entity.Document;
import com.realestate.document.service.DocumentService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document and media file management API")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload document", description = "Uploads a new document or media file")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam("createdBy") Long createdBy,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long resourceId,
            @RequestParam(required = false) String description) {
        try {
            Document document = documentService.uploadDocument(
                    file, organizationId, createdBy, propertyId, resourceId, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Returns document metadata for a specific document ID")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download document", description = "Downloads the actual file for a specific document ID")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            HttpServletResponse response) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

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
    public ResponseEntity<List<Document>> getDocuments(
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

        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Updates document metadata for a specific document ID")
    public ResponseEntity<Document> updateDocument(
            @PathVariable Long id,
            @RequestBody Document documentDetails) {
        try {
            Document updated = documentService.updateDocument(id, documentDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes a document and its file from the system")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

