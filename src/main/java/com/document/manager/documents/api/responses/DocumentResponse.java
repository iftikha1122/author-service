package com.document.manager.documents.api.responses;

import com.document.manager.authors.api.responses.AuthorResponse;
import com.document.manager.documents.domain.Document;

import java.util.Set;

public record DocumentResponse(Long id, String title, String body, AuthorResponse author, Set<String> references) {

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getBody(),
                AuthorResponse.from(document.getAuthor()),
                document.getReferences()
        );
    }
}
