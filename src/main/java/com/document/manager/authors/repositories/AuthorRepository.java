package com.document.manager.authors.repositories;

import com.document.manager.authors.domain.Author;
import com.document.manager.authors.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long>{

    @Query("SELECT a FROM Author a WHERE a.userName = :userName AND a.status = 'ACTIVE'")
    Optional<Author> findActiveByUserName(@Param("userName") String userName);

    Page<Author> findAllByStatus(Status status, Pageable pageable);



}
