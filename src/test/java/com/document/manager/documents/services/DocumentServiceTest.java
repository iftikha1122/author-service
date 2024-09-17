package com.document.manager.documents.services;

import com.document.manager.authors.domain.Author;
import com.document.manager.authors.exceptions.AuthorNotFoundException;
import com.document.manager.authors.repositories.AuthorRepository;
import com.document.manager.documents.api.requests.CreateDocumentDto;
import com.document.manager.documents.api.requests.UpdateDocumentDto;
import com.document.manager.documents.api.responses.DocumentResponse;
import com.document.manager.documents.domain.Document;
import com.document.manager.documents.exceptions.DocumentNotFoundException;
import com.document.manager.documents.repositories.DocumentRepository;
import com.document.manager.documents.services.impl.DocumentServiceImpl;
import com.document.manager.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSaveDocumentSuccessfully() {
        // Given
        CreateDocumentDto createRequest = new CreateDocumentDto("title", "body", "username", Set.of("reference"));
        var author = TestUtils.getFakeAuthor();
        Document document = Document.builder().id(1L).title("title").body("body").author(author).build();
        when(authorRepository.findActiveByUserName("username")).thenReturn(Optional.of(author));
        when(documentRepository.save(ArgumentMatchers.any(Document.class))).thenReturn(document);
        // When
        DocumentResponse response = documentService.save(createRequest);
        // Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("title");
        assertThat(response.body()).isEqualTo("body");
        verify(documentRepository, times(1)).save(ArgumentMatchers.any(Document.class));
    }


    @Test
    public void shouldThrowAuthorNotFoundExceptionWhenAuthorDoesNotExist() {
        // Given
        CreateDocumentDto createRequest = new CreateDocumentDto("title", "body", "username", Set.of("reference"));
        when(authorRepository.findActiveByUserName("username")).thenReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() -> documentService.save(createRequest))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessage("Author with username 'username' not found");
    }

    @Test
    public void shouldRetrieveDocumentSuccessfully() {
        // Given
        Long documentId = 1L;
        Document document = Document.builder().id(documentId).title("title").body("body").author(TestUtils.getFakeAuthor()).build();
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        // When
        DocumentResponse response = documentService.get(documentId);
        // Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("title");
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    public void shouldThrowDocumentNotFoundExceptionWhenDocumentDoesNotExist() {
        // Given
        Long documentId = 1L;
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() -> documentService.get(documentId))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessage(String.format("Document with id '%s' not found", documentId));
    }

    @Test
    public void shouldUpdateDocumentSuccessfully() {
        // Given
        Long documentId = 1L;
        UpdateDocumentDto updateRequest = new UpdateDocumentDto("newTitle", "newBody", "username", Set.of("newReference"));
        Author author = TestUtils.getFakeAuthor(); // Populate as needed
        Document existingDocument = Document.builder().id(documentId).title("oldTitle").body("oldBody").build();
        Document updatedDocument = Document.builder().id(documentId).title("newTitle").body("newBody").author(author).build();
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(existingDocument));
        when(authorRepository.findActiveByUserName("username")).thenReturn(Optional.of(author));
        when(documentRepository.save(existingDocument)).thenReturn(updatedDocument);
        // When
        Optional<DocumentResponse> response = documentService.update(updateRequest, documentId);
        // Then
        assertThat(response).isPresent();
        assertThat(response.get().title()).isEqualTo("newTitle");
        assertThat(response.get().body()).isEqualTo("newBody");
        verify(documentRepository, times(1)).save(existingDocument);
    }

    @Test
    public void shouldThrowAuthorNotFoundExceptionWhenUpdatingWithNonExistentAuthor() {
        // Given
        Long documentId = 1L;
        UpdateDocumentDto updateRequest = new UpdateDocumentDto("newTitle", "newBody", "username", Set.of("newReference"));
        Document existingDocument = Document.builder().id(documentId).title("oldTitle").body("oldBody").build();
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(existingDocument));
        when(authorRepository.findActiveByUserName("username")).thenReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() -> documentService.update(updateRequest, documentId))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessage("Author with username 'username' not found");
    }

    @Test
    public void shouldDeleteDocumentSuccessfully() {
        // Given
        Long documentId = 1L;
        Document document = Document.builder().id(documentId).title("title").body("body").build();
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        // When
        documentService.delete(documentId);
        // Then
        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    public void shouldNotDeleteDocumentWhenDocumentDoesNotExist() {
        // Given
        Long documentId = 1L;
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());
        // When
        documentService.delete(documentId);
        // Then
        verify(documentRepository, times(0)).delete(ArgumentMatchers.any(Document.class));
    }


}