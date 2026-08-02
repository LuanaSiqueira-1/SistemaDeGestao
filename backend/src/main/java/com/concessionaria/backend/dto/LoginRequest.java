package com.concessionaria.backend.dto;

public record LoginRequest(
        String email,
        String senha
) {
}