package com.venhancer.payment_service.exception;

public class PaymentRequestException extends RuntimeException {
    public PaymentRequestException(String message) {
        super(message);
    }
}
