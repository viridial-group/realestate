package com.realestate.document.controller;

import com.realestate.document.entity.Document;
import com.realestate.document.entity.Storage;
import com.realestate.document.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    private Document createTestDocument() {
        Document document = new Document();
        document.setId(1L);
        document.setName("test-image.jpg");
        document.setDescription("Test image");
        document.setType("IMAGE");
        document.setMimeType("image/jpeg");
        document.setFileSize(1024L);
        document.setFilePath("100/test-file.jpg");
        document.setOrganizationId(100L);
        document.setCreatedBy(1L);
        document.setPropertyId(1L);
        document.setActive(true);
        return document;
    }

    @Test
    void testUploadDocument_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test content".getBytes());
        Document document = createTestDocument();

        when(documentService.uploadDocument(
                any(), eq(100L), eq(1L), eq(1L), eq(null), eq(null)))
                .thenReturn(document);

        // When & Then
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("organizationId", "100")
                        .param("createdBy", "1")
                        .param("propertyId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-image.jpg"))
                .andExpect(jsonPath("$.type").value("IMAGE"));
    }

    @Test
    void testGetDocumentById_Success() throws Exception {
        // Given
        Long id = 1L;
        Document document = createTestDocument();
        when(documentService.getDocumentById(id)).thenReturn(Optional.of(document));

        // When & Then
        mockMvc.perform(get("/api/documents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test-image.jpg"));
    }

    @Test
    void testGetDocumentById_NotFound() throws Exception {
        // Given
        Long id = 999L;
        when(documentService.getDocumentById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/documents/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetDocumentsByOrganizationId_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        List<Document> documents = Arrays.asList(createTestDocument());
        when(documentService.getDocumentsByOrganizationId(organizationId)).thenReturn(documents);

        // When & Then
        mockMvc.perform(get("/api/documents")
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetDocumentsByPropertyId_Success() throws Exception {
        // Given
        Long propertyId = 1L;
        List<Document> documents = Arrays.asList(createTestDocument());
        when(documentService.getDocumentsByPropertyId(propertyId)).thenReturn(documents);

        // When & Then
        mockMvc.perform(get("/api/documents")
                        .param("propertyId", propertyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateDocument_Success() throws Exception {
        // Given
        Long id = 1L;
        Document updateDetails = createTestDocument();
        updateDetails.setName("Updated Name");
        Document updated = createTestDocument();
        updated.setName("Updated Name");

        when(documentService.updateDocument(eq(id), any(Document.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/documents/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"description\":\"Test image\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteDocument_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(documentService).deleteDocument(id);

        // When & Then
        mockMvc.perform(delete("/api/documents/{id}", id))
                .andExpect(status().isNoContent());
    }
}

