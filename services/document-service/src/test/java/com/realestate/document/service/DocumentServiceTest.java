package com.realestate.document.service;

import com.realestate.document.entity.Document;
import com.realestate.document.entity.Storage;
import com.realestate.document.repository.DocumentRepository;
import com.realestate.document.repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private StorageRepository storageRepository;

    @InjectMocks
    private DocumentService documentService;

    private Storage testStorage;
    private Document testDocument;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Créer un répertoire temporaire pour les tests
        tempDir = Files.createTempDirectory("document-test");
        ReflectionTestUtils.setField(documentService, "baseStoragePath", tempDir.toString());
        ReflectionTestUtils.setField(documentService, "allowedTypes", 
                "image/jpeg,image/png,image/gif,image/webp,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        testStorage = new Storage();
        testStorage.setId(1L);
        testStorage.setType("FILESYSTEM");
        testStorage.setBasePath(tempDir.toString());
        testStorage.setIsDefault(true);
        testStorage.setActive(true);

        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setName("test-image.jpg");
        testDocument.setType("IMAGE");
        testDocument.setMimeType("image/jpeg");
        testDocument.setFileSize(1024L);
        testDocument.setFilePath("100/test-file.jpg");
        testDocument.setOrganizationId(100L);
        testDocument.setCreatedBy(1L);
        testDocument.setActive(true);
    }

    @Test
    void testUploadDocument_Success() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test content".getBytes());
        Long organizationId = 100L;
        Long createdBy = 1L;

        when(storageRepository.findDefaultStorage()).thenReturn(Optional.of(testStorage));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // When
        Document result = documentService.uploadDocument(
                file, organizationId, createdBy, null, null, null);

        // Then
        assertNotNull(result);
        verify(storageRepository).findDefaultStorage();
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testUploadDocument_InvalidFileType() {
        // Given
        // Créer un fichier avec un type vraiment non autorisé (video au lieu d'image/document)
        // Le type "video/mp4" n'est pas dans la liste autorisée
        MultipartFile file = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "test content".getBytes());
        Long organizationId = 100L;
        Long createdBy = 1L;

        // When & Then
        // La validation du type se fait en premier et doit lever une IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
                documentService.uploadDocument(file, organizationId, createdBy, null, null, null));
        
        assertTrue(exception.getMessage().contains("not allowed"));
        
        // Vérifier que le storage n'a pas été appelé car la validation échoue avant
        verify(storageRepository, never()).findDefaultStorage();
    }

    @Test
    void testUploadDocument_FileTooLarge() {
        // Given
        byte[] largeContent = new byte[51 * 1024 * 1024]; // 51MB
        MultipartFile file = new MockMultipartFile(
                "file", "large-image.jpg", "image/jpeg", largeContent);
        Long organizationId = 100L;
        Long createdBy = 1L;

        // When & Then
        // La validation de la taille se fait après la validation du type, mais avant l'accès au storage
        assertThrows(IllegalArgumentException.class, () -> 
                documentService.uploadDocument(file, organizationId, createdBy, null, null, null));
        
        // Vérifier que le storage n'a pas été appelé
        verify(storageRepository, never()).findDefaultStorage();
    }

    @Test
    void testGetDocumentById_Success() {
        // Given
        Long id = 1L;
        when(documentRepository.findById(id)).thenReturn(Optional.of(testDocument));

        // When
        Optional<Document> result = documentService.getDocumentById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDocument.getId(), result.get().getId());
        verify(documentRepository).findById(id);
    }

    @Test
    void testGetDocumentsByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        List<Document> documents = Arrays.asList(testDocument);
        when(documentRepository.findActiveByOrganizationId(organizationId)).thenReturn(documents);

        // When
        List<Document> result = documentService.getDocumentsByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(documentRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testGetDocumentsByPropertyId_Success() {
        // Given
        Long propertyId = 1L;
        List<Document> documents = Arrays.asList(testDocument);
        when(documentRepository.findActiveByPropertyId(propertyId)).thenReturn(documents);

        // When
        List<Document> result = documentService.getDocumentsByPropertyId(propertyId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(documentRepository).findActiveByPropertyId(propertyId);
    }

    @Test
    void testUpdateDocument_Success() {
        // Given
        Long id = 1L;
        Document updateDetails = new Document();
        updateDetails.setName("Updated Name");
        updateDetails.setDescription("Updated Description");

        when(documentRepository.findById(id)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // When
        Document result = documentService.updateDocument(id, updateDetails);

        // Then
        assertNotNull(result);
        verify(documentRepository).findById(id);
        verify(documentRepository).save(testDocument);
    }

    @Test
    void testUpdateDocument_NotFound() {
        // Given
        Long id = 999L;
        Document updateDetails = new Document();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> documentService.updateDocument(id, updateDetails));
        verify(documentRepository).findById(id);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void testDeleteDocument_Success() throws IOException {
        // Given
        Long id = 1L;
        Path filePath = tempDir.resolve("100").resolve("test-file.jpg");
        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);

        when(documentRepository.findById(id)).thenReturn(Optional.of(testDocument));
        when(storageRepository.findDefaultStorage()).thenReturn(Optional.of(testStorage));
        doNothing().when(documentRepository).delete(any(Document.class));

        // When
        assertDoesNotThrow(() -> documentService.deleteDocument(id));

        // Then
        verify(documentRepository).findById(id);
        verify(documentRepository).delete(testDocument);
    }
}

