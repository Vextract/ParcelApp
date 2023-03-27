package com.mikhail.test.gateway.config.security;

import com.mikhail.test.gateway.config.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class MyFilter implements GatewayFilterFactory<MyFilter.Config> {

    private final JwtTokenUtil jwtUtil;

    private final RouterValidator routerValidator;

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus)  {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    private boolean isAuthorizationValid(String authorizationHeader, URI uri) {

        String token = authorizationHeader.substring(7);

        Claims claims = jwtUtil.extractAllClaims(token);
        String role = (String) claims.get("role");

        return uri.toString().contains("admin") && role.equals("[ROLE_ADMIN]") ||
                uri.toString().contains("user") && role.equals("[ROLE_USER]") ||
                uri.toString().contains("courier") && role.equals("[ROLE_COURIER]");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (routerValidator.isSecured.test(request)) {

                if (!request.getURI().toString().startsWith("http://localhost:4000")) {
                    return this.onError(exchange, "Invalid URI", HttpStatus.UNAUTHORIZED);
                }

                if (!request.getHeaders().containsKey("Authorization")) {
                    return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authorizationHeader = request.getHeaders().get("Authorization").get(0);

                if (!this.isAuthorizationValid(authorizationHeader, request.getURI())) {
                    return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                }

            }
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {

    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }
}
