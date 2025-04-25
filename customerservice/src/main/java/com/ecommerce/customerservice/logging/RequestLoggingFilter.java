package com.ecommerce.customerservice.logging;

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

import java.util.Optional;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Extract traceId and spanId from headers or generate new ones
        String traceId = Optional.ofNullable(request.getHeaders().getFirst("X-B3-TraceId"))
                .orElse(UUID.randomUUID().toString());
        String spanId = Optional.ofNullable(request.getHeaders().getFirst("X-B3-SpanId"))
                .orElse(UUID.randomUUID().toString());

        // Log the request details
        logger.info("Inside RequestLoggingFilter - TraceId: {}, SpanId: {}, IP: {}, Method: {}, Path: {}, Headers: {}",
                traceId,
                spanId,
                request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown",
                request.getMethod(),
                request.getURI().getPath(),
                request.getHeaders());

        // Put traceId and spanId in MDC for downstream logging
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        // Proceed with the next filter in the chain
        return chain.filter(exchange)
                .doOnTerminate(MDC::clear);  // Clear MDC after request is handled
    }
}
