package com.concessionaria.backend.dto;

public record ClienteDetalheResponse(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email
) {
}