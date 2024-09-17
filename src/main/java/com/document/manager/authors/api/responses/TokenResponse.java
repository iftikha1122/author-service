package com.document.manager.authors.api.responses;

public record TokenResponse(String response) {

    public static TokenResponse from(String token){
        return new TokenResponse(token);
    }
}
