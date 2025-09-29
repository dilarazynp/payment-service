package com.venhancer.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponseDTO {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_description")
    private String statusDescription;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private String token;
        @JsonProperty("is_3d")
        private int is3D;
        @JsonProperty("expires_at")
        private String expiresAt;

    }

}
