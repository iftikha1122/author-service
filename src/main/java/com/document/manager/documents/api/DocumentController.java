package com.document.manager.documents.api;

import com.document.manager.documents.api.requests.CreateDocumentDto;
import com.document.manager.documents.api.requests.UpdateDocumentDto;
import com.document.manager.documents.api.responses.DocumentResponse;
import com.document.manager.documents.services.DocumentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@Slf4j
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Add a new Document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Document successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid Input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody CreateDocumentDto createRequest) {
        log.debug("Create document API called: {}", createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.save(createRequest));
    }

    @Operation(summary = "Get all Documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all documents")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DocumentResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0")
            @PositiveOrZero(message = "Page number must be zero or a positive integer") Integer page,
            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 10")
            @Max(value = 100, message = "Size must not exceed 100") Integer size) {

        log.debug("Fetch all documents API called with params page: {} and size: {}", page, size);
        return ResponseEntity.ok(documentService.all(page, size));
    }

    @Operation(summary = "Get Document by Title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DocumentResponse> get(@PathVariable(name = "id") Long id) {
        log.debug("Fetch document by title API called with title: {}", id);
        return ResponseEntity.ok(documentService.get(id));
    }

    @Operation(summary = "Update Document by Title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated"),
            @ApiResponse(responseCode = "204", description = "No content - Idempotent"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> update(@Valid @RequestBody UpdateDocumentDto updateRequest,
                                                   @PathVariable("id") Long id) {
        log.debug("Update document API called with request: {} and title: {}", updateRequest, id);
        var updatedResponse = documentService.update(updateRequest, id);
        return updatedResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Delete Document by Title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted (Idempotent)"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.debug("Delete document API called with title: {}", id);
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
