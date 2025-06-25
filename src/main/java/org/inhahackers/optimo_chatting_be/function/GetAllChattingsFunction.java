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

@Component("getAllChattingsFunction")
public class GetAllChattingsFunction implements Function<ServerRequest, Mono<ServerResponse>> {

    private final ChattingService chattingService;
    private final JwtTokenVerifyService jwtTokenVerifyService;

    public GetAllChattingsFunction(ChattingService chattingService, JwtTokenVerifyService jwtTokenVerifyService) {
        this.chattingService = chattingService;
        this.jwtTokenVerifyService = jwtTokenVerifyService;
    }

    @Override
    public Mono<ServerResponse> apply(ServerRequest request) {
        try {
            String accessToken = AuthorizationHeaderUtil.extractToken(request.headers());
            Long userId = jwtTokenVerifyService.extractUserId(accessToken);

            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(chattingService.findAllByOwnerIdWithoutChatList(userId.toString()));

        } catch (InvalidAuthorizationHeaderException | NumberFormatException e) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("Invalid request: " + e.getMessage());

        } catch (JwtAuthenticationException e) { // 분리된 예외 처리
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("Authentication failed: " + e.getMessage());
        }
    }
}
