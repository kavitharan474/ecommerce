package com.ecommerce.productservice.logging;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger("productRequestLogger");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Extract traceId and spanId
        String traceId = Optional.ofNullable(request.getHeaders().getFirst("X-B3-TraceId"))
                .orElse(UUID.randomUUID().toString());
        String spanId = Optional.ofNullable(request.getHeaders().getFirst("X-B3-SpanId"))
                .orElse(UUID.randomUUID().toString());

        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        String ip = Optional.ofNullable(request.getHeaders().getFirst("X-Forwarded-For"))
                .orElse(request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown");

        logger.info("Incoming request - IP: {}, Method: {}, Path: {}, Headers: {}",
                ip,
                request.getMethod(),
                request.getURI().getPath(),
                request.getHeaders());

        return chain.filter(exchange)
                .doOnTerminate(MDC::clear);
    }
}
