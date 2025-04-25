package com.ecommerce.commonservice.util;

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
                // Retrieve traceId from MDC or generate a new one if not available
                String traceId = MDC.get("traceId");
                if (traceId == null) {
                    traceId = UUID.randomUUID().toString();  // Generate a new traceId if not present
                    MDC.put("traceId", traceId); // Optionally put it in MDC for the current request
                }
                String spanId = UUID.randomUUID().toString(); // Generate a new spanId

                // Build the ClientRequest with trace and span IDs
                ClientRequest filteredRequest = ClientRequest.from(request)
                        .header("X-B3-TraceId", traceId)
                        .header("X-B3-SpanId", spanId)
                        .build();

                return next.exchange(filteredRequest);
            });
    }
}
