package com.ecommerce.commonservice.service;

import com.ecommerce.commonservice.dto.CustomerDTO;
import com.ecommerce.commonservice.dto.ProductDTO;
import com.ecommerce.commonservice.dto.CustomerProductResponseDTO;
import com.ecommerce.commonservice.exception.EmptyInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CommonService {

    private final WebClient.Builder webClientBuilder;
    private static final Logger log = LoggerFactory.getLogger("commonRequestLogger");

    public CommonService(final WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public Mono<CustomerDTO> getCustomerById(final Integer id) {
        validateId(id);
        log.info("Fetching customer with ID: {}", id);

        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/retrieve/" + id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);
    }

    public Mono<ProductDTO> getProductById(final Integer id) {
        validateId(id);
        log.info("Fetching product with ID: {}", id);

        return webClientBuilder.build()
                .get()
                .uri(productServiceUrl + "/products/retrieve/" + id)
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }

    public Flux<ProductDTO> getAllProducts() {  
        log.info("Fetching all products...");

        return webClientBuilder.build()
                .get()
                .uri(productServiceUrl + "/products/retrieve")
                .retrieve()
                .bodyToFlux(ProductDTO.class);
    }

    public Mono<CustomerProductResponseDTO> getCustomerWithProducts(Integer id) {
        validateId(id);
        
        Mono<CustomerDTO> customer = getCustomerById(id);
        Flux<ProductDTO> products = getAllProducts();

        return Mono.zip(customer, products.collectList())
                .map(tuple -> new CustomerProductResponseDTO(tuple.getT1(), tuple.getT2()));
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            log.warn("Invalid ID provided: {}", id);
            throw new EmptyInputException("ID cannot be null or negative " + id);
        }
    }
}
