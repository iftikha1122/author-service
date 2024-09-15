package com.document.manager.authors.api.responses;


import com.document.manager.authors.domain.Author;
import lombok.Builder;
@Builder
public record AuthorResponse( Long authorId, String firstName, String lastName, String userName ) {

    public static AuthorResponse from(Author newAuthor) {
        return AuthorResponse.builder().authorId(newAuthor.getId())
                .firstName(newAuthor.getFirstName())
                .lastName(newAuthor.getLastName())
                .userName(newAuthor.getUserName())
                .build();
    }
}
