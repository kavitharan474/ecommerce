package com.ecommerce.productservice.config;

import java.util.UUID;


import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .filter((request, next) -> {
                String traceId = MDC.get("traceId");
                String spanId = UUID.randomUUID().toString();

                ClientRequest filteredRequest = ClientRequest.from(request)
                        .header("X-B3-TraceId", traceId)
                        .header("X-B3-SpanId", spanId)
                        .build();

                return next.exchange(filteredRequest);
            });
    }
}
