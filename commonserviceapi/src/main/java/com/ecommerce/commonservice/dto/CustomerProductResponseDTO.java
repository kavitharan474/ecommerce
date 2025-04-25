package com.ecommerce.commonservice.dto;

import java.util.List;

public class CustomerProductResponseDTO {
    private CustomerDTO customer;
    private List<ProductDTO> products;
	public CustomerProductResponseDTO(CustomerDTO t1, List<ProductDTO> t2) {
		this.customer=t1;
		this.products=t2;
	}
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	public List<ProductDTO> getProducts() {
		return products;
	}
	public void setProducts(List<ProductDTO> products) {
		this.products = products;
	}
    
    
}