package com.ecommerce.productservice.controller;

import com.ecommerce.generalservice.dto.CustomerDTO;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private static final Logger requestLogger = LoggerFactory.getLogger("ProductRequestLogger");

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/retrieve")
    public List<Product> getAllProducts() {
        requestLogger.info("Received request: GET /products/retrieve");
        return productService.getAllProducts();
    }

    @GetMapping("/retrieve/{id}")
    public Product getProductById(@PathVariable("id") final Integer id) {
        requestLogger.info("Received request: GET /products/retrieve/{}", id);
        return productService.getProductById(id);
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerDTO> getCustomerById(@PathVariable("customerId") final Integer customerId) {
        requestLogger.info("Received request: GET /products/{}", customerId);
        return productService.fetchCustomerDetails(customerId);
    }
}
