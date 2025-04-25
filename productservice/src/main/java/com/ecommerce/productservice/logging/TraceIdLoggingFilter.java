package com.ecommerce.productservice.logging;


import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
public class TraceIdLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-B3-TraceId"))
                .orElse(UUID.randomUUID().toString());

        String spanId = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-B3-SpanId"))
                .orElse(UUID.randomUUID().toString());

        String clientIp = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .orElse("unknown");

        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("ip", clientIp);

        return chain.filter(exchange)
                .doOnTerminate(MDC::clear);
    }
}
