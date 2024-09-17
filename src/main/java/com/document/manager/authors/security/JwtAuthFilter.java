package com.document.manager.authors.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Value("${security.jwt.exclude-paths}")
    private List<String> excludePaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var requestPath = request.getServletPath();
        if (containsExcludedPaths(request, response, filterChain, requestPath)) return;
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // Extract token
                username = jwtTokenGenerator.extractUsername(token); // Extract username from token
            }
            // If the token is valid and no authentication is set in the context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);
                // Validate token and set authentication
                    if (jwtTokenGenerator.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
            }
        }catch (ExpiredJwtException ex){
            logger.error("JWT expired:", ex);
            expiredTokenResponse(response);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private boolean containsExcludedPaths(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String requestPath) throws IOException, ServletException {
        if (excludePaths.contains(requestPath) && HttpMethod.POST.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private static void expiredTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "Expired Token Error! Token is expired, kindly request a new token.");
        new ObjectMapper().writeValue(response.getWriter(), errorDetails);
    }
}
