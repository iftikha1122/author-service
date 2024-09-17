package com.document.manager.authors.exceptions;


import com.document.manager.documents.exceptions.DocumentNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorRegistrationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(AuthorRegistrationException ex) {

        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", ex.getMessage());
        return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(HandlerMethodValidationException ex) {

        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", "page and size should be non negative and valid");
        return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(AuthorNotFoundException ex) {
        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", ex.getMessage());
        return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(UsernameNotFoundException ex) {
        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", ex.getMessage());
        return new ResponseEntity<>(reason, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(ExpiredJwtException ex) {
        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", "Expired Token Error!Token is expired kindly request the new token");
        return new ResponseEntity<>(reason, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(RuntimeException ex) {
        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", "Server error try later");
        log.error("Runtime Error!",ex);
        return new ResponseEntity<>(reason, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(DocumentNotFoundException ex) {
        Map<String,String> reason = HashMap.newHashMap(1);
        reason.put("message", ex.getMessage());
        return new ResponseEntity<>(reason, HttpStatus.NOT_FOUND);
    }



    //


}//
