package com.document.manager.documents.api.requests;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateDocumentDto(
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
        String title,

        String body,

        String authorUsername,

        Set<String> references
) {
}
