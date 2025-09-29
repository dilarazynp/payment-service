package com.venhancer.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequestDTO {
    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("app_secret")
    private String appSecret;
}