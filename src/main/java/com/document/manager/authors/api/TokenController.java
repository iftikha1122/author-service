package com.document.manager.authors.api;

import com.document.manager.authors.api.request.TokenRequest;
import com.document.manager.authors.api.responses.TokenResponse;
import com.document.manager.authors.security.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
@Slf4j
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<TokenResponse> authenticateAndGetToken(@RequestBody TokenRequest tokenRequest) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequest.userName(), tokenRequest.password())
        );
        if (authentication.isAuthenticated()) {
            var token = jwtTokenGenerator.generateToken(tokenRequest.userName());
            return ResponseEntity.ok(TokenResponse.from(token));
        } else {
            throw new UsernameNotFoundException("Invalid login request!");
        }
    }
}
