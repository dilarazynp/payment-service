package com.venhancer.payment_service.service;

import java.util.Map;
import com.venhancer.payment_service.entity.Transaction;

public interface TransactionService {
    Transaction saveFromCallback(Map<String, String> payload);
}
