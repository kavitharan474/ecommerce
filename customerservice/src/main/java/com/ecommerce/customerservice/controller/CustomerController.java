package com.ecommerce.customerservice.controller;

import com.ecommerce.customerservice.entity.Customer;


import com.ecommerce.customerservice.service.CustomerService;
import com.ecommerce.generalservice.dto.ProductDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private static final Logger requestLogger = LoggerFactory.getLogger("customerRequestLogger"); 
   
    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
        
    }
    @GetMapping("/retrieve")
    public Mono<List<Customer>> getAllCustomers() {
        return Mono.fromSupplier(() -> customerService.getAllCustomers());
    }

    @GetMapping("/retrieve/{id}")
    public Mono<Customer> getCustomerById(@PathVariable("id") Integer id) {
        return Mono.fromSupplier(() -> customerService.getCustomerById(id));
    }

    @GetMapping("/{productId}")
    public Mono<ProductDTO> getProductById(@PathVariable("productId") final Integer productId) {
    	requestLogger.info("Received request: GET /customers/{}", productId);
    	return customerService.fetchProductById(productId);
    }



}
