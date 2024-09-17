package com.document.manager.documents.services.impl;


import com.document.manager.authors.domain.Author;
import com.document.manager.authors.exceptions.AuthorNotFoundException;
import com.document.manager.authors.repositories.AuthorRepository;
import com.document.manager.documents.api.requests.CreateDocumentDto;
import com.document.manager.documents.api.requests.UpdateDocumentDto;
import com.document.manager.documents.api.responses.DocumentResponse;
import com.document.manager.documents.domain.Document;

import com.document.manager.documents.exceptions.DocumentNotFoundException;
import com.document.manager.documents.repositories.DocumentRepository;
import com.document.manager.documents.services.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;

    @Override
    public DocumentResponse save(CreateDocumentDto createRequest) {
        var author = authorRepository.findActiveByUserName(createRequest.authorUsername())
                .orElseThrow(() -> new AuthorNotFoundException("Author with username '" + createRequest.authorUsername() + "' not found"));
        try {
            Document newDocument = Document.builder()
                    .title(createRequest.title())
                    .body(createRequest.body())
                    .author(author)
                    .references(createRequest.references())
                    .build();

            var savedDocument = documentRepository.save(newDocument);
            return DocumentResponse.from(savedDocument);
        } catch (RuntimeException ex) {
            log.error("Error! saving document", ex);
            throw ex;
        }
    }

    @Override
    public List<DocumentResponse> all(int page, int size) {
        var pageable = PageRequest.of(page, size);
        try {
            return documentRepository.findAll(pageable)
                    .stream()
                    .map(DocumentResponse::from)
                    .toList();
        } catch (RuntimeException ex) {
            log.error("Error! fetching documents", ex);
            throw ex;
        }
    }

    @Override
    public DocumentResponse get(Long id) {
        return documentRepository.findById(id)
                .map(DocumentResponse::from)
                .orElseThrow(() -> new DocumentNotFoundException(String.format("Document with id '%s' not found", id)));
    }

    @Override
    public Optional<DocumentResponse> update(UpdateDocumentDto updateRequest, Long id) {
        return documentRepository.findById(id)
                .map(existingDocument -> {
                    Author author = updateRequest.authorUsername() != null
                            ? authorRepository.findActiveByUserName(updateRequest.authorUsername())
                            .orElseThrow(() -> new AuthorNotFoundException("Author with username '" + updateRequest.authorUsername() + "' not found"))
                            : existingDocument.getAuthor();

                    existingDocument.updateDocument(updateRequest.title(), updateRequest.body(), author, updateRequest.references());
                    return documentRepository.save(existingDocument);
                })
                .map(DocumentResponse::from);
    }

    @Override
    public void delete(Long id) {
        documentRepository.findById(id).ifPresent(documentRepository::delete);
    }
}
