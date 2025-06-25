package org.inhahackers.optimo_chatting_be.function;

import org.inhahackers.optimo_chatting_be.exception.InvalidAuthorizationHeaderException;
import org.inhahackers.optimo_chatting_be.exception.JwtAuthenticationException;
import org.inhahackers.optimo_chatting_be.service.ChattingService;
import org.inhahackers.optimo_chatting_be.service.JwtTokenVerifyService;
import org.inhahackers.optimo_chatting_be.util.AuthorizationHeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component("deleteChattingByIdFunction")
public class DeleteChattingByIdFunction implements Function<ServerRequest, Mono<ServerResponse>> {
    private final ChattingService chattingService;
    private final JwtTokenVerifyService jwtTokenVerifyService;

    public DeleteChattingByIdFunction(ChattingService chattingService, JwtTokenVerifyService jwtTokenVerifyService) {
        this.chattingService = chattingService;
        this.jwtTokenVerifyService = jwtTokenVerifyService;
    }

    @Override
    public Mono<ServerResponse> apply(ServerRequest request) {
        try {
            String accessToken = AuthorizationHeaderUtil.extractToken(request.headers());

            Long userId = jwtTokenVerifyService.extractUserId(accessToken);

            String chattingId = request.pathVariable("id");

            chattingService.deleteById(chattingId, userId.toString());

            return ServerResponse.noContent().build();

        } catch (InvalidAuthorizationHeaderException e) {
            return ServerResponse.badRequest().bodyValue("Authorization 헤더가 잘못되었습니다: " + e.getMessage());
        } catch (JwtAuthenticationException e) { // 추가된 예외 처리
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue("인증 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ServerResponse.badRequest().bodyValue("채팅이 존재하지 않습니다: " + e.getMessage());

        } catch (SecurityException e) {
            return ServerResponse.status(403).bodyValue("삭제 권한이 없습니다: " + e.getMessage());

        } catch (Exception e) {
            return ServerResponse.status(500).bodyValue("서버 내부 오류: " + e.getMessage());
        }
    }
}
