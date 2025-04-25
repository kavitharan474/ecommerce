package com.ecommerce.commonservice.controller;

import com.ecommerce.commonservice.dto.CustomerDTO;
import com.ecommerce.commonservice.dto.ProductDTO;
import com.ecommerce.commonservice.dto.CustomerProductResponseDTO;
import com.ecommerce.commonservice.service.CommonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/common")  
public class CommonController {

    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("/customer/{id}")
    public Mono<CustomerDTO> getCustomerById(@PathVariable Integer id) {
        return commonService.getCustomerById(id);
    }

    @GetMapping("/product/{id}")
    public Mono<ProductDTO> getProductById(@PathVariable Integer id) {
        return commonService.getProductById(id);
    }

    @GetMapping("/products")
    public Flux<ProductDTO> getAllProducts() {  
        return commonService.getAllProducts();
    }

    @GetMapping("/customer/{id}/products")
    public Mono<CustomerProductResponseDTO> getCustomerWithProducts(@PathVariable Integer id) {
        return commonService.getCustomerWithProducts(id);
    }
}
