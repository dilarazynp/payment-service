package com.venhancer.payment_service.util;

import com.venhancer.payment_service.dto.PaymentRequestDTO;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PaymentFormUtil {

    public static MultiValueMap<String, String> toForm(PaymentRequestDTO dto,
                                                       String merchantKey,
                                                       String total,
                                                       int installment,
                                                       String hashKey,
                                                       String cancelUrl,
                                                       String returnUrl) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("cc_holder_name", dto.getCc_holder_name());
        form.add("cc_no", dto.getCc_no());
        form.add("expiry_month", dto.getExpiry_month());
        form.add("expiry_year", dto.getExpiry_year());
        form.add("cvv", dto.getCvv());

        form.add("currency_code", dto.getCurrency_code() != null ? dto.getCurrency_code() : "TRY");
        form.add("installments_number", String.valueOf(installment));
        form.add("invoice_id", dto.getInvoice_id());
        form.add("invoice_description", dto.getInvoice_description());
        form.add("name", dto.getName());
        form.add("surname", dto.getSurname());
        form.add("total", total);

        form.add("items", "[{\"name\":\"Ürün 1\",\"price\":\"" + total + "\",\"quantity\":1}]");

        form.add("cancel_url", cancelUrl);
        form.add("return_url", returnUrl);
        form.add("merchant_key", merchantKey);
        form.add("hash_key", hashKey);

        form.add("bill_email", dto.getBill_email());
        form.add("bill_phone", dto.getBill_phone());
        form.add("response_method", dto.getResponse_method());

        return form;
    }
}
