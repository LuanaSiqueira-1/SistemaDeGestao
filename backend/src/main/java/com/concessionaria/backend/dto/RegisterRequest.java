package com.concessionaria.backend.dto;

import com.concessionaria.backend.model.Role;

public record RegisterRequest(
        String nome,
        String email,
        String senha,
        Role role
) {
}