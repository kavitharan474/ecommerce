package com.ecommerce.customerservice.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(traceIdPropagatingFilter());
    }

    private ExchangeFilterFunction traceIdPropagatingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(originalRequest -> {
            String traceId = MDC.get("traceId");
            String spanId = MDC.get("spanId");

            ClientRequest.Builder requestBuilder = ClientRequest.from(originalRequest);

            if (traceId != null) {
                requestBuilder.header("X-B3-TraceId", traceId);
            }
            if (spanId != null) {
                requestBuilder.header("X-B3-SpanId", spanId);
            }

            return Mono.just(requestBuilder.build());
        });
    }
}
