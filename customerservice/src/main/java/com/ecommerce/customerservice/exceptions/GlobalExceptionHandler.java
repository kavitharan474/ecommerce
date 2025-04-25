package com.ecommerce.customerservice.exceptions;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.*;

import com.ecommerce.generalservice.exceptions.EmptyInputException;
import com.ecommerce.generalservice.exceptions.ResourceNotFoundException;
import com.ecommerce.generalservice.responses.ErrorResponse;

import java.net.ConnectException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getTraceId() {
        return MDC.get("traceId");
    }

    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {} | traceId={}", ex.getMessage(), getTraceId());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(EmptyInputException.class)
    public ResponseEntity<ErrorResponse> handleEmptyInputException(EmptyInputException ex) {
        logger.warn("Empty input received: {} | traceId={}", ex.getMessage(), getTraceId());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

  
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException ex) {
        logger.error("External service error [{}]: {} | traceId={}",
                ex.getRawStatusCode(), ex.getMessage(), getTraceId());

        ErrorResponse errorResponse = new ErrorResponse(
                "External service error: " + ex.getStatusText() + ". Please check the URL or ID.",
                ex.getRawStatusCode(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

   
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorResponse> handleWebClientRequestException(WebClientRequestException ex) {
        if (ex.getCause() instanceof ConnectException) {
            logger.error("Product Service is down from Customer Service: {} | traceId={}", ex.getMessage(), getTraceId());

            ErrorResponse errorResponse = new ErrorResponse(
                    "Customer service is currently unavailable.",
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.error("WebClient request failure from Customer Service: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Communication error with external service: " + ex.getMessage(),
                HttpStatus.BAD_GATEWAY.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected exception in Customer Service: {} | traceId={}", ex.getMessage(), getTraceId(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

