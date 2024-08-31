package api_gateway.demo.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter implements WebFilter {

    @Autowired
    private JwtUtils jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = extractTokenFromCookie(request);

        if (token != null && jwtUtil.validateJwtToken(token)) {
            String email = jwtUtil.getEmailFromJwtToken(token);
            Authentication auth = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        return chain.filter(exchange);
    }

    private String extractTokenFromCookie(ServerHttpRequest request) {
        HttpCookie jwtCookie = request.getCookies().getFirst("jwt");
        return jwtCookie != null ? jwtCookie.getValue() : null;
    }
}
