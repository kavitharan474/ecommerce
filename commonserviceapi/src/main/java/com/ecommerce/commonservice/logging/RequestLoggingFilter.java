package com.ecommerce.commonservice.logging;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger("commonRequestLogger");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate or extract traceId
        String traceId = Optional.ofNullable(request.getHeaders().getFirst("X-B3-TraceId"))
                .orElse(UUID.randomUUID().toString());
        String spanId = UUID.randomUUID().toString();

        // Set MDC
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        String ip = Optional.ofNullable(request.getHeaders().getFirst("X-Forwarded-For"))
                .orElse(request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown");

        logger.info("Incoming request - IP: {}, Method: {}, Path: {}, Headers: {}",
                ip,
                request.getMethod(),
                request.getURI().getPath(),
                request.getHeaders());

        // Mutate the request to add trace headers for downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-B3-TraceId", traceId)
                .header("X-B3-SpanId", spanId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnTerminate(MDC::clear);
    }
}
