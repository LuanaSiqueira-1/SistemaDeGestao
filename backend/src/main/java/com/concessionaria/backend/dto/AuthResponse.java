package com.concessionaria.backend.dto;

import com.concessionaria.backend.model.Role;

public record AuthResponse(
        String token,
        String nome,
        String email,
        Role role
) {
}