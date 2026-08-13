package com.concessionaria.backend.dto;

public record ClienteResponse(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email
) {
}
