package com.realestate.billing.exception;

import com.realestate.common.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BillingExceptionHandler extends GlobalExceptionHandler {
    // Inherits all exception handling from GlobalExceptionHandler
    // Can add service-specific exception handlers here if needed
}

