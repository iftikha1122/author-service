package com.document.manager.authors.exceptions;

public class AuthorNotFoundException extends RuntimeException{
    public AuthorNotFoundException(String message){
        super(message);
    }
}
