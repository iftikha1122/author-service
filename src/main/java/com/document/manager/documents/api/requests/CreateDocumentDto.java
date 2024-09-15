package com.document.manager.documents.api.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateDocumentDto(
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
        String title,

        @NotBlank(message = "Body is required")
        String body,

        @NotBlank(message = "Author username is required")
        String authorUsername,

        Set<String> references
) {
}
