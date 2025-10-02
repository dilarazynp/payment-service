package com.venhancer.payment_service.service;

import com.venhancer.payment_service.dto.PaymentRequestDTO;

public interface PaymentService {

    String doDirectPayment(PaymentRequestDTO dto);

}
