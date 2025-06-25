package org.inhahackers.optimo_chatting_be.util;

import org.inhahackers.optimo_chatting_be.exception.InvalidAuthorizationHeaderException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;

public class AuthorizationHeaderUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    public static String extractToken(ServerRequest.Headers headers) {
        String authHeader = headers.firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidAuthorizationHeaderException("Missing or invalid Authorization header");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}