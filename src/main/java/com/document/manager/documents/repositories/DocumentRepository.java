package com.document.manager.documents.repositories;



import com.document.manager.documents.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findAll(Pageable pageable);

    Optional<Document> findByTitle(String title);

 /**   @Query("SELECT d FROM Document d JOIN d.author a WHERE a.username = :authorUserName")
    List<Document> findByAuthorUsername(@Param("authorUserName") String authorUserName);
 **/
}
