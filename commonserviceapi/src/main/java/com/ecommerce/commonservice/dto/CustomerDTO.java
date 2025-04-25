package com.ecommerce.commonservice.dto;




public class CustomerDTO {
    private Integer id;
    private String name;
    private String email;
    
	
	public CustomerDTO(Integer customerId, String name, String email) {
		super();
		this.id = customerId;
		this.name = name;
		this.email = email;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    
}


