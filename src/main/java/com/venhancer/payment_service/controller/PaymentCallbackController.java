package com.venhancer.payment_service.controller;

import com.venhancer.payment_service.entity.Transaction;
import com.venhancer.payment_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final TransactionService transactionService;

    @PostMapping("/return")
    public ResponseEntity<String> paymentReturn(@RequestParam Map<String, String> payload) {

        Transaction savedTx = transactionService.saveFromCallback(payload);

        return ResponseEntity.ok("Payment Successful - Transaction ID: " + savedTx.getId());
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> paymentCancel(@RequestParam Map<String, String> payload) {

        Transaction savedTx = transactionService.saveFromCallback(payload);

        return ResponseEntity.ok("Payment Cancelled - Transaction ID: " + savedTx.getId());
    }
}
