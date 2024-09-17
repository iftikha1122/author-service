package com.document.manager.authors.api;

import com.document.manager.authors.api.request.CreateAuthorDto;
import com.document.manager.authors.api.request.UpdateAuthorDto;
import com.document.manager.authors.api.responses.AuthorResponse;
import com.document.manager.authors.services.AuthorService;
import com.document.manager.authors.validation.ValidUsername;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "Authors service")

public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Add a new Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid Input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody CreateAuthorDto createAuthorDTO) {
        log.debug("Create author Api:{}", createAuthorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.save(createAuthorDTO));
    }


    @Operation(summary = "Get all Authors")
    @ApiResponse(responseCode = "200", description = "List of all authors")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AuthorResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0")
            @PositiveOrZero(message = "Page number must be zero or a positive integer") Integer page,
            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 10")
            @Max(value = 100, message = "Size must not exceed 100") Integer size) {

        log.debug("Fetch all authors Api called with params page:{} and size:{}", page, size);
        return ResponseEntity.ok(authorService.all(page, size));
    }

    @Operation(summary = "Get Author by User Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthorResponse> get(@PathVariable(name = "username") @Valid @ValidUsername String userName) {

        log.debug("Fetch author by user name api called with UserName:{} ", userName);
        return ResponseEntity.ok(authorService.get(userName));
    }


    //Only admin can update authors
    @Operation(summary = "Update Author by User Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated"),
            @ApiResponse(responseCode = "204", description = "No content Idempotent"),

    })
    @PutMapping("/{username}")
    public ResponseEntity<AuthorResponse> update(@RequestBody UpdateAuthorDto updateDto,
                                                 @PathVariable("username") String userName) {
        log.debug("update author by user name api called with request:{} ", updateDto);
        var updateResponse = authorService.update(updateDto, userName);
        return updateResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    //only author itself and admin can delete author this api
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userName == authentication.name")
    @Operation(summary = "Delete Author by User Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Idempotent no response"),
    })
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("username") String userName) {
        log.debug("delete author by user name api called with username:{} ", userName);
        authorService.delete(userName);
        return ResponseEntity.noContent().build();
    }

}
