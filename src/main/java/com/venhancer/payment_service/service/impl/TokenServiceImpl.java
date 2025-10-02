package com.venhancer.payment_service.service.impl;

import com.venhancer.payment_service.dto.TokenResponseDTO;
import com.venhancer.payment_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final WebClient webClient;

    @Value("${sipay.appId}")
    private String appId;

    @Value("${sipay.appSecret}")
    private String appSecret;

    @Value("${sipay.tokenUrl}")
    private String tokenUrl;

    @Override
    public String getTokenFromSipay() {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("app_id", appId);
        formData.add("app_secret", appSecret);


        TokenResponseDTO tokenResponse = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .block();

        if (tokenResponse != null && tokenResponse.getData() != null) {
            log.info("Sipay Token Response: {}", tokenResponse);
            return tokenResponse.getData().getToken();
        } else {
            log.error("Token Request Failed: {}", tokenResponse);
            return null;
        }
    }
}
