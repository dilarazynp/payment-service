package com.venhancer.payment_service.service.impl;

import com.venhancer.payment_service.entity.Transaction;
import com.venhancer.payment_service.repository.TransactionRepository;
import com.venhancer.payment_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction saveFromCallback(Map<String, String> payload) {
        Objects.requireNonNull(payload, "payload cannot be null");
        log.info("Saving transaction from callback payload keys: {}", payload.keySet());

        try {
            BigDecimal amount = parseAmount(payload.get("amount"));
            String orderId = safe(payload.get("order_id"));
            String orderNo = safe(payload.get("order_no"));
            String invoiceId = safe(payload.get("invoice_id"));
            String currency = safe(payload.get("currency"));
            String mdStatus = safe(payload.get("md_status"));
            String status = mdStatus != null ? mdStatus : safe(payload.get("error"));

            String errorCode = safe(payload.get("error_code"));
            String errorMessage = firstNonEmpty(
                    payload.get("original_bank_error_description"),
                    payload.get("payment_reason_code_detail"),
                    payload.get("error")
            );

            String authCode = safe(payload.get("auth_code"));
            String cardMasked = safe(payload.get("credit_card_no"));

            Transaction tx = Transaction.builder()
                    .orderId(orderId != null ? orderId : generateFallbackOrderId())
                    .orderNo(orderNo)
                    .invoiceId(invoiceId)
                    .amount(amount)
                    .currency(currency)
                    .status(status)
                    .errorCode(errorCode)
                    .errorMessage(errorMessage)
                    .authCode(authCode)
                    .cardNumberMasked(cardMasked)
                    .build();

            Transaction saved = transactionRepository.save(tx);
            log.info("Transaction saved id={} orderId={} amount={}", saved.getId(), saved.getOrderId(), saved.getAmount());
            return saved;
        } catch (Exception exception) {
            log.error("Failed to save transaction from callback", exception);
            throw new RuntimeException("Failed to save transaction", exception);
        }
    }

    private BigDecimal parseAmount(String amountStr) {
        if (amountStr == null) return BigDecimal.ZERO;
        String trimmed = amountStr.trim();
        if (trimmed.isEmpty()) return BigDecimal.ZERO;
        trimmed = trimmed.replace(',', '.');
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException invalidNumber) {
            log.warn("Unable to parse amount '{}', defaulting to 0", amountStr);
            return BigDecimal.ZERO;
        }
    }

    private String safe(String input) {
        if (input == null) return null;
        String t = input.trim();
        return t.isEmpty() ? null : t;
    }

    private String firstNonEmpty(String... candidates) {
        if (candidates == null) return null;
        for (String c : candidates) {
            if (c != null && !c.isBlank()) return c.trim();
        }
        return null;
    }

    private String generateFallbackOrderId() {
        return "gen-" + System.currentTimeMillis();
    }
}
