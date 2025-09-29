package com.venhancer.payment_service.controller;

import com.venhancer.payment_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping ("/test")
    public ResponseEntity<String> testToken() {
        String response = tokenService.getTokenFromSipay();
        System.out.println("Sipay Token Response: " + response);
        return ResponseEntity.ok("200 OK");
    }
}