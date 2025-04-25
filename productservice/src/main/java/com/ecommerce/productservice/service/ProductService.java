package com.ecommerce.productservice.service;

import com.ecommerce.generalservice.dto.CustomerDTO;
import com.ecommerce.generalservice.exceptions.EmptyInputException;
import com.ecommerce.generalservice.exceptions.ResourceNotFoundException;
import com.ecommerce.generalservice.util.ServiceUrls;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger("productRequestLogger"); 
    private static final Logger externalLogger = LoggerFactory.getLogger("externalLogger"); 

    public ProductService(final ProductRepository productRepository, WebClient.Builder webClientBuilder) {
        this.productRepository = productRepository;
        this.webClient = webClientBuilder.build();
    }

    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    public Product getProductById(final Integer id) {
        log.info("Fetching product with ID: {}", id);
        validateId(id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public Mono<CustomerDTO> fetchCustomerDetails(final Integer id) {
        log.info("Fetching customer details for customer with ID: {}", id);
        validateId(id);
        externalLogger.info("Calling external customer service at: {}{}", ServiceUrls.CUSTOMER_RETRIEVE_URL, id);

        return webClient.get()
                .uri(ServiceUrls.CUSTOMER_RETRIEVE_URL + id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);
    }

    private void validateId(final Integer id) {
        if (id == null || id <= 0) {
        	log.warn("Invalid ID provided: {}", id);
            throw new EmptyInputException("ID cannot be null or negative");
        }
    }
}
