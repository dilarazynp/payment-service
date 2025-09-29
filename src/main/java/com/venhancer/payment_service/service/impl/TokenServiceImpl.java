package com.venhancer.payment_service.service.impl;

import com.venhancer.payment_service.dto.TokenRequestDTO;
import com.venhancer.payment_service.dto.TokenResponseDTO;
import com.venhancer.payment_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final RestTemplate restTemplate;

    @Value("${sipay.appId}")
    private String appId;

    @Value("${sipay.appSecret}")
    private String appSecret;

    @Value("${sipay.tokenUrl}")
    private String tokenUrl;

    @Override
    public String getTokenFromSipay() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TokenRequestDTO dto = new TokenRequestDTO(appId, appSecret);
        HttpEntity<TokenRequestDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<TokenResponseDTO> response =
                restTemplate.postForEntity(tokenUrl, request, TokenResponseDTO.class);

        TokenResponseDTO body = response.getBody();

        if(body != null && body.getData() != null) {
            log.info("Sipay Token Response: {}", body);
            return body.getData().getToken();
        }
        else{
            log.error("Token Request Failed: {}",body);
            return null;
        }
    }
}

