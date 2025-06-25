package org.inhahackers.optimo_chatting_be.function;

import org.inhahackers.optimo_chatting_be.exception.InvalidAuthorizationHeaderException;
import org.inhahackers.optimo_chatting_be.exception.JwtAuthenticationException;
import org.inhahackers.optimo_chatting_be.service.ChattingService;
import org.inhahackers.optimo_chatting_be.service.JwtTokenVerifyService;
import org.inhahackers.optimo_chatting_be.util.AuthorizationHeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component("getChattingByIdFunction")
public class GetChattingByIdFunction implements Function<ServerRequest, Mono<ServerResponse>> {
    private final ChattingService chattingService;
    private final JwtTokenVerifyService jwtTokenVerifyService;

    public GetChattingByIdFunction(ChattingService chattingService, JwtTokenVerifyService jwtTokenVerifyService) {
        this.chattingService = chattingService;
        this.jwtTokenVerifyService = jwtTokenVerifyService;
    }

    @Override
    public Mono<ServerResponse> apply(ServerRequest request) {
        String accessToken;
        try {
            accessToken = AuthorizationHeaderUtil.extractToken(request.headers());
        } catch (InvalidAuthorizationHeaderException e) {
            return ServerResponse.badRequest().bodyValue(e.getMessage());
        }

        try {
            Long userId = jwtTokenVerifyService.extractUserId(accessToken);
            String chattingId = request.pathVariable("id");
            // 채팅 조회 시 발생하는 예외를 try-catch로 감싸서 처리
            return Mono.fromCallable(() -> chattingService.findById(chattingId, userId.toString()))
                    .flatMap(chatting -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(chatting))
                    .onErrorResume(e -> {
                        if (e instanceof IllegalArgumentException) {
                            return ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue("Chatting not found: " + e.getMessage());
                        } else if (e instanceof SecurityException) {
                            return ServerResponse.status(HttpStatus.FORBIDDEN)
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue("Forbidden: " + e.getMessage());
                        } else {
                            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue("Internal server error: " + e.getMessage());
                        }
                    });
        } catch (JwtAuthenticationException e) { // 추가된 예외 처리
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("Authentication failed: " + e.getMessage());
        }
    }
}
