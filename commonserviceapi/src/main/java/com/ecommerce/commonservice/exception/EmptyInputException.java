package com.ecommerce.commonservice.exception;

public class EmptyInputException extends RuntimeException {
        public EmptyInputException(String message) {
        	super(message);
        }
}
