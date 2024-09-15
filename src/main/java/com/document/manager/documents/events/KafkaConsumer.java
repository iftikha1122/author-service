package com.document.manager.documents.events;

import com.document.manager.documents.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {


    private final DocumentRepository documentRepository;


    @KafkaListener(topics = "author-deletion", groupId = "document-service")
    public void consume(String authorName) {
        log.info("deleting all the docs for author: {}", authorName);
       /** var docs = documentRepository.findByAuthorUsername(authorName);
        if (Optional.ofNullable(docs).isPresent()) {
            try {
                documentRepository.deleteAll(docs);
            } catch (RuntimeException ex) {
                log.error("Error! deleting docs", ex);
            }
        }
        **/


    }

}