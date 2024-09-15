package com.document.manager.authors.services.impl;

import com.document.manager.authors.api.request.CreateAuthorDto;
import com.document.manager.authors.api.request.UpdateAuthorDto;
import com.document.manager.authors.api.responses.AuthorResponse;
import com.document.manager.authors.event.KafkaProducer;
import com.document.manager.authors.domain.Author;
import com.document.manager.authors.domain.Status;
import com.document.manager.authors.exceptions.AuthorNotFoundException;
import com.document.manager.authors.exceptions.AuthorRegistrationException;
import com.document.manager.authors.repositories.AuthorRepository;
import com.document.manager.authors.services.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final KafkaProducer kafkaProducer;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthorResponse save(CreateAuthorDto createRequest) {
        try {
            var authorExist = authorRepository.findActiveByUserName(createRequest.userName());
            if (authorExist.isPresent()) throw new RuntimeException("User name already exist");

            var newAuthor = authorRepository.save(Author.from(createRequest,passwordEncoder.encode(createRequest.password())));
            log.debug("new author added successfully: {}", newAuthor);
            return AuthorResponse.from(newAuthor);
        } catch (RuntimeException ex) {
            log.error("Error while creating new author", ex);
            throw new AuthorRegistrationException(ex.getMessage());
        }

    }

    @Override
    public List<AuthorResponse> all(Integer page, Integer size) {
        try {
            var pageable = PageRequest.of(page, size);
            return authorRepository.findAllByStatus(Status.ACTIVE,pageable).getContent().stream().map(AuthorResponse::from).toList();
        } catch (RuntimeException ex) {
            log.error("Error: while fetching all the author names", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AuthorResponse get(String userName) {
        try {
            var author = authorRepository.findActiveByUserName(userName)
                    .orElseThrow(() -> new AuthorNotFoundException(String.format("Author:%s not found", userName)));
            return AuthorResponse.from(author);
        } catch (Exception ex) {
            log.error("Error while fetching the author with username:{}", userName);
            throw ex;
        }
    }


    @Override
    public Optional<AuthorResponse> update(UpdateAuthorDto updateAuthorDto, String userName) {
        try {
            var author = authorRepository.findActiveByUserName(userName);

            if (author.isEmpty()) {
                //  PUT is idempotent, returning Optional.empty() to indicate no content update
                return Optional.empty();
            }
            // If the author is found, update and return the response
            return author
                    .map(existingAuthor -> {
                        existingAuthor.updateAuthor(updateAuthorDto);
                        return authorRepository.save(existingAuthor);
                    })
                    .map(AuthorResponse::from);

        } catch (RuntimeException ex) {
            log.error("Error while updating the author information", ex);
            return Optional.empty(); //  empty in case of unexpected errors
        }



    }

    @Override
    public void delete(String userName) {
        try {
            var author = authorRepository.findActiveByUserName(userName);
             author.ifPresent(auth ->{
                 auth.markDeleted();
                 authorRepository.save(auth);
                 kafkaProducer.sendMessage(userName);

             });
             //delete method is idempotent will not return any error if
            // resource do not exists

        } catch (RuntimeException ex) {
            log.error("Error! while update the author information", ex);

        }

    }
}
