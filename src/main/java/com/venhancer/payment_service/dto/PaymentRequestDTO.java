package com.venhancer.payment_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String cc_holder_name;
    private String cc_no;
    private String expiry_month;
    private String expiry_year;
    private String cvv;
    private String currency_code;
    private Integer installments_number;
    private String invoice_id;
    private String invoice_description;
    private String name;
    private String surname;
    private BigDecimal total;
    private String items;
    private String bill_email;
    private String bill_phone;
    private String return_url;
    private String cancel_url;
    private String response_method = "POST";
}
