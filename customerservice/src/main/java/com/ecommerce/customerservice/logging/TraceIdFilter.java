package com.ecommerce.customerservice.logging;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class TraceIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Get traceId and spanId from the request headers
        String traceId = exchange.getRequest().getHeaders().getFirst("X-B3-TraceId");
        String spanId = exchange.getRequest().getHeaders().getFirst("X-B3-SpanId");
        
        // Default values if not found in headers
        if (traceId == null) {
            traceId = "no-trace-id";
        }
        if (spanId == null) {
            spanId = "no-span-id";
        }

        // Add traceId and spanId to MDC
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        // Proceed with the request processing
        return chain.filter(exchange)
                    .doOnTerminate(MDC::clear); // Clean up the MDC after processing the request
    }
}
