package com.venhancer.payment_service.dto;

import lombok.Data;

@Data
public class PaymentCallbackDTO {
    private String order_id;
    private String status;
    private String amount;
    private String currency;
    private String transaction_id;
}
