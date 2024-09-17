package com.document.manager.utils;

import com.document.manager.authors.domain.Author;
import com.document.manager.authors.domain.Status;
import com.document.manager.documents.api.requests.CreateDocumentDto;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestUtils {

    public static Author getFakeAuthor(){
        var author = new Author();
        author.setId(100L);
        author.setFirstName("fake");
        author.setLastName("author");
        author.setUserName("fake_userName");
        author.setStatus(Status.ACTIVE);

        return author;
    }

    @NotNull
    public static CreateDocumentDto getCreateDocumentRequestDto() {
        return new CreateDocumentDto("title1",
                "body1",
                "u0007",
                Set.of("ref1"));
    }
}
