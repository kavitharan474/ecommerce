package com.ecommerce.customerservice.service;


import com.ecommerce.customerservice.entity.Customer;


import com.ecommerce.customerservice.repository.CustomerRepository;
import com.ecommerce.generalservice.dto.ProductDTO;
import com.ecommerce.generalservice.exceptions.EmptyInputException;
import com.ecommerce.generalservice.exceptions.ResourceNotFoundException;
import com.ecommerce.generalservice.util.ServiceUrls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WebClient webClient;
    

    private static final Logger log = LoggerFactory.getLogger("customerRequestLogger"); 
    private static final Logger externalLogger = LoggerFactory.getLogger("externalLogger"); 


    public CustomerService(final CustomerRepository customerRepository, WebClient.Builder webClientBuilder) {
        this.customerRepository = customerRepository;
        this.webClient = webClientBuilder.build();
    }

    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }

    public Customer getCustomerById(final Integer id) {
        log.info("Fetching customer with ID: {}", id);
        validateId(id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }
  
    public Mono<ProductDTO> fetchProductById(final Integer productId) {
    	log.info("Fetching product  details for Product with ID: {}", productId);
        validateId(productId);
        externalLogger.info("Calling external product service at: {}{}", ServiceUrls.PRODUCT_RETRIEVE_URL, productId);

        return webClient.get()
                .uri(ServiceUrls.PRODUCT_RETRIEVE_URL + productId)
                .retrieve()
                .bodyToMono(ProductDTO.class);

    }
    
    private void validateId(final Integer id) {
        if (id == null || id <= 0) {
            log.error("Invalid ID provided: {}", id);  
            throw new EmptyInputException("ID cannot be null or negative");
        }
    }

    }	
