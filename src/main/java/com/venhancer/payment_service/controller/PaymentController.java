package com.venhancer.payment_service.controller;


import com.venhancer.payment_service.dto.PaymentRequestDTO;
import com.venhancer.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/directPayment")
    public ResponseEntity<String> directPayment(@RequestBody PaymentRequestDTO dto) {
        String htmlForm = paymentService.doDirectPayment(dto);
        return ResponseEntity.ok()
                .header("Content-Type", MediaType.TEXT_HTML_VALUE)
                .body(htmlForm);
    }

}
