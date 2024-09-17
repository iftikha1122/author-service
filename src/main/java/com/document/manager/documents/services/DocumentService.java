package com.document.manager.documents.services;




import com.document.manager.documents.api.requests.CreateDocumentDto;
import com.document.manager.documents.api.requests.UpdateDocumentDto;
import com.document.manager.documents.api.responses.DocumentResponse;

import java.util.List;
import java.util.Optional;

public interface DocumentService {
    DocumentResponse save(CreateDocumentDto createRequest);

    List<DocumentResponse> all(int page, int size);

    DocumentResponse get(Long id);

    Optional<DocumentResponse> update(UpdateDocumentDto updateRequest, Long id);

    void delete(Long id);
}
