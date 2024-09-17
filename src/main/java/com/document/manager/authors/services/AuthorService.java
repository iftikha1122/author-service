package com.document.manager.authors.services;

import com.document.manager.authors.api.request.CreateAuthorDto;
import com.document.manager.authors.api.request.UpdateAuthorDto;
import com.document.manager.authors.api.responses.AuthorResponse;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    AuthorResponse save(CreateAuthorDto createRequest);
    List<AuthorResponse> all(Integer page, Integer size);
    AuthorResponse get(String userName);
    Optional<AuthorResponse> update(UpdateAuthorDto updateAuthorDto, String userName);
    void delete(String userName);

}
