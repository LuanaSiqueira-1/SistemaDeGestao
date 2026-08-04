package com.concessionaria.backend.dto;

import com.concessionaria.backend.model.Role;

public record RegisterResponse(
        Long id,
        String nome,
        String email,
        Role role
) {
}