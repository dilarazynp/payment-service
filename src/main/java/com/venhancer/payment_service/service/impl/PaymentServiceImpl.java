package com.venhancer.payment_service.service.impl;

import com.venhancer.payment_service.dto.PaymentRequestDTO;
import com.venhancer.payment_service.exception.PaymentRequestException;
import com.venhancer.payment_service.service.PaymentService;
import com.venhancer.payment_service.service.TokenService;
import com.venhancer.payment_service.util.PaymentFormUtil;
import com.venhancer.payment_service.util.SipayHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final WebClient webClient;
    private final TokenService tokenService;

    @Value("${sipay.api.base-url}")
    private String baseUrl;

    @Value("${sipay.api.pay-smart3d}")
    private String paySmart3dEndpoint;

    @Value("${sipay.return-url}")
    private String returnUrl;


    @Value("${sipay.cancel-url}")
    private String cancelUrl;

    @Value("${sipay.merchant.key}")
    private String merchantKey;

    @Value("${sipay.appSecret}")
    private String appSecret;

    private static String normalizeAmount(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replace(',', '.');
    }

    private static String prepareInvoiceId(String providedId) {
        return Optional.ofNullable(providedId)
                .filter(id -> !id.isBlank())
                .orElseGet(() -> String.format("INV-%d", System.currentTimeMillis()));
    }

    @Override
    public String doDirectPayment(PaymentRequestDTO request) {

        String token = tokenService.getTokenFromSipay();
        if (token == null || token.isBlank()) {
            throw new PaymentRequestException("Failed to obtain Sipay token");
        }

        String total = normalizeAmount(
                request.getTotal() != null ? request.getTotal() : BigDecimal.ONE
        );
        int installments = request.getInstallments_number();
        String invoiceId = prepareInvoiceId(request.getInvoice_id());


        String hashKey = SipayHashUtil.generateHashKey(
                total,
                String.valueOf(installments),
                Optional.ofNullable(request.getCurrency_code()).orElse("TRY"),
                merchantKey,
                invoiceId,
                appSecret
        );

        MultiValueMap<String, String> formData =
                PaymentFormUtil.toForm(request, merchantKey, total, installments, hashKey, cancelUrl, returnUrl);


        return webClient.post()
                .uri(baseUrl + paySmart3dEndpoint)
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_HTML)
                .bodyValue(formData)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> new PaymentRequestException(
                                        "paySmart3D request failed " + resp.statusCode() + " / " + body))
                )
                .bodyToMono(String.class)
                .block();
    }
}
