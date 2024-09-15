package com.document.manager.documents.domain;


import com.document.manager.authors.domain.Author;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "DOCUMENTS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "BODY", columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "id")
    private Author author;

    @ElementCollection
    @CollectionTable(name = "DOCUMENT_REFERENCES", joinColumns = @JoinColumn(name = "DOCUMENT_ID"))
    @Column(name = "REFERENCE")
    private Set<String> references = new HashSet<>();

    public void updateDocument(String title, String body, Author author, Set<String> references) {
        if (!Objects.isNull(title) && !title.trim().isEmpty()) this.title = title;
        if (!Objects.isNull(body)) this.body = body;
        if (!Objects.isNull(author)) this.author = author;
        if (!Objects.isNull(references)) this.references = references;
    }
}
