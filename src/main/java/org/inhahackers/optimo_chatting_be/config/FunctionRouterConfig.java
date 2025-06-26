package org.inhahackers.optimo_chatting_be.config;

import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FunctionRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> customFunctionRouter(FunctionCatalog catalog) {
        return route(GET("/api/chattings"), request -> {
            @SuppressWarnings("unchecked")
            Function<ServerRequest, Mono<ServerResponse>> func =
                    (Function<ServerRequest, Mono<ServerResponse>>) catalog.lookup("getAllChattingsWithoutChatListFunction");
            return func.apply(request);
        }).andRoute(GET("/api/chattings/detail"), request -> {
            @SuppressWarnings("unchecked")
            Function<ServerRequest, Mono<ServerResponse>> func =
                    (Function<ServerRequest, Mono<ServerResponse>>) catalog.lookup("getAllChattingsFunction");
            return func.apply(request);
        }).andRoute(GET("/api/chattings/{id}"), request -> {
            @SuppressWarnings("unchecked")
            Function<ServerRequest, Mono<ServerResponse>> func =
                    (Function<ServerRequest, Mono<ServerResponse>>) catalog.lookup("getChattingByIdFunction");
            return func.apply(request);
        }).andRoute(DELETE("/api/chattings/{id}"), request -> {
            @SuppressWarnings("unchecked")
            Function<ServerRequest, Mono<ServerResponse>> func =
                    (Function<ServerRequest, Mono<ServerResponse>>) catalog.lookup("deleteChattingByIdFunction");
            return func.apply(request);
        });
    }
}

